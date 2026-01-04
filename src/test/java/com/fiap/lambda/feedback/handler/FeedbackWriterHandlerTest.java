package com.fiap.lambda.feedback.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.lambda.feedback.repository.IFeedbackRepository;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class FeedbackWriterHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveProcessarEventoSqsComSucesso() throws Exception {

        AtomicBoolean chamado = new AtomicBoolean(false);

        IFeedbackRepository fakeRepo = entity -> chamado.set(true);

        FeedbackWriterHandler handler = new FeedbackWriterHandler(fakeRepo);

        String json = Files.readString(
                Path.of("src/test/resources/event.json"),
                StandardCharsets.UTF_8
        );

        SQSEvent event = objectMapper.readValue(json, SQSEvent.class);

        handler.handleRequest(event, null);

        assertTrue(chamado.get());
    }
}
