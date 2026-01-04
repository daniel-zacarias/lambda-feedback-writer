package com.fiap.lambda.feedback.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.lambda.feedback.repository.FeedbackRepository;

import io.quarkus.test.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
public class FeedbackWriterHandlerTest {
    private FeedbackWriterHandler handler;

    private ObjectMapper objectMapper;

    @Mock
    FeedbackRepository feedbackRepository;

    @BeforeEach
    void injectObjectMapper() throws Exception {
        objectMapper = new ObjectMapper();
        handler = new FeedbackWriterHandler(objectMapper, feedbackRepository);
    }

    @Test
    void deveProcessarEventoSqsComSucesso() throws Exception {
        String json = Files.readString(
                Path.of("src/test/resources/event.json"),
                StandardCharsets.UTF_8);

        SQSEvent event = objectMapper.readValue(json, SQSEvent.class);

        handler.handleRequest(event, null);
    }
}
