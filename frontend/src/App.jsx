import React, { useState, useEffect } from 'react';
import './App.css';
import UploadComponent from './components/UploadComponent';
import AnalysisOptions from './components/AnalysisOptions';
import ResultsDisplay from './components/ResultsDisplay';
import Header from './components/Header';
import Footer from './components/Footer';

function App() {
  const [documents, setDocuments] = useState([]);
  const [analysisOptions, setAnalysisOptions] = useState({
    authorship: true,
    lexical: true,
    semantic: true,
    stylistic: true,
    syntactic: true
  });
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleDocumentUpload = (newDocuments) => {
    setDocuments([...documents, ...newDocuments]);
  };

  const handleOptionChange = (option) => {
    setAnalysisOptions({
      ...analysisOptions,
      [option]: !analysisOptions[option]
    });
  };

  const performAnalysis = async () => {
    if (documents.length === 0) {
      setError("Please upload at least one document to analyze");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Prepare form data for file upload
      const formData = new FormData();
      documents.forEach((doc, index) => {
        formData.append(`document-${index}`, doc);
      });

      // Add analysis options
      Object.keys(analysisOptions).forEach(option => {
        formData.append(option, analysisOptions[option]);
      });

      // Make API request to your Clojure backend
      const response = await fetch('/api/analyze', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Analysis failed');
      }

      const data = await response.json();
      setResults(data);
    } catch (err) {
      setError('An error occurred during analysis: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const clearAll = () => {
    setDocuments([]);
    setResults(null);
    setError(null);
  };

  return (
      <div className="app-container">
        <Header />
        <main className="content">
          <section className="input-section">
            <UploadComponent
                onUpload={handleDocumentUpload}
                documents={documents}
                onClear={() => setDocuments([])}
            />

            <AnalysisOptions
                options={analysisOptions}
                onChange={handleOptionChange}
            />

            <div className="action-buttons">
              <button
                  className="analyze-button"
                  onClick={performAnalysis}
                  disabled={loading || documents.length === 0}
              >
                {loading ? 'Analyzing...' : 'Analyze Documents'}
              </button>

              <button
                  className="clear-button"
                  onClick={clearAll}
                  disabled={loading}
              >
                Clear All
              </button>
            </div>

            {error && <div className="error-message">{error}</div>}
          </section>

          {results && (
              <section className="results-section">
                <ResultsDisplay results={results} analysisOptions={analysisOptions} />
              </section>
          )}
        </main>
        <Footer />
      </div>
  );
}

export default App;
