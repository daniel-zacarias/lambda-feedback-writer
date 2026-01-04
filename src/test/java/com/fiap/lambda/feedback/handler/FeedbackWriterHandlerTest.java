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
import java.util.concurrent.atomic.AtomicBoolean;

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

        AtomicBoolean chamado = new AtomicBoolean(false);

        IFeedbackRepository fakeRepo = entity -> chamado.set(true);

        FeedbackWriterHandler handler = new FeedbackWriterHandler(fakeRepo);

        String json = Files.readString(
                Path.of("src/test/resources/event.json"),
                StandardCharsets.UTF_8);

        SQSEvent event = objectMapper.readValue(json, SQSEvent.class);

        handler.handleRequest(event, null);

        assertTrue(chamado.get());
    }
}
