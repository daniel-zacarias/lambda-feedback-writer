package com.fiap.lambda.feedback.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.lambda.feedback.dto.FeedbackDTO;
import com.fiap.lambda.feedback.model.FeedbackEntity;
import com.fiap.lambda.feedback.repository.FeedbackRepository;
import com.fiap.lambda.feedback.repository.IFeedbackRepository;

import java.util.UUID;

public class FeedbackWriterHandler implements RequestHandler<SQSEvent, Void>{

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IFeedbackRepository feedbackRepository;

    public FeedbackWriterHandler() {
        String tableName = System.getenv("FEEDBACK_TABLE_NAME");
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Variável de ambiente FEEDBACK_TABLE_NAME não configurada.");
        }
        this.feedbackRepository = new FeedbackRepository(tableName);
    }

    // construtor só para teste
    FeedbackWriterHandler(IFeedbackRepository  feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        if (event == null || event.getRecords() == null || event.getRecords().isEmpty()) {
            System.out.println("Nenhum registro recebido no evento SQS.");
            return null;
        }

        event.getRecords().forEach(sqsMessage -> {
            try {
                String body = sqsMessage.getBody();

                JsonNode snsEnvelope = objectMapper.readTree(body);
                String snsTimestamp = snsEnvelope.get("Timestamp").asText();
                String feedbackJson = snsEnvelope.get("Message").asText();

                JsonNode snsMessage = objectMapper.readTree(feedbackJson);
                FeedbackDTO feedback = objectMapper.treeToValue(snsMessage, FeedbackDTO.class);

                System.out.printf(
                        "Feedback recebido: descricao='%s', nota=%d%n",
                        feedback.getDescricao(),
                        feedback.getNota()
                );
                System.out.println("Timestamp SNS: " + snsTimestamp);

                FeedbackEntity entity = toEntity(feedback, snsTimestamp);

                feedbackRepository.salvar(entity);
                System.out.println("Salvo no DynamoDB: id=" + entity.getId());

            } catch (Exception e) {
                System.err.println("Erro ao processar mensagem SQS: " + e.getMessage());
                e.printStackTrace();
            }
        });

        return null;
    }

    private FeedbackEntity toEntity(FeedbackDTO dto, String timestampSns) {
        FeedbackEntity entity = new FeedbackEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setDescricao(dto.getDescricao());
        entity.setNota(dto.getNota());
        entity.setTimestamp(timestampSns);
        return entity;
    }
}
