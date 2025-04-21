import React from 'react';

function SyntacticResults({ data }) {
    return (
        <div className="syntactic-results">
            <h3>Syntactic Analysis Results</h3>
            {data.sentenceStructure && (
                <div className="sentence-structure">
                    <h4>Sentence Structure</h4>
                    <div className="structure-metrics">
                        <div className="metric-item">
                            <div className="metric-name">Average Sentence Length</div>
                            <div className="metric-value">{data.sentenceStructure.avgLength.toFixed(2)} words</div>
                        </div>
                        <div className="metric-item">
                            <div className="metric-name">Complexity Score</div>
                            <div className="metric-value">{data.sentenceStructure.complexityScore.toFixed(2)}</div>
                        </div>
                        <div className="metric-item">
                            <div className="metric-name">Sentence Variation</div>
                            <div className="metric-value">{data.sentenceStructure.variation.toFixed(2)}</div>
                        </div>
                    </div>
                    {data.sentenceStructure.lengthDistribution && (
                        <div className="length-distribution">
                            <h5>Sentence Length Distribution</h5>
                            <div className="distribution-chart">
                                {Object.entries(data.sentenceStructure.lengthDistribution).map(([range, percentage]) => (
                                    <div key={range} className="distribution-bar">
                                        <div className="bar-label">{range} words</div>
                                        <div className="bar-container">
                                            <div className="bar" style={{ width: `${percentage * 100}%` }}></div>
                                        </div>
                                        <div className="bar-value">{(percentage * 100).toFixed(1)}%</div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            )}
            {data.partsOfSpeech && (
                <div className="parts-of-speech">
                    <h4>Parts of Speech Analysis</h4>
                    <div className="pos-distribution">
                        {Object.entries(data.partsOfSpeech.distribution).map(([pos, value]) => (
                            <div key={pos} className="pos-item">
                                <div className="pos-label">{getPosFullName(pos)}</div>
                                <div className="pos-bar-container">
                                    <div className="pos-bar" style={{ width: `${value * 100}%` }}></div>
                                </div>
                                <div className="pos-value">{(value * 100).toFixed(1)}%</div>
                            </div>
                        ))}
                    </div>
                    {data.partsOfSpeech.ratios && (
                        <div className="pos-ratios">
                            <h5>Key POS Ratios</h5>
                            <div className="ratios-grid">
                                {Object.entries(data.partsOfSpeech.ratios).map(([ratio, value]) => (
                                    <div key={ratio} className="ratio-card">
                                        <div className="ratio-name">{formatRatioName(ratio)}</div>
                                        <div className="ratio-value">{value.toFixed(2)}</div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            )}
            {data.phrasePatterns && (
                <div className="phrase-patterns">
                    <h4>Common Phrase Patterns</h4>
                    <table className="patterns-table">
                        <thead>
                        <tr>
                            <th>Pattern</th>
                            <th>Frequency</th>
                            <th>Example</th>
                        </tr>
                        </thead>
                        <tbody>
                        {data.phrasePatterns.map((pattern, index) => (
                            <tr key={index}>
                                <td>{pattern.pattern}</td>
                                <td>{pattern.frequency}</td>
                                <td>{pattern.example}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

function getPosFullName(abbreviation) {
    const posMap = {
        'NN': 'Noun',
        'NNS': 'Plural Noun',
        'NNP': 'Proper Noun',
        'NNPS': 'Plural Proper Noun',
        'VB': 'Verb (Base Form)',
        'VBD': 'Verb (Past Tense)',
        'VBG': 'Verb (Gerund)',
        'VBN': 'Verb (Past Participle)',
        'VBP': 'Verb (Present)',
        'VBZ': 'Verb (3rd Person)',
        'JJ': 'Adjective',
        'JJR': 'Adjective (Comparative)',
        'JJS': 'Adjective (Superlative)',
        'RB': 'Adverb',
        'RBR': 'Adverb (Comparative)',
        'RBS': 'Adverb (Superlative)',
        'IN': 'Preposition',
        'DT': 'Determiner',
        'PRP': 'Personal Pronoun',
        'PRP$': 'Possessive Pronoun',
        'CC': 'Coordinating Conjunction',
        'CD': 'Cardinal Number',
        'MD': 'Modal',
        'TO': 'Infinitive Marker',
        'UH': 'Interjection'
    };

    return posMap[abbreviation] || abbreviation;
}

function formatRatioName(ratio) {
    return ratio
        .replace(/([A-Z])/g, ' $1')
        .replace(/^./, str => str.toUpperCase())
        .replace(/To/g, ' to ');
}

export default SyntacticResults;
