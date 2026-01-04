package com.fiap.lambda.feedback.repository;

import com.fiap.lambda.feedback.model.FeedbackEntity;

public interface IFeedbackRepository {
    void salvar(FeedbackEntity entity);
}
