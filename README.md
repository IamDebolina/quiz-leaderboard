# Quiz Leaderboard

## Project Overview
This project implements a full-stack quiz leaderboard system that polls the Vidal Health quiz API 10 times, deduplicates repeated events, builds a participant leaderboard, and submits the final leaderboard once.

The backend is built with Spring Boot and exposes `GET /api/start-quiz?regNo=XXXX` for the frontend to trigger the complete flow.

## Tech Stack
- Java 17
- Spring Boot 3 (Maven)
- React 18 + Vite

## Project Structure
```text
quiz-leaderboard/
+-- src/main/java/com/quiz/
¦   +-- QuizApplication.java
¦   +-- controller/QuizController.java
¦   +-- service/QuizService.java
¦   +-- model/Event.java
¦   +-- model/LeaderboardEntry.java
¦   +-- model/SubmitResponse.java
+-- frontend/
¦   +-- src/
¦   ¦   +-- App.jsx
¦   ¦   +-- components/Leaderboard.jsx
¦   ¦   +-- components/PollProgress.jsx
¦   +-- package.json
+-- pom.xml
+-- README.md
```

## Backend Flow
1. Frontend calls `GET /api/start-quiz?regNo=XXXX`.
2. Backend polls the API from `poll=0` to `poll=9`.
3. Backend waits exactly `Thread.sleep(5000)` between polls.
4. Events are deduplicated using composite key:
   - `roundId + "_" + participant`
5. Unique events are aggregated into participant `totalScore`.
6. Leaderboard is sorted descending by `totalScore`.
7. Leaderboard is submitted once to the submit API.
8. Submission response is returned to frontend and printed in backend console.

## Deduplication Logic
A `HashSet<String>` stores unique event keys:
- Key format: `<roundId>_<participant>`
- If key already exists, the event is skipped completely.
- Only first-seen unique events contribute to participant score totals.

## How to Run
### Backend
```bash
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`.

Using Maven Wrapper (recommended, no global Maven needed):

- Windows (PowerShell/CMD):
```bash
.\mvnw.cmd spring-boot:run
```
- Unix/macOS:
```bash
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173`.

## API Contracts
### Sample Poll API Response
```json
{
  "regNo": "REG123",
  "setId": "SET001",
  "pollIndex": 0,
  "events": [
    { "roundId": "R1", "participant": "Alice", "score": 20 },
    { "roundId": "R1", "participant": "Bob", "score": 15 }
  ]
}
```

### Sample Submit Payload
```json
{
  "regNo": "REG123",
  "leaderboard": [
    { "participant": "Alice", "totalScore": 100 },
    { "participant": "Bob", "totalScore": 85 }
  ]
}
```

### Sample Submit Response
```json
{
  "isCorrect": true,
  "message": "Submission accepted",
  "submittedTotal": 185
}
```

## Screenshots
- Add frontend dashboard screenshot here
- Add final leaderboard screenshot here
- Add submission response screenshot here
