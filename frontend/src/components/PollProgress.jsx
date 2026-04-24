function PollProgress({ progress, currentStep, totalSteps }) {
  return (
    <div className="card">
      <div className="section-title">Polling Progress</div>
      <div className="progress-meta">
        <span>{currentStep} / {totalSteps} polls</span>
        <span>{Math.round(progress)}%</span>
      </div>
      <div className="progress-track">
        <div className="progress-fill" style={{ width: `${progress}%` }} />
      </div>
    </div>
  )
}

export default PollProgress
