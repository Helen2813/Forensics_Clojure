(ns forensic-nlp.stylistic-analysis
    (:require [clojure.string :as str]))

(defn split-sentences [text]
      (->> (str/split text #"(?<=[.!?])\s+")
           (map str/trim)
           (remove str/blank?)))

(defn split-words [sentence]
      (->> (str/split sentence #"\s+")
           (remove str/blank?)))

(defn average [nums]
      (if (empty? nums) 0.0 (double (/ (reduce + nums) (count nums)))))

(defn variance [nums]
      (let [avg (average nums)]
           (if (empty? nums) 0.0 (double (/ (reduce + (map #(Math/pow (- % avg) 2) nums)) (count nums))))))

(defn average-word-length [text]
      (let [words (->> (str/split text #"\s+") (remove str/blank?))
            total-chars (reduce + (map count words))
            word-count (count words)]
           (if (zero? word-count) 0.0 (double (/ total-chars word-count)))))

(defn punctuation-frequency [text]
      (frequencies (re-seq #"[,.!?;:]" text)))

(defn sentence-length-metrics [text]
      (let [sentences (split-sentences text)
            lengths (map #(count (split-words %)) sentences)]
           {:average-sentence-length (average lengths)
            :sentence-length-variance (variance lengths)}))

(defn flesch-score [text]
      (let [sentences (split-sentences text)
            words (mapcat split-words sentences)
            num-sentences (count sentences)
            num-words (count words)
            num-syllables (reduce + (map #(count (re-seq #"[aeiouyAEIOUY]+" %)) words))]
           (if (or (zero? num-sentences) (zero? num-words))
             0.0
             (let [wps (double (/ num-words num-sentences))
                   spw (double (/ num-syllables (max num-words 1)))]
                  (- 206.835 (* 1.015 wps) (* 84.6 spw))))))

(defn analyze-style [text]
      (let [sentences (split-sentences text)
            words (mapcat split-words sentences)
            sent-metrics (sentence-length-metrics text)]
           {:num-sentences (count sentences)
            :total-word-count (count words)
            :average-word-length (average-word-length text)
            :average-sentence-length (:average-sentence-length sent-metrics)
            :sentence-length-variance (:sentence-length-variance sent-metrics)
            :punctuation-frequency (punctuation-frequency text)
            :flesch-reading-score (flesch-score text)}))
