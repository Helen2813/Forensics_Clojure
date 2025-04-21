import React, { useState } from 'react';
import AuthorshipResults from './results/AuthorshipResults';
import LexicalResults from './results/LexicalResults';
import SemanticResults from './results/SemanticResults';
import StylisticResults from './results/StylisticResults';
import SyntacticResults from './results/SyntacticResults';

function ResultsDisplay({ results, analysisOptions }) {
    const [activeTab, setActiveTab] = useState('summary');

    const tabs = [
        { id: 'summary', label: 'Summary' },
        ...(analysisOptions.authorship ? [{ id: 'authorship', label: 'Authorship' }] : []),
        ...(analysisOptions.lexical ? [{ id: 'lexical', label: 'Lexical' }] : []),
        ...(analysisOptions.semantic ? [{ id: 'semantic', label: 'Semantic' }] : []),
        ...(analysisOptions.stylistic ? [{ id: 'stylistic', label: 'Stylistic' }] : []),
        ...(analysisOptions.syntactic ? [{ id: 'syntactic', label: 'Syntactic' }] : [])
    ];

    return (
        <div className="results-display">
            <h2>Analysis Results</h2>
            <div className="results-tabs">
                {tabs.map(tab => (
                    <button
                        key={tab.id}
                        className={`tab-button ${activeTab === tab.id ? 'active' : ''}`}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>
            <div className="tab-content">
                {activeTab === 'summary' && (
                    <div className="summary-results">
                        <h3>Analysis Summary</h3>
                        <div className="summary-cards">
                            {Object.keys(analysisOptions).filter(key => analysisOptions[key]).map(type => (
                                <div key={type} className="summary-card">
                                    <h4>{type.charAt(0).toUpperCase() + type.slice(1)} Analysis</h4>
                                    <p>{getSummaryDescription(type)}</p>
                                    <button
                                        className="view-details-button"
                                        onClick={() => setActiveTab(type)}
                                    >
                                        View Details
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
                {activeTab === 'authorship' && results.authorship && (
                    <AuthorshipResults data={results.authorship} />
                )}
                {activeTab === 'lexical' && results.lexical && (
                    <LexicalResults data={results.lexical} />
                )}
                {activeTab === 'semantic' && results.semantic && (
                    <SemanticResults data={results.semantic} />
                )}
                {activeTab === 'stylistic' && results.stylistic && (
                    <StylisticResults data={results.stylistic} />
                )}
                {activeTab === 'syntactic' && results.syntactic && (
                    <SyntacticResults data={results.syntactic} />
                )}
            </div>
        </div>
    );
}

function getSummaryDescription(type) {
    const descriptions = {
        authorship: "Analysis of writing patterns to determine potential authors",
        lexical: "Analysis of vocabulary, word choice, and term frequency",
        semantic: "Analysis of meaning, themes, and contextual relationships",
        stylistic: "Analysis of writing style, including formality and consistency",
        syntactic: "Analysis of sentence structure, grammar, and composition"
    };
    return descriptions[type] || "Analysis complete";
}

export default ResultsDisplay;
