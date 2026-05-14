package com.catjudge.couplejudge.model;

public class CaseRecord {
    private long id;
    private String judgeType;
    private String mode;
    private String eventDescription;
    private String viewA;
    private String viewB;
    private String judgmentResult;
    private String status;
    private String createdAt;
    private String category;
    private boolean inNotebook;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(String judgeType) {
        this.judgeType = judgeType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getViewA() {
        return viewA;
    }

    public void setViewA(String viewA) {
        this.viewA = viewA;
    }

    public String getViewB() {
        return viewB;
    }

    public void setViewB(String viewB) {
        this.viewB = viewB;
    }

    public String getJudgmentResult() {
        return judgmentResult;
    }

    public void setJudgmentResult(String judgmentResult) {
        this.judgmentResult = judgmentResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isInNotebook() {
        return inNotebook;
    }

    public void setInNotebook(boolean inNotebook) {
        this.inNotebook = inNotebook;
    }
}
