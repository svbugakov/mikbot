package org.bot.ai;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;

import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;


import chat.giga.model.completion.*;
import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.bot.ai.function.AIFunction;
import org.bot.ai.function.AIFunctionManager;
import org.bot.ai.function.TypeAI;
import org.bot.ai.function.giga.FuncParamGiga;
import org.bot.ai.function.giga.GigaWeatherFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class GigaModelClient extends AbstractAICommon<ChatMessage> {
    private static final Logger logger = LoggerFactory.getLogger(GigaModelClient.class);

    private List<AIFunction> aiFunctions = new ArrayList<>();

    private GigaChatClient client;
    private String apiKey;
    private List<ChatMessage> allMessages;

    public GigaModelClient(String token, AIFunctionManager aiFunctionManager) {
        this.apiKey = token;
        allMessages = new ArrayList<>();
        this.client = GigaChatClient.builder()
                .connectTimeout(60)
                .readTimeout(60)
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey(getApiKey())
                                .build())
                        .build())
                .build();
        aiFunctionManager.getAiFunctions().stream()
                .filter(fun -> fun instanceof GigaWeatherFunction)
                .forEach(fun -> {
                            //  allMessages.addAll(fun.getTestMessages());
                            aiFunctions.add(fun);
                        }
                );
    }

    @Override
    public ResponseAI getResponse(Question question) {
        logicRecreationAllMessages();
        if (question.getQuestionGoal() == QuestionGoal.PICTURE) {
            allMessages.clear();
            allMessages.add(ChatMessage.builder()
                    .role(ChatMessage.Role.SYSTEM)
                    .content("Ты — художник")
                    .build());
        }

        allMessages.add(ChatMessage.builder()
                .content(question.getMessage())
                .role(ChatMessage.Role.USER)
                .build());

        List<ChatFunction> funcGiga = new ArrayList<>();
        if (question.getQuestionGoal() == QuestionGoal.TEXT) {
            funcGiga.addAll(new ArrayList<>(aiFunctions.stream()
                    .filter(f -> f instanceof GigaWeatherFunction)
                    .map(f -> (ChatFunction) f.getFunc()).toList()));
        }


        CompletionResponse response;
        try {
            response = client.completions(CompletionRequest.builder()
                    .model(getModel())
                    .messages(allMessages)
                    .functions(funcGiga)
                    .build());
        } catch (HttpClientException ex) {
            logger.error("getResponse error: ", ex);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }


        var message = response.choices().getFirst().message();
        allMessages.add(ChatMessage.of(message));

        final var content = message.content();
        if (question.getQuestionGoal() == QuestionGoal.PICTURE) {
            return handleImageQuestion(content);
        }
        ResponseAI responseAI = new ResponseAI(content, StatusResponse.SUCCESS, QuestionGoal.TEXT);
        ChoiceMessageFunctionCall choiceMessageFunctionCall = message.functionCall();
        if (choiceMessageFunctionCall == null) {
            return responseAI;
        }

        Optional<AIFunction> aiFunctionOpt = aiFunctions.stream()
                .filter(func -> func.getTypeAi() == TypeAI.GIGA)
                .filter(func -> choiceMessageFunctionCall.name().equals(func.getName()))
                .findFirst();


        if (aiFunctionOpt.isEmpty()) {
            throw new RuntimeException("Not found function %s".formatted(choiceMessageFunctionCall.name()));
        }

        AIFunction<ChatFunction, ChatFunctionFewShotExample, FuncParamGiga> aiFunction =
                aiFunctionOpt.get();

        final String responseWeather;
        try {
            responseWeather = aiFunction.logic(choiceMessageFunctionCall.arguments());
        } catch (retrofit2.adapter.rxjava2.HttpException ex) {
            logger.error("getResponse error: ", ex);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }

        allMessages.add(ChatMessage.builder()
                .role(ChatMessage.Role.FUNCTION)
                .content(responseWeather)
                .name(getName())
                .build());

        response = client.completions(CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT_PRO)
                .messages(allMessages)
                .function(aiFunction.getFunc())
                .build());

        var messageRespFinish = response.choices().getFirst().message();

        return new ResponseAI(messageRespFinish.content(), StatusResponse.SUCCESS, QuestionGoal.TEXT);
    }

    @Override
    public String getName() {
        return "giga";
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getModel() {
        return ModelName.GIGA_CHAT_PRO;
    }

    @Override
    public String getRequestModel() {
        throw new UnsupportedOperationException("not to use getRequestModel for GigaModelClient");
    }

    @Override
    public String getUri() {
        throw new UnsupportedOperationException("not to use getUri for GigaModelClient");
    }

    @Override
    public List<ChatMessage> getAllMessage() {
        return allMessages;
    }

    public void setAllMessage(List<ChatMessage> messNew) {
        allMessages.addAll(messNew);
    }

    private ResponseAI handleImageQuestion(String content) {
        if (content == null || !content.contains("img src=")) {
            throw new RuntimeException("no img on answer GigaChat!");
        }
        var fileId = content.split("\"")[1];
        // Получаем информацию по сгенерированному файлу
        logger.info(client.getFileInfo(fileId).toString());

        byte[] bytes = client.downloadFile(fileId, null);
        ResponseAI responseAI = new ResponseAI(bytes, StatusResponse.SUCCESS, QuestionGoal.PICTURE);
        client.deleteFile(fileId);
        return responseAI;
    }
}
