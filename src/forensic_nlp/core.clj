(ns forensic-nlp.core
    (:require [forensic-nlp.ingest :as ingest]
      [forensic-nlp.model-downloader :as mdl]
      [forensic-nlp.stylistic-analysis :as sty]
      [forensic-nlp.semantic-analysis :as sem]
      [forensic-nlp.lexical-analysis :as lex]
      [forensic-nlp.feature_extractor :as fx]
      [forensic-nlp.syntactic-analysis :as syn]
      [clojure.java.io :as io]
      [clojure.data.csv :as csv]))

;; Check if the processed dataset exists
(defn processed-data-exists? []
      (let [dir (io/file "resources/dataset/processed/")]
           (and (.exists dir) (not (empty? (.list dir))))))

;; Read the cleaned CSV dataset
(defn read-cleaned-dataset []
      "Reads dataset_clean.csv into a sequence of rows."
      (with-open [reader (io/reader "resources/dataset/processed/dataset_clean.csv")]
                 (doall (csv/read-csv reader))))

;; Main function that orchestrates the process
(defn -main [& args]
      (println " Starting application...")

      ;; Step 0: Ensure NLP models are available
      (println "\n Checking for required NLP models...")
      (mdl/ensure-models)

      ;; Step 1: Process the dataset if needed
      (if (or (some #{"--force"} args) (not (processed-data-exists?)))
        (do
          (println "\n Processing dataset...")
          (ingest/process-dataset)
          (println " Dataset processing finished."))
        (println "\n Processed dataset already exists. Loading preprocessed data..."))

      ;; Step 2: Stylistic analysis on a sample text
      (println "\n Performing stylistic analysis on a sample text:")
      (let [sample-text "This is a sample text. It contains multiple sentences! Is it effective? Let's analyze."
            style-analysis (sty/analyze-style sample-text)]
           (println "Sample Text:" sample-text)
           (println "Stylistic Analysis:")
           (doseq [[k v] style-analysis]
                  (println (name k) ":" v)))

      ;; Step 3: Semantic comparison between two sample texts
      (println "\n Performing semantic comparison between two texts:")
      (let [text-a "The quick brown fox jumps over the lazy dog."
            text-b "A quick red fox leaps over sleepy canines."
            semantic-result (sem/compare-texts text-a text-b)]
           (println "Text A:" text-a)
           (println "Text B:" text-b)
           (println "Semantic Comparison Result:")
           (doseq [[k v] semantic-result]
                  (println (name k) ":" v)))

      ;; Step 4: Lexical analysis on a sample text
      (println "\n Performing lexical analysis on a sample text:")
      (let [lex-text "The quick brown fox jumps over the lazy dog. The fox was very quick."
            lex-result (lex/analyze-text lex-text)]
           (println "Sample Text:" lex-text)
           (println "Lexical Analysis Result:")
           (doseq [[k v] lex-result]
                  (println (name k) ":" v)))

      ;;; Step 5: Feature extraction for authorship attribution
      ;(println "\n Extracting features from full dataset for authorship attribution...")
      ;(fx/process-dataset)
      ;(println " Feature extraction complete. Output: features.csv")

      ;; Step 6: Syntactic Analysis on sample text
      (println "\n--- Running Syntactic Analysis ---")
      (syn/analyze-syntax-sample)

      (println "\n Application finished."))
