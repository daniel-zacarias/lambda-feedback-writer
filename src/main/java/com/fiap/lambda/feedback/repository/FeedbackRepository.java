package com.fiap.lambda.feedback.repository;


import com.fiap.lambda.feedback.model.FeedbackEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedbackRepository implements IFeedbackRepository  {

    private final DynamoDbTable<FeedbackEntity> table;

    public FeedbackRepository(String tableName) {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_2) // Ohio
                .build();

        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(dynamoDbClient)
                        .build();

        this.table = enhancedClient.table(
                tableName,
                TableSchema.fromBean(FeedbackEntity.class)
        );
    }

    public void salvar(FeedbackEntity entity) {
        try {
            table.putItem(entity);
            System.out.println("Feedback salvo com sucesso!");
        } catch (DynamoDbException e) {
            System.err.println("Erro ao salvar no DynamoDB: " + e.getMessage());
            throw e;
        }
    }
}
