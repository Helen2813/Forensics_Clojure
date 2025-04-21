import React from 'react';

function SemanticResults({ data }) {
    return (
        <div className="semantic-results">
            <h3>Semantic Analysis Results</h3>
            {data.topicModeling && (
                <div className="topic-modeling-section">
                    <h4>Topic Modeling</h4>
                    <div className="topics-grid">
                        {data.topicModeling.topics.map((topic, index) => (
                            <div key={index} className="topic-card">
                                <h5>Topic {index + 1}</h5>
                                <div className="topic-keywords">
                                    {topic.keywords.map((keyword, kidx) => (
                                        <span key={kidx} className="keyword-tag">
                      {keyword}
                    </span>
                                    ))}
                                </div>
                                <div className="topic-weight">
                                    Weight: {(topic.weight * 100).toFixed(2)}%
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {data.sentimentAnalysis && (
                <div className="sentiment-section">
                    <h4>Sentiment Analysis</h4>
                    <div className="sentiment-gauge">
                        <div className="sentiment-scale">
                            <div className="scale-negative">Negative</div>
                            <div className="scale-neutral">Neutral</div>
                            <div className="scale-positive">Positive</div>
                        </div>
                        <div className="gauge-pointer" style={{ left: `${(data.sentimentAnalysis.overall + 1) * 50}%` }}></div>
                        <div className="sentiment-score">
                            Score: {data.sentimentAnalysis.overall.toFixed(2)}
                        </div>
                    </div>
                    {data.sentimentAnalysis.segments && (
                        <div className="sentiment-segments">
                            <h5>Document Segments</h5>
                            <div className="segments-container">
                                {data.sentimentAnalysis.segments.map((segment, index) => (
                                    <div key={index} className="segment-item">
                                        <div className="segment-text">{segment.text}</div>
                                        <div className="segment-sentiment" style={{ backgroundColor: getSentimentColor(segment.score) }}>
                                            {segment.score.toFixed(2)}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            )}
            {data.entityRecognition && (
                <div className="entity-recognition">
                    <h4>Named Entity Recognition</h4>
                    <div className="entities-list">
                        {Object.entries(data.entityRecognition).map(([type, entities]) => (
                            <div key={type} className="entity-group">
                                <h5>{formatEntityType(type)}</h5>
                                <div className="entity-tags">
                                    {entities.map((entity, index) => (
                                        <span key={index} className="entity-tag">
                      {entity}
                    </span>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}

function getSentimentColor(score) {
    if (score < -0.5) return "#ff4d4d";
    if (score < 0) return "#ffcccc";
    if (score === 0) return "#f0f0f0";
    if (score <= 0.5) return "#ccffcc";
    return "#4dff4d";
}

function formatEntityType(type) {
    const formatted = type.replace(/([A-Z])/g, ' $1').trim();
    return formatted.charAt(0).toUpperCase() + formatted.slice(1);
}

export default SemanticResults;
