package com.quiz.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubmitResponse {
    @JsonProperty("regNo")
    private String regNo;
    @JsonProperty("isCorrect")
    private Boolean isCorrect;
    @JsonProperty("isIdempotent")
    private Boolean isIdempotent;
    @JsonProperty("totalPollsMade")
    private Integer totalPollsMade;
    @JsonProperty("attemptCount")
    private Integer attemptCount;
    @JsonProperty("submittedTotal")
    private Integer submittedTotal;
    @JsonProperty("expectedTotal")
    private Integer expectedTotal;
    @JsonProperty("message")
    private String message;

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Boolean getIsIdempotent() {
        return isIdempotent;
    }

    public void setIsIdempotent(Boolean isIdempotent) {
        this.isIdempotent = isIdempotent;
    }

    public Integer getTotalPollsMade() {
        return totalPollsMade;
    }

    public void setTotalPollsMade(Integer totalPollsMade) {
        this.totalPollsMade = totalPollsMade;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSubmittedTotal() {
        return submittedTotal;
    }

    public void setSubmittedTotal(Integer submittedTotal) {
        this.submittedTotal = submittedTotal;
    }

    public Integer getExpectedTotal() {
        return expectedTotal;
    }

    public void setExpectedTotal(Integer expectedTotal) {
        this.expectedTotal = expectedTotal;
    }

    @Override
    public String toString() {
        return "SubmitResponse{" +
                "regNo='" + regNo + '\'' +
                ", totalPollsMade=" + totalPollsMade +
                ", attemptCount=" + attemptCount +
                ", isCorrect=" + isCorrect +
                ", isIdempotent=" + isIdempotent +
                ", message='" + message + '\'' +
                ", submittedTotal=" + submittedTotal +
                ", expectedTotal=" + expectedTotal +
                '}';
    }
}
