import React from 'react';

function AuthorshipResults({ data }) {
    return (
        <div className="authorship-results">
            <h3>Authorship Analysis Results</h3>
            {data.potentialAuthors && (
                <div className="author-likelihood-section">
                    <h4>Potential Authors</h4>
                    <div className="author-scores">
                        {data.potentialAuthors.map((author, index) => (
                            <div key={index} className="author-score-card">
                                <div className="author-name">{author.name}</div>
                                <div className="score-bar-container">
                                    <div
                                        className="score-bar"
                                        style={{ width: `${author.probabilityScore * 100}%` }}
                                    ></div>
                                </div>
                                <div className="probability-score">
                                    {(author.probabilityScore * 100).toFixed(2)}%
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {data.stylometricFeatures && (
                <div className="stylometric-features">
                    <h4>Key Stylometric Features</h4>
                    <table className="features-table">
                        <thead>
                        <tr>
                            <th>Feature</th>
                            <th>Value</th>
                            <th>Significance</th>
                        </tr>
                        </thead>
                        <tbody>
                        {data.stylometricFeatures.map((feature, index) => (
                            <tr key={index}>
                                <td>{feature.name}</td>
                                <td>{feature.value}</td>
                                <td>{feature.significance}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
            {data.confidenceScore && (
                <div className="confidence-section">
                    <h4>Analysis Confidence</h4>
                    <div className="confidence-meter">
                        <div className="meter-label">Low</div>
                        <div className="meter-bar-container">
                            <div className="meter-bar" style={{ width: `${data.confidenceScore * 100}%` }}></div>
                        </div>
                        <div className="meter-label">High</div>
                        <div className="meter-value">{(data.confidenceScore * 100).toFixed(2)}%</div>
                    </div>
                </div>
            )}
            {data.analysisNotes && (
                <div className="analysis-notes">
                    <h4>Analysis Notes</h4>
                    <p>{data.analysisNotes}</p>
                </div>
            )}
        </div>
    );
}

export default AuthorshipResults;
