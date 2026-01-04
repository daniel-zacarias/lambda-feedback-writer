package com.fiap.lambda.feedback.repository;

import org.jboss.logging.Logger;

import com.fiap.lambda.feedback.model.FeedbackEntity;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@ApplicationScoped
public class FeedbackRepository {
    private static final Logger LOG = Logger.getLogger(FeedbackRepository.class);

    private final String TABLE_NAME = System.getenv("TABLE_NAME");

    @Inject
    private final DynamoDbTable<FeedbackEntity> table;

    public FeedbackRepository(String tableName) {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_2) // Ohio
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.table = enhancedClient.table(
                tableName,
                TableSchema.fromBean(FeedbackEntity.class));
    }

    public FeedbackRepository() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.table = enhancedClient.table(
                TABLE_NAME,
                TableSchema.fromBean(FeedbackEntity.class));
    }

    public FeedbackRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table(
                TABLE_NAME,
                TableSchema.fromBean(FeedbackEntity.class));
    }

    public void salvar(FeedbackEntity entity) {
        try {
            this.table.putItem(entity);

            LOG.info("Feedback salvo com sucesso!");
        } catch (DynamoDbException e) {
            Log.info("Erro ao salvar no DynamoDB: " + e.getMessage());
            throw e;
        }
    }
}
