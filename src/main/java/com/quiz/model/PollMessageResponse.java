package com.quiz.model;

import java.util.ArrayList;
import java.util.List;

public class PollMessageResponse {
    private String regNo;
    private String setId;
    private Integer pollIndex;
    private List<Event> events = new ArrayList<>();

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Integer getPollIndex() {
        return pollIndex;
    }

    public void setPollIndex(Integer pollIndex) {
        this.pollIndex = pollIndex;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
