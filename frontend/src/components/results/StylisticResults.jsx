import React from 'react';

function StylisticResults({ data }) {
    return (
        <div className="stylistic-results">
            <h3>Stylistic Analysis Results</h3>
            {data.writingStyle && (
                <div className="writing-style-section">
                    <h4>Writing Style</h4>
                    <div className="style-indicators">
                        {Object.entries(data.writingStyle).map(([dimension, value]) => (
                            <div key={dimension} className="style-indicator">
                                <div className="dimension-name">{formatDimension(dimension)}</div>
                                <div className="dimension-scale">
                                    <div className="scale-start">{getDimensionPole(dimension, false)}</div>
                                    <div className="scale-bar">
                                        <div className="scale-position" style={{ left: `${(value + 1) * 50}%` }}></div>
                                    </div>
                                    <div className="scale-end">{getDimensionPole(dimension, true)}</div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {data.styleMetrics && (
                <div className="style-metrics">
                    <h4>Style Metrics</h4>
                    <div className="metrics-grid">
                        {Object.entries(data.styleMetrics).map(([metric, value]) => (
                            <div key={metric} className="metric-card">
                                <div className="metric-name">{formatMetricName(metric)}</div>
                                <div className="metric-value">{typeof value === 'number' ? value.toFixed(2) : value}</div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {data.consistencyAnalysis && (
                <div className="consistency-section">
                    <h4>Style Consistency</h4>
                    <div className="consistency-score">
                        <div className="score-label">Overall Consistency Score</div>
                        <div className="score-display">
                            <div className="score-value">{(data.consistencyAnalysis.overallScore * 100).toFixed(2)}%</div>
                            <div className="score-bar-container">
                                <div className="score-bar" style={{ width: `${data.consistencyAnalysis.overallScore * 100}%` }}></div>
                            </div>
                        </div>
                    </div>
                    {data.consistencyAnalysis.measures && (
                        <div className="consistency-measures">
                            <h5>Consistency Measures</h5>
                            <table className="measures-table">
                                <thead>
                                <tr>
                                    <th>Measure</th>
                                    <th>Score</th>
                                    <th>Interpretation</th>
                                </tr>
                                </thead>
                                <tbody>
                                {Object.entries(data.consistencyAnalysis.measures).map(([measure, score]) => (
                                    <tr key={measure}>
                                        <td>{formatMeasureName(measure)}</td>
                                        <td>{score.toFixed(2)}</td>
                                        <td>{getConsistencyInterpretation(score)}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

function formatDimension(dimension) {
    return dimension
        .replace(/([A-Z])/g, ' $1')
        .replace(/^./, str => str.toUpperCase());
}

function getDimensionPole(dimension, isPositive) {
    const poles = {
        formalityLevel: { neg: "Informal", pos: "Formal" },
        complexity: { neg: "Simple", pos: "Complex" },
        objectivity: { neg: "Subjective", pos: "Objective" },
        tone: { neg: "Negative", pos: "Positive" }
    };

    return poles[dimension] ?
        (isPositive ? poles[dimension].pos : poles[dimension].neg) :
        (isPositive ? "High" : "Low");
}

function formatMetricName(name) {
    return name
        .replace(/([A-Z])/g, ' $1')
        .replace(/^./, str => str.toUpperCase());
}

function formatMeasureName(name) {
    return name
        .replace(/([A-Z])/g, ' $1')
        .replace(/^./, str => str.toUpperCase());
}

function getConsistencyInterpretation(score) {
    if (score > 0.9) return "Very Consistent";
    if (score > 0.7) return "Consistent";
    if (score > 0.5) return "Moderately Consistent";
    if (score > 0.3) return "Somewhat Inconsistent";
    return "Very Inconsistent";
}

export default StylisticResults;
