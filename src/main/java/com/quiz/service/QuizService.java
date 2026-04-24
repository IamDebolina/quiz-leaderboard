package com.quiz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.model.Event;
import com.quiz.model.LeaderboardEntry;
import com.quiz.model.PollMessageResponse;
import com.quiz.model.PollSummary;
import com.quiz.model.StartQuizResponse;
import com.quiz.model.SubmitRequest;
import com.quiz.model.SubmitResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuizService {

    private static final String POLL_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/messages";
    private static final String SUBMIT_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/submit";
    private static final int POLL_RETRY_COUNT = 5;
    private static final long POLL_RETRY_DELAY_MS = 10000;
    private static final long POLL_INTERVAL_MS = 8000;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1500;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public QuizService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public StartQuizResponse startQuiz(String regNo) {
        StartQuizResponse startQuizResponse = new StartQuizResponse();
        startQuizResponse.setRegNo(regNo);

        Set<String> seenKeys = new HashSet<>();
        Map<String, Integer> scoreByParticipant = new HashMap<>();
        List<PollSummary> pollSummaries = new ArrayList<>();

        String setId = null;

        try {
            for (int poll = 0; poll < 10; poll++) {
                String url = UriComponentsBuilder.fromHttpUrl(POLL_URL)
                        .queryParam("regNo", regNo)
                        .queryParam("poll", poll)
                        .toUriString();

                ResponseEntity<PollMessageResponse> response = executePollWithRetry(url);
                PollMessageResponse body = response.getBody();

                if (body == null) {
                    pollSummaries.add(new PollSummary(poll, 0, 0, 0));
                } else {
                    if (setId == null) {
                        setId = body.getSetId();
                    }

                    List<Event> events = body.getEvents() == null ? List.of() : body.getEvents();
                    int uniqueAccepted = 0;
                    int duplicatesSkipped = 0;

                    for (Event event : events) {
                        if (event == null || event.getRoundId() == null || event.getParticipant() == null || event.getScore() == null) {
                            continue;
                        }

                        String key = event.getRoundId() + "_" + event.getParticipant();
                        if (seenKeys.contains(key)) {
                            duplicatesSkipped++;
                            continue;
                        }

                        seenKeys.add(key);
                        uniqueAccepted++;
                        scoreByParticipant.merge(event.getParticipant(), event.getScore(), Integer::sum);
                    }

                    pollSummaries.add(new PollSummary(poll, events.size(), uniqueAccepted, duplicatesSkipped));
                }

                if (poll < 9) {
                    Thread.sleep(POLL_INTERVAL_MS);
                }
            }

            List<LeaderboardEntry> leaderboard = scoreByParticipant.entrySet()
                    .stream()
                    .map(entry -> new LeaderboardEntry(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(LeaderboardEntry::getTotalScore, Comparator.nullsLast(Integer::compareTo)).reversed())
                    .toList();

            SubmitRequest submitRequest = new SubmitRequest(regNo, leaderboard);
            SubmitResponse submitResponse = submitWithRetry(submitRequest);

            System.out.println("Submission response: " + Objects.toString(submitResponse));

            startQuizResponse.setSetId(setId);
            startQuizResponse.setPolls(pollSummaries);
            startQuizResponse.setLeaderboard(leaderboard);
            startQuizResponse.setSubmission(submitResponse);

            return startQuizResponse;
        } catch (RestClientException e) {
            startQuizResponse.setError("HTTP error while polling/submitting quiz: " + e.getMessage());
            return startQuizResponse;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            startQuizResponse.setError("Polling interrupted: " + e.getMessage());
            return startQuizResponse;
        } catch (Exception e) {
            startQuizResponse.setError("Unexpected error: " + e.getMessage());
            return startQuizResponse;
        }
    }

    private ResponseEntity<PollMessageResponse> executePollWithRetry(String url) throws InterruptedException {
        RestClientException lastException = null;
        int pollNumber = extractPollNumber(url);
        for (int attempt = 1; attempt <= POLL_RETRY_COUNT; attempt++) {
            try {
                if (attempt > 1) {
                    System.out.println("Retrying poll " + pollNumber + " (attempt " + attempt + "/" + POLL_RETRY_COUNT + ")");
                }
                return restTemplate.exchange(url, HttpMethod.GET, null, PollMessageResponse.class);
            } catch (HttpStatusCodeException e) {
                lastException = e;
                int statusCode = e.getStatusCode().value();
                boolean isRetryableStatus = statusCode == 502 || statusCode == 503;
                if (!isRetryableStatus || attempt == POLL_RETRY_COUNT) {
                    break;
                }
                System.out.println("Poll " + pollNumber + " failed with HTTP " + statusCode
                        + ". Waiting " + (POLL_RETRY_DELAY_MS / 1000) + "s before retry.");
                Thread.sleep(POLL_RETRY_DELAY_MS);
            } catch (RestClientException e) {
                lastException = e;
                break;
            }
        }
        throw lastException;
    }

    private int extractPollNumber(String url) {
        Matcher matcher = Pattern.compile("[?&]poll=(\\d+)").matcher(url);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    private SubmitResponse submitWithRetry(SubmitRequest submitRequest) throws InterruptedException {
        RestClientException lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<String> rawResponse = restTemplate.postForEntity(SUBMIT_URL, new HttpEntity<>(submitRequest), String.class);
                String rawBody = rawResponse.getBody();
                System.out.println("Raw submission response body: " + rawBody);
                return parseSubmitResponse(rawBody);
            } catch (RestClientException e) {
                lastException = e;
                if (attempt < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY_MS);
                }
            }
        }
        throw lastException;
    }

    private SubmitResponse parseSubmitResponse(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            return new SubmitResponse();
        }
        try {
            return objectMapper.readValue(rawBody, SubmitResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to parse submission response: " + rawBody, e);
        }
    }
}
