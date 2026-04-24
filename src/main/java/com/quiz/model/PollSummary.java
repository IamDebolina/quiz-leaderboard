package com.quiz.model;

public class PollSummary {
    private Integer pollIndex;
    private Integer eventsReceived;
    private Integer uniqueAccepted;
    private Integer duplicatesSkipped;

    public PollSummary() {
    }

    public PollSummary(Integer pollIndex, Integer eventsReceived, Integer uniqueAccepted, Integer duplicatesSkipped) {
        this.pollIndex = pollIndex;
        this.eventsReceived = eventsReceived;
        this.uniqueAccepted = uniqueAccepted;
        this.duplicatesSkipped = duplicatesSkipped;
    }

    public Integer getPollIndex() {
        return pollIndex;
    }

    public void setPollIndex(Integer pollIndex) {
        this.pollIndex = pollIndex;
    }

    public Integer getEventsReceived() {
        return eventsReceived;
    }

    public void setEventsReceived(Integer eventsReceived) {
        this.eventsReceived = eventsReceived;
    }

    public Integer getUniqueAccepted() {
        return uniqueAccepted;
    }

    public void setUniqueAccepted(Integer uniqueAccepted) {
        this.uniqueAccepted = uniqueAccepted;
    }

    public Integer getDuplicatesSkipped() {
        return duplicatesSkipped;
    }

    public void setDuplicatesSkipped(Integer duplicatesSkipped) {
        this.duplicatesSkipped = duplicatesSkipped;
    }
}
