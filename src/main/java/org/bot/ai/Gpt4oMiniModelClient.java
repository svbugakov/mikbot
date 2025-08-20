package org.bot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.bot.ResponseAI;
import org.bot.ai.function.AIFunction;
import org.bot.ai.function.AIFunctionManager;
import org.bot.ai.function.meteosource.WeatherArgs;
import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.WeatherPlace;
import org.bot.ai.function.openai.OpenAIFunctionWeather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Gpt4oMiniModelClient extends AbstractGtp4oMini {

    private static final Logger logger = LoggerFactory.getLogger(Gpt4oMiniModelClient.class);
    final static int SERVER_ERROR = 500;
    final static int SERVER_OK = 200;
    final static int MAX_MESSAGES = 200;

    private List<ChatMessage> baseMessages = new ArrayList<>();
    private List<AIFunction> aiFunctions = new ArrayList<>();
    private List<ChatMessage> allMessages;
    final OpenAiService service;

    public Gpt4oMiniModelClient(
            String apiKey,
            AIFunctionManager aiFunctionManager
    ) {
        super(apiKey);
        service = new OpenAiService(getApiKey());
        aiFunctionManager.getAiFunctions()
                .forEach(fun -> {
                            baseMessages.addAll(fun.getTestMessages());
                            aiFunctions.add(fun);
                        }
                );
        allMessages = new ArrayList<>(baseMessages);
    }

    @Override
    public ResponseAI getResponse(String question) {
        logicRecreationAllMessages();

        ChatMessage userMessage = new ChatMessage(
                ChatMessageRole.USER.value(),
                question
        );

        allMessages.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(getModel())
                .messages(allMessages)
                .functions(aiFunctions.stream().map(AIFunction::getFunc).toList())
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .build();

        // 4. Отправляем запрос
        ChatMessage response = service.createChatCompletion(request)
                .getChoices().getFirst().getMessage();

        allMessages.add(response);
        ChatFunctionCall chatFunctionCall = response.getFunctionCall();
        if (chatFunctionCall != null) {
            Optional<AIFunction> aiFunctionOpt = aiFunctions.stream()
                    .filter(func -> chatFunctionCall.getName().equals(func.getName()))
                    .findFirst();
            if (aiFunctionOpt.isPresent()) {
                return aiFuncLogic(chatFunctionCall, aiFunctionOpt.get(), response);
            }
        }

        return new ResponseAI(response.getContent(), SERVER_OK);
    }

    @Override
    public String getName() {
        return "gpt-4o-mini-model";
    }

    @Override
    public String getRequestModel() {
        throw new UnsupportedOperationException("not to use getRequestModel for Gpt4oMiniModelClient");
    }

    @Override
    public String getUri() {
        throw new UnsupportedOperationException("not to use getUri for Gpt4oMiniModelClient");
    }

    private ResponseAI aiFuncLogic(
            final ChatFunctionCall chatFunctionCall,
            final AIFunction aiFunction,
            final ChatMessage response
    ) {

        JsonNode jsonNode = chatFunctionCall.getArguments();
        WeatherArgs weatherArgs = new WeatherArgs(
                WeatherPlace.valueOf(jsonNode.get("location").textValue()),
                WeatherDay.valueOf(jsonNode.get("type").asText()),
                jsonNode.get("days").asInt()
        );

        String responseWeather = null;
        try {
            responseWeather = aiFunction.logic(
                    weatherArgs
            );
        } catch (Exception e) {
            logger.error("error in call aiFunction.logic", e);
            return new ResponseAI(response.getContent(), SERVER_ERROR);
        }

        ChatMessage functionResponse = new ChatMessage(
                ChatMessageRole.FUNCTION.value(),
                responseWeather,
                response.getFunctionCall().getName()
        );
        allMessages.add(functionResponse);

        ChatCompletionRequest followUp = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(allMessages)
                .functions(List.of(aiFunction.getFunc()))
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .build();

        ChatMessage finMessage = service.createChatCompletion(followUp)
                .getChoices().getFirst().getMessage();

        logger.info("Response after func:{}", finMessage);

        allMessages.add(finMessage);

        return new ResponseAI(finMessage.getContent(), SERVER_OK);
    }

    private void logicRecreationAllMessages() {
        if (allMessages.size() > MAX_MESSAGES) {
            synchronized (this) {
                if (allMessages.size() > MAX_MESSAGES) {
                    logger.info("recreation messages in chat...");
                    List<ChatMessage> newMessages = new ArrayList<>(baseMessages);
                    newMessages.addAll(allMessages.subList(
                            Math.max(0, allMessages.size() - 10),
                            allMessages.size()
                    ));
                    allMessages = newMessages;
                }
            }
        }
    }
}
