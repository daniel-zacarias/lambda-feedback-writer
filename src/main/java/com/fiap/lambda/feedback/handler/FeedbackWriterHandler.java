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

import org.jboss.logging.Logger;

@ApplicationScoped
public class FeedbackWriterHandler implements RequestHandler<SQSEvent, Void> {

    ObjectMapper objectMapper;

    FeedbackRepository feedbackRepository;

    public FeedbackWriterHandler() {
        this(new ObjectMapper(), new FeedbackRepository());
    }

    public FeedbackWriterHandler(
            ObjectMapper objectMapper,
            FeedbackRepository feedbackRepository) {
        this.objectMapper = objectMapper;
        this.feedbackRepository = feedbackRepository;
    }

    private static final Logger LOG = Logger.getLogger(FeedbackWriterHandler.class);

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

                LOG.infof("Feedback recebido: descricao='%s', nota=%d", feedback.getDescricao(), feedback.getNota());
                LOG.info("Timestamp SNS: " + snsTimestamp);

                FeedbackEntity entity = toEntity(feedback, snsTimestamp);

                if (feedbackRepository != null) {
                    feedbackRepository.salvar(entity);
                } else {
                    LOG.warn("FeedbackRepository nulo (provavelmente em teste unitário), não salvando no DynamoDB.");
                }

            } catch (Exception e) {
                LOG.error("Erro ao processar mensagem SQS: " + e.getMessage(), e);
            }
        });

        return null;
    }

    private FeedbackEntity toEntity(FeedbackDTO dto, String timestampSns) {
        FeedbackEntity entity = new FeedbackEntity();
        entity.setId(dto.getId());
        entity.setDescricao(dto.getDescricao());
        entity.setNota(dto.getNota());
        entity.setTimestamp(timestampSns);
        return entity;
    }
}
