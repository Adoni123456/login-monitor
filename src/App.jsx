import { useEffect, useState } from "react";
import "./App.css";

const API_URL = "http://localhost:8081/api";

function App() {
  const [loginAttempts, setLoginAttempts] = useState([]);
  const [suspiciousActivities, setSuspiciousActivities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchData = async () => {
    try {
      setLoading(true);
      setError("");

      const [loginResponse, suspiciousResponse] = await Promise.all([
        fetch(`${API_URL}/login`),
        fetch(`${API_URL}/suspicious`)
      ]);

      if (!loginResponse.ok || !suspiciousResponse.ok) {
        throw new Error("Failed to fetch data");
      }

      const loginData = await loginResponse.json();
      const suspiciousData = await suspiciousResponse.json();

      setLoginAttempts(loginData);
      setSuspiciousActivities(suspiciousData);
    } catch (err) {
      setError(
        "Unable to connect to the Spring Boot backend. Make sure it is running on port 8081."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const totalAttempts = loginAttempts.length;

  const failedAttempts = loginAttempts.filter(
    (attempt) => attempt.status === "FAILURE"
  ).length;

  const successfulAttempts = loginAttempts.filter(
    (attempt) => attempt.status === "SUCCESS"
  ).length;

  const suspiciousCount = suspiciousActivities.length;

  const formatDate = (date) => {
    if (!date) return "-";

    return new Date(date).toLocaleString("en-IN");
  };

  return (
    <div className="dashboard">

      {/* Header */}
      <header className="header">
        <div>
          <h1>Login Activity Monitor</h1>
          <p>Monitor login attempts and suspicious behaviour</p>
        </div>

        <button className="refresh-button" onClick={fetchData}>
          ↻ Refresh
        </button>
      </header>

      {/* Error */}
      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {/* Loading */}
      {loading ? (
        <div className="loading">
          Loading dashboard...
        </div>
      ) : (
        <>
          {/* Statistics */}
          <section className="stats-grid">

            <div className="stat-card">
              <div className="stat-title">Total Attempts</div>
              <div className="stat-value">{totalAttempts}</div>
            </div>

            <div className="stat-card">
              <div className="stat-title">Successful</div>
              <div className="stat-value success">
                {successfulAttempts}
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-title">Failed</div>
              <div className="stat-value danger">
                {failedAttempts}
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-title">Suspicious</div>
              <div className="stat-value warning">
                {suspiciousCount}
              </div>
            </div>

          </section>

          {/* Login Attempts */}
          <section className="panel">
            <div className="panel-header">
              <div>
                <h2>Recent Login Attempts</h2>
                <p>All recorded login activity</p>
              </div>
            </div>

            {loginAttempts.length === 0 ? (
              <div className="empty">
                No login attempts found.
              </div>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Username</th>
                      <th>IP Address</th>
                      <th>Status</th>
                      <th>Time</th>
                    </tr>
                  </thead>

                  <tbody>
                    {loginAttempts.map((attempt) => (
                      <tr key={attempt.id}>
                        <td>{attempt.id}</td>
                        <td>{attempt.username}</td>
                        <td>{attempt.ipAddress}</td>

                        <td>
                          <span
                            className={
                              attempt.status === "SUCCESS"
                                ? "badge success-badge"
                                : "badge failure-badge"
                            }
                          >
                            {attempt.status}
                          </span>
                        </td>

                        <td>{formatDate(attempt.timestamp)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>

          {/* Suspicious Activity */}
          <section className="panel">
            <div className="panel-header">
              <div>
                <h2>Suspicious Activity</h2>
                <p>Detected potentially suspicious login behaviour</p>
              </div>
            </div>

            {suspiciousActivities.length === 0 ? (
              <div className="empty">
                No suspicious activity detected.
              </div>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>IP Address</th>
                      <th>Username</th>
                      <th>Reason</th>
                      <th>Time</th>
                    </tr>
                  </thead>

                  <tbody>
                    {suspiciousActivities.map((activity) => (
                      <tr key={activity.id}>
                        <td>{activity.id}</td>
                        <td>{activity.ipAddress}</td>
                        <td>{activity.username || "-"}</td>
                        <td className="reason">
                          {activity.reason}
                        </td>
                        <td>{formatDate(activity.timestamp)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}

export default App;