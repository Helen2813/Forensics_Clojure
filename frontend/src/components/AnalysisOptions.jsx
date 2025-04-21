import React from 'react';

function AnalysisOptions({ options, onChange }) {
    return (
        <div className="analysis-options">
            <h2>Analysis Options</h2>
            <div className="options-grid">
                {Object.entries(options).map(([option, enabled]) => (
                    <div key={option} className="option-item">
                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                checked={enabled}
                                onChange={() => onChange(option)}
                            />
                            {option.charAt(0).toUpperCase() + option.slice(1)} Analysis
                        </label>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default AnalysisOptions;
