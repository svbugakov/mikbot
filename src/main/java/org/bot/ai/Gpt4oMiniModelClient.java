package org.bot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.bot.ai.function.AIFunction;
import org.bot.ai.function.AIFunctionManager;
import org.bot.ai.function.TypeAI;
import org.bot.ai.function.openai.OpenAIWeatherFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Gpt4oMiniModelClient extends AbstractAICommon<ChatMessage> {

    private static final Logger logger = LoggerFactory.getLogger(Gpt4oMiniModelClient.class);
    public static final String NAME_AI = "gpt-4o-mini-model";

    private List<ChatMessage> baseMessages = new ArrayList<>();
    private List<AIFunction> aiFunctions = new ArrayList<>();
    private List<ChatMessage> allMessages;
    final OpenAiService service;
    private String apiKey;

    public Gpt4oMiniModelClient(
            String apiKey,
            AIFunctionManager aiFunctionManager
    ) {
        this.apiKey = apiKey;
        service = new OpenAiService(getApiKey());
        aiFunctionManager.getAiFunctions().stream()
                .filter(fun -> fun instanceof OpenAIWeatherFunction)
                .forEach(fun -> {
                            baseMessages.addAll(fun.getTestMessages());
                            aiFunctions.add(fun);
                        }
                );
        allMessages = new ArrayList<>(baseMessages);
    }

    @Override
    public ResponseAI getResponse(Question question) {
        final ResponseAI responseAIR = tryNeedRedirect(question);
        if (responseAIR.getRedirectAiName() != null) {
            logger.info("redirect question:{}", question.getMessage());
            return responseAIR;
        }
        logicRecreationAllMessages();

        ChatMessage userMessage = new ChatMessage(
                ChatMessageRole.USER.value(),
                question.getMessage()
        );

        allMessages.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(getModel())
                .messages(allMessages)
                .functions(aiFunctions.stream().map(f -> (ChatFunction) f.getFunc()).toList())
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .build();
        ChatMessage response;
        // 4. Отправляем запрос
        try {
            response = service.createChatCompletion(request)
                    .getChoices().getFirst().getMessage();
        } catch (Exception ex) {
            logger.error("getResponse error: ", ex);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }

        allMessages.add(response);
        ChatFunctionCall chatFunctionCall = response.getFunctionCall();
        ResponseAI responseAI = new ResponseAI(response.getContent(), StatusResponse.SUCCESS, QuestionGoal.TEXT);

        if (chatFunctionCall == null) {
            return responseAI;
        }
        Optional<AIFunction> aiFunctionOpt = aiFunctions.stream()
                .filter(func -> func.getTypeAi() == TypeAI.GPT)
                .filter(func -> chatFunctionCall.getName().equals(func.getName()))
                .findFirst();
        if (aiFunctionOpt.isEmpty()) {
            throw new RuntimeException("Not found function %s".formatted(chatFunctionCall.getName()));
        }
        final String responseWeather;
        try {
            JsonNode jsonNode = chatFunctionCall.getArguments();
            Map<String, Object> mArgs = new HashMap<>();
            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                mArgs.put(fieldName, jsonNode.get(fieldName).asText());
            }

            responseWeather =
                    aiFunctionOpt.get().logic(mArgs);
        } catch (retrofit2.adapter.rxjava2.HttpException ex) {
            logger.error("getResponse error: ", ex);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }

        ChatMessage functionResponse = new ChatMessage(
                ChatMessageRole.FUNCTION.value(),
                responseWeather,
                response.getFunctionCall().getName()
        );
        allMessages.add(functionResponse);

        ChatCompletionRequest followUp = ChatCompletionRequest.builder()
                .model(getModel())
                .messages(allMessages)
                .functions(List.of((ChatFunction) aiFunctionOpt.get().getFunc()))
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .build();

        ChatMessage finMessage = service.createChatCompletion(followUp)
                .getChoices().getFirst().getMessage();

        logger.info("Response after func:{}", finMessage);

        allMessages.add(finMessage);

        return new ResponseAI(finMessage.getContent(), StatusResponse.SUCCESS, QuestionGoal.TEXT);
    }

    @Override
    public String getName() {
        return NAME_AI;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getModel() {
        return "gpt-4o-mini";
    }

    @Override
    public String getRequestModel() {
        throw new UnsupportedOperationException("not to use getRequestModel for Gpt4oMiniModelClient");
    }

    @Override
    public String getUri() {
        throw new UnsupportedOperationException("not to use getUri for Gpt4oMiniModelClient");
    }




}
