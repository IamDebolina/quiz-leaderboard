package com.quiz.controller;

import com.quiz.model.StartQuizResponse;
import com.quiz.service.QuizService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/start-quiz")
    public ResponseEntity<StartQuizResponse> startQuiz(@RequestParam @NotBlank String regNo) {
        StartQuizResponse response = quizService.startQuiz(regNo);
        if (response.getError() != null) {
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
