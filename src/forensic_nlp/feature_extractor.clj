(ns forensic-nlp.feature_extractor
    (:require [forensic-nlp.stylistic-analysis :as sty]
      [forensic-nlp.lexical-analysis :as lex]
      [forensic-nlp.data-utils :refer [read-cleaned-dataset]]
      [clojure.java.io :as io]
      [clojure.data.csv :as csv]
      [clojure.string :as str]))

(defn extract-features [text]
      (let [sty (sty/analyze-style text)
            lex (lex/analyze-text text)]
           {:avg-sentence-length      (:average-sentence-length sty)
            :sentence-length-variance (:sentence-length-variance sty)
            :flesch-reading-score     (:flesch-reading-score sty)
            :avg-word-length          (:average-word-length sty)
            :vocab-diversity          (:vocabulary-diversity lex)
            :total-tokens             (:total-word-count sty)}))

(defn process-dataset []
      (let [rows (take 20000 (rest (read-cleaned-dataset)))
            output-file "resources/features_sample.csv"]
           (with-open [writer (io/writer output-file)]
                      (csv/write-csv writer
                                     [["author"
                                       "avg-sentence-length"
                                       "sentence-length-variance"
                                       "flesch-reading-score"
                                       "avg-word-length"
                                       "vocab-diversity"
                                       "total-tokens"]])

                      (doseq [row rows
                              :let [text (nth row 6)
                                    author (nth row 4)
                                    word-count (count (str/split text #"\s+"))]
                              :when (and text author (>= word-count 20))]
                             (let [features (extract-features text)]
                                  (csv/write-csv writer
                                                 [[author
                                                   (features :avg-sentence-length)
                                                   (features :sentence-length-variance)
                                                   (features :flesch-reading-score)
                                                   (features :avg-word-length)
                                                   (features :vocab-diversity)
                                                   (features :total-tokens)]]))))
           (println "Sample feature extraction complete! Saved to:" output-file)))
