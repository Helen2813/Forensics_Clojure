import React from 'react';

function LexicalResults({ data }) {
    return (
        <div className="lexical-results">
            <h3>Lexical Analysis Results</h3>
            {data.vocabulary && (
                <div className="vocabulary-section">
                    <h4>Vocabulary Analysis</h4>
                    <div className="stat-cards">
                        <div className="stat-card">
                            <div className="stat-label">Unique Words</div>
                            <div className="stat-value">{data.vocabulary.uniqueWords}</div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-label">Lexical Density</div>
                            <div className="stat-value">{data.vocabulary.lexicalDensity.toFixed(2)}</div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-label">Avg. Word Length</div>
                            <div className="stat-value">{data.vocabulary.avgWordLength.toFixed(2)}</div>
                        </div>
                    </div>
                </div>
            )}
            {data.frequentWords && (
                <div className="frequent-words">
                    <h4>Most Frequent Words</h4>
                    <div className="word-cloud">
                        {data.frequentWords.map((word, index) => (
                            <div
                                key={index}
                                className="word-item"
                                style={{ fontSize: `${Math.max(1, Math.min(3, word.frequency / 10))}em` }}
                            >
                                {word.text}
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {data.readabilityMetrics && (
                <div className="readability-section">
                    <h4>Readability Metrics</h4>
                    <table className="metrics-table">
                        <thead>
                        <tr>
                            <th>Metric</th>
                            <th>Score</th>
                            <th>Interpretation</th>
                        </tr>
                        </thead>
                        <tbody>
                        {Object.entries(data.readabilityMetrics).map(([key, value]) => (
                            <tr key={key}>
                                <td>{formatMetricName(key)}</td>
                                <td>{typeof value === 'number' ? value.toFixed(2) : value}</td>
                                <td>{getReadabilityInterpretation(key, value)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

function formatMetricName(name) {
    return name
        .replace(/([A-Z])/g, ' $1')
        .replace(/^./, str => str.toUpperCase());
}

function getReadabilityInterpretation(metric, value) {
    const interpretations = {
        fleschKincaid: value => {
            if (value > 90) return "Very Easy - 5th Grade";
            if (value > 80) return "Easy - 6th Grade";
            if (value > 70) return "Fairly Easy - 7th Grade";
            if (value > 60) return "Standard - 8-9th Grade";
            if (value > 50) return "Fairly Difficult - 10-12th Grade";
            if (value > 30) return "Difficult - College";
            return "Very Difficult - Graduate Level";
        },
        automatedReadability: value => `Grade level: ${Math.round(value)}`,
        colemanLiau: value => `Grade level: ${Math.round(value)}`
    };

    return interpretations[metric] ? interpretations[metric](value) : "N/A";
}

export default LexicalResults;
