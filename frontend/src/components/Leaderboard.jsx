function Leaderboard({ leaderboard }) {
  return (
    <div className="card">
      <div className="section-title">Final Leaderboard</div>
      <table className="leaderboard-table">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Participant</th>
            <th>Total Score</th>
          </tr>
        </thead>
        <tbody>
          {leaderboard.map((entry, index) => (
            <tr key={`${entry.participant}-${index}`}>
              <td>{index + 1}</td>
              <td>{entry.participant}</td>
              <td>{entry.totalScore}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default Leaderboard
