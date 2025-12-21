package com.fiap.lambda.feedback.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.lambda.feedback.dto.FeedbackDTO;
import com.fiap.lambda.feedback.model.FeedbackEntity;
import com.fiap.lambda.feedback.repository.FeedbackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class FeedbackWriterHandler implements RequestHandler<SQSEvent, Void>{

    @Inject
    ObjectMapper objectMapper;

    @Inject
    FeedbackRepository feedbackRepository;

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        event.getRecords().forEach(sqsMessage -> {
            try {
                String body = sqsMessage.getBody();

                JsonNode snsEnvelope = objectMapper.readTree(body);

                String feedbackJson = snsEnvelope.get("Message").asText();

                FeedbackDTO feedback = objectMapper.readValue(feedbackJson, FeedbackDTO.class);

                String snsTimestamp = snsEnvelope.get("Timestamp").asText();

                System.out.printf(
                        "Feedback recebido: descricao='%s', nota=%d%n",
                        feedback.getDescricao(),
                        feedback.getNota()
                );
                System.out.println("Timestamp SNS: " + snsTimestamp);

                FeedbackEntity entity = toEntity(feedback, snsTimestamp);

                if (feedbackRepository != null) {
                    feedbackRepository.salvar(entity);
                } else {
                    System.out.println("FeedbackRepository nulo (provavelmente em teste unitário), não salvando no DynamoDB.");
                }

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
