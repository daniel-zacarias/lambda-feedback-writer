package com.fiap.lambda.feedback.dto;

public class FeedbackDTO {

    private String status;
    private String id;
    private String descricao;
    private Integer nota;
    private String receivedAt;

    public FeedbackDTO() {
    }

    public FeedbackDTO(String status, String id, String descricao, Integer nota, String receivedAt) {
        this.status = status;
        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.receivedAt = receivedAt;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public String toString() {
        return "FeedbackDTO{" +
                "descricao='" + descricao + '\'' +
                ", nota=" + nota +
                '}';
    }

}
