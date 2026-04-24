import { useEffect, useMemo, useState } from 'react'
import Leaderboard from './components/Leaderboard'
import PollProgress from './components/PollProgress'

const TOTAL_POLLS = 10

function App() {
  const [regNo, setRegNo] = useState('')
  const [isRunning, setIsRunning] = useState(false)
  const [progress, setProgress] = useState(0)
  const [currentStep, setCurrentStep] = useState(0)
  const [polls, setPolls] = useState([])
  const [leaderboard, setLeaderboard] = useState([])
  const [submission, setSubmission] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let timer
    if (isRunning) {
      timer = setInterval(() => {
        setProgress((prev) => {
          const next = Math.min(prev + 2, 98)
          const inferredStep = Math.min(TOTAL_POLLS, Math.max(0, Math.round(next / 10)))
          setCurrentStep(inferredStep)
          return next
        })
      }, 1000)
    }

    return () => clearInterval(timer)
  }, [isRunning])

  const sortedPolls = useMemo(
    () => [...polls].sort((a, b) => (a.pollIndex ?? 0) - (b.pollIndex ?? 0)),
    [polls]
  )

  const handleStart = async () => {
    if (!regNo.trim()) {
      setError('Please enter a registration number before starting.')
      return
    }

    setIsRunning(true)
    setError('')
    setProgress(0)
    setCurrentStep(0)
    setPolls([])
    setLeaderboard([])
    setSubmission(null)

    try {
      const response = await fetch(`http://localhost:8080/api/start-quiz?regNo=${encodeURIComponent(regNo.trim())}`)
      const payload = await response.json()

      if (!response.ok || payload.error) {
        throw new Error(payload.error || 'Failed to run quiz polling.')
      }

      setPolls(payload.polls || [])
      setLeaderboard(payload.leaderboard || [])
      setSubmission(payload.submission || null)
      setCurrentStep(TOTAL_POLLS)
      setProgress(100)
    } catch (err) {
      setError(err.message || 'Unexpected error while running quiz polling.')
    } finally {
      setIsRunning(false)
    }
  }

  return (
    <div className="app-shell">
      <header className="hero">
        <h1>Quiz Leaderboard Dashboard</h1>
        <p>Run all 10 polls, deduplicate events, and submit the final ranking in one click.</p>
      </header>

      <main className="content-grid">
        <section className="card controls-card">
          <div className="section-title">Start Quiz Poll</div>
          <div className="controls-row">
            <input
              value={regNo}
              onChange={(e) => setRegNo(e.target.value)}
              placeholder="Enter registration number"
              className="reg-input"
              disabled={isRunning}
            />
            <button onClick={handleStart} disabled={isRunning}>
              {isRunning ? 'Polling in Progress...' : 'Start Quiz Poll'}
            </button>
          </div>
          {error && <div className="error-box">{error}</div>}
        </section>

        <PollProgress progress={progress} currentStep={currentStep} totalSteps={TOTAL_POLLS} />

        <section className="card">
          <div className="section-title">Poll Results</div>
          {sortedPolls.length === 0 ? (
            <p className="muted">Poll results will appear here once the run is complete.</p>
          ) : (
            <div className="poll-list">
              {sortedPolls.map((poll) => (
                <article className="poll-item" key={poll.pollIndex}>
                  <div>Poll #{poll.pollIndex}</div>
                  <div>Events: {poll.eventsReceived}</div>
                  <div>Unique: {poll.uniqueAccepted}</div>
                  <div>Duplicates Skipped: {poll.duplicatesSkipped}</div>
                </article>
              ))}
            </div>
          )}
        </section>

        {leaderboard.length > 0 && <Leaderboard leaderboard={leaderboard} />}

        {submission && (
          <section className="card">
            <div className="section-title">Submission Response</div>
            <div className="response-grid">
              <div><strong>isCorrect:</strong> {String(submission.correct ?? submission.isCorrect)}</div>
              <div><strong>isIdempotent:</strong> {String(submission.isIdempotent ?? 'N/A')}</div>
              <div><strong>message:</strong> {submission.message ?? 'N/A'}</div>
              <div><strong>submittedTotal:</strong> {submission.submittedTotal ?? 'N/A'}</div>
              <div><strong>regNo:</strong> {submission.regNo ?? 'N/A'}</div>
              <div><strong>totalPollsMade:</strong> {submission.totalPollsMade ?? 'N/A'}</div>
              <div><strong>attemptCount:</strong> {submission.attemptCount ?? 'N/A'}</div>
              <div><strong>expectedTotal:</strong> {submission.expectedTotal ?? 'N/A'}</div>
            </div>
          </section>
        )}
      </main>
    </div>
  )
}

export default App
