(ns forensic-nlp.syntactic-analysis
    (:require [clojure.string :as str]
      [clojure.java.io :as io]
      [clojure.data.csv :as csv]
      [opennlp.nlp :as nlp]))


;; Load OpenNLP models
(def sentence-detector (nlp/make-sentence-detector "resources/models/en-sent.bin"))
(def tokenizer (nlp/make-tokenizer "resources/models/en-token.bin"))
(def pos-tagger (nlp/make-pos-tagger "resources/models/en-pos-maxent.bin"))
(defn parse-sentence [sentence]
      nil)

;; Function to read CSV file
(defn read-csv-file [file-path]
      (with-open [reader (io/reader file-path)]
                 (doall (csv/read-csv reader))))

;; Function to extract text column from CSV
(defn extract-text-column [csv-data column-index]
      (map #(nth % column-index) (rest csv-data)))

;; Function to extract author metadata
(defn extract-author-metadata [csv-data]
      (map (fn [row]
               {:id (nth row 0)
                :gender (nth row 1)
                :age (nth row 2)
                :topic (nth row 3)
                :sign (nth row 4)
                :date (nth row 5)
                :text (nth row 6)})
           (rest csv-data)))

;; Text processing functions
(defn detect-sentences [text]
      (sentence-detector text))

(defn tokenize-sentence [sentence]
      (tokenizer sentence))

(defn tag-parts-of-speech [tokens]
      (pos-tagger tokens))

(defn sentence-length [tokens]
      (count tokens))

(defn average-sentence-length [sentences]
      (let [lengths (map #(sentence-length (tokenize-sentence %)) sentences)]
           (if (empty? lengths)
             0
             (/ (reduce + lengths) (count lengths)))))

(defn sentence-complexity [parse-tree]
      (if parse-tree
        (count (re-seq #"\(" (str parse-tree)))
        0))

(defn average-sentence-complexity [parse-trees]
      (let [complexities (map sentence-complexity parse-trees)
            valid-complexities (filter pos? complexities)]
           (if (empty? valid-complexities)
             0
             (/ (reduce + valid-complexities) (count valid-complexities)))))

(defn extract-syntactic-features [text]
      (let [sentences (doall (detect-sentences text))
            tokenized-sentences (doall (map tokenize-sentence sentences))
            pos-tagged-sentences (doall (map tag-parts-of-speech tokenized-sentences))
            parse-trees (doall (map parse-sentence sentences))
            total-sentences (count sentences)
            avg-sentence-length (average-sentence-length sentences)
            avg-sentence-complexity (average-sentence-complexity parse-trees)
            all-pos-tags (flatten (map second (flatten pos-tagged-sentences)))
            pos-tag-freqs (frequencies all-pos-tags)
            total-tags (count all-pos-tags)
            normalized-pos-freqs (if (pos? total-tags)
                                   (into {} (for [[k v] pos-tag-freqs]
                                                 [k (double (/ v total-tags))]))
                                   {})
            sentence-structures (doall (map (fn [tagged-sent]
                                                (str/join " " (map second tagged-sent)))
                                            pos-tagged-sentences))
            common-structures (take 5 (sort-by val > (frequencies sentence-structures)))
            passive-voice-count (count (filter #(re-find #"VBN IN|VBN VBN|VBZ VBN" %) sentence-structures))
            question-count (count (filter #(re-find #"\?$" %) sentences))
            exclamation-count (count (filter #(re-find #"\!$" %) sentences))
            subordinate-clauses (count (filter #(re-find #"IN S|WDT S" (str %)) parse-trees))
            coordinate-clauses (count (filter #(re-find #"CC S" (str %)) parse-trees))]
           {:sentence-count total-sentences
            :avg-sentence-length avg-sentence-length
            :avg-sentence-complexity avg-sentence-complexity
            :pos-tag-frequencies normalized-pos-freqs
            :common-sentence-structures common-structures
            :passive-voice-ratio (if (pos? total-sentences)
                                   (double (/ passive-voice-count total-sentences))
                                   0)
            :question-ratio (if (pos? total-sentences)
                              (double (/ question-count total-sentences))
                              0)
            :exclamation-ratio (if (pos? total-sentences)
                                 (double (/ exclamation-count total-sentences))
                                 0)
            :subordination-ratio (if (pos? total-sentences)
                                   (double (/ subordinate-clauses total-sentences))
                                   0)
            :coordination-ratio (if (pos? total-sentences)
                                  (double (/ coordinate-clauses total-sentences))
                                  0)}))

(defn extract-author-syntactic-patterns [text]
      (let [sentences (doall (detect-sentences text))
            tokenized-sentences (doall (map tokenize-sentence sentences))
            pos-tagged-sentences (doall (map tag-parts-of-speech tokenized-sentences))
            sentence-starts (doall (map #(take 3 (map second %)) pos-tagged-sentences))
            sentence-start-patterns (frequencies (doall (map #(str/join " " %) sentence-starts)))
            sentence-ends (doall (map #(take-last 3 (map second %)) pos-tagged-sentences))
            sentence-end-patterns (frequencies (doall (map #(str/join " " %) sentence-ends)))
            verb-phrases (doall (map (fn [tagged-sent]
                                         (str/join " " (doall (map second (filter #(re-matches #"VB.*" (second %)) tagged-sent)))))

                                     pos-tagged-sentences))
            verb-phrase-patterns (frequencies verb-phrases)
            noun-phrases (doall (map (fn [tagged-sent]
                                         (str/join " " (doall (map second (filter #(re-matches #"NN.*" (second %)) tagged-sent)))))

                                     pos-tagged-sentences))
            noun-phrase-patterns (frequencies noun-phrases)]
           {:sentence-start-patterns (take 5 (sort-by val > sentence-start-patterns))
            :sentence-end-patterns (take 5 (sort-by val > sentence-end-patterns))
            :verb-phrase-patterns (take 5 (sort-by val > verb-phrase-patterns))
            :noun-phrase-patterns (take 5 (sort-by val > noun-phrase-patterns))}))

(defn compare-syntactic-features [features1 features2]
      (let [all-tags (set (concat (keys (:pos-tag-frequencies features1))
                                  (keys (:pos-tag-frequencies features2))))
            get-freq (fn [m tag] (get m tag 0.0))
            squared-diffs (map (fn [tag]
                                   (Math/pow (- (get-freq (:pos-tag-frequencies features1) tag)
                                                (get-freq (:pos-tag-frequencies features2) tag))
                                             2))
                               all-tags)
            euclidean-dist (Math/sqrt (reduce + squared-diffs))
            pos-similarity (/ 1.0 (+ 1.0 euclidean-dist))
            sentence-length-diff (Math/abs (- (:avg-sentence-length features1)
                                              (:avg-sentence-length features2)))
            complexity-diff (Math/abs (- (:avg-sentence-complexity features1)
                                         (:avg-sentence-complexity features2)))
            passive-diff (Math/abs (- (:passive-voice-ratio features1)
                                      (:passive-voice-ratio features2)))
            question-diff (Math/abs (- (:question-ratio features1)
                                       (:question-ratio features2)))
            exclamation-diff (Math/abs (- (:exclamation-ratio features1)
                                          (:exclamation-ratio features2)))
            subordination-diff (Math/abs (- (:subordination-ratio features1)
                                            (:subordination-ratio features2)))
            coordination-diff (Math/abs (- (:coordination-ratio features1)
                                           (:coordination-ratio features2)))]
           {:pos-similarity pos-similarity
            :sentence-length-similarity (/ 1.0 (+ 1.0 sentence-length-diff))
            :complexity-similarity (/ 1.0 (+ 1.0 complexity-diff))
            :passive-voice-similarity (/ 1.0 (+ 1.0 passive-diff))
            :question-ratio-similarity (/ 1.0 (+ 1.0 question-diff))
            :exclamation-ratio-similarity (/ 1.0 (+ 1.0 exclamation-diff))
            :subordination-similarity (/ 1.0 (+ 1.0 subordination-diff))
            :coordination-similarity (/ 1.0 (+ 1.0 coordination-diff))
            :overall-similarity (/ (+ pos-similarity
                                      (/ 1.0 (+ 1.0 sentence-length-diff))
                                      (/ 1.0 (+ 1.0 complexity-diff))
                                      (/ 1.0 (+ 1.0 passive-diff))
                                      (/ 1.0 (+ 1.0 question-diff))
                                      (/ 1.0 (+ 1.0 exclamation-diff))
                                      (/ 1.0 (+ 1.0 subordination-diff))
                                      (/ 1.0 (+ 1.0 coordination-diff)))
                                   8.0)}))

(defn analyze-author-syntax [csv-data text-column-index]
      (let [author-data (extract-author-metadata csv-data)
            author-texts (group-by :id author-data)
            author-features (into {}
                                  (for [[author-id texts] author-texts]
                                       [author-id (extract-syntactic-features (str/join " " (map :text texts)))]))]
           author-features))

(defn identify-potential-author [anonymous-text author-features]
      (let [anonymous-features (extract-syntactic-features anonymous-text)
            author-similarities (into {}
                                      (for [[author-id features] author-features]
                                           [author-id (:overall-similarity (compare-syntactic-features anonymous-features features))]))]
           (sort-by val > author-similarities)))

(defn batch-process-syntax [csv-file-path text-column-index]
      (let [csv-data (read-csv-file csv-file-path)
            texts (extract-text-column csv-data text-column-index)
            author-data (extract-author-metadata csv-data)
            batch-size 100
            text-batches (partition-all batch-size texts)
            results (doall
                      (mapcat (fn [batch]
                                  (map (fn [text]
                                           (try
                                             (extract-syntactic-features text)
                                             (catch Exception e
                                               (println "Error processing text:" (subs text 0 100))
                                               {})))
                                       batch))
                              text-batches))]
           (map (fn [features metadata]
                    (assoc metadata :syntactic-features features))
                results author-data)))

(defn detect-syntactic-anomalies [features author-avg-features]
      (let [significant-deviation? (fn [val1 val2]
                                       (> (Math/abs (- val1 val2)) (* 0.5 val2)))
            anomalies (filter identity
                              [(when (significant-deviation? (:avg-sentence-length features
                                                               (:avg-sentence-length author-avg-features)))
                                     {:type "sentence-length"
                                      :actual (:avg-sentence-length features)
                                      :expected (:avg-sentence-length author-avg-features)})
                               (when (significant-deviation? (:avg-sentence-complexity features
                                                               (:avg-sentence-complexity author-avg-features)))
                                     {:type "sentence-complexity"
                                      :actual (:avg-sentence-complexity features)
                                      :expected (:avg-sentence-complexity author-avg-features)})
                               (when (significant-deviation? (:passive-voice-ratio features
                                                               (:passive-voice-ratio author-avg-features)))
                                     {:type "passive-voice-usage"
                                      :actual (:passive-voice-ratio features)
                                      :expected (:passive-voice-ratio author-avg-features)})
                               (when (significant-deviation? (:question-ratio features
                                                               (:question-ratio author-avg-features)))
                                     {:type "question-usage"
                                      :actual (:question-ratio features)
                                      :expected (:question-ratio author-avg-features)})
                               (when (significant-deviation? (:exclamation-ratio features
                                                               (:exclamation-ratio author-avg-features)))
                                     {:type "exclamation-usage"
                                      :actual (:exclamation-ratio features)
                                      :expected (:exclamation-ratio author-avg-features)})
                               (when (significant-deviation? (:subordination-ratio features
                                                               (:subordination-ratio author-avg-features)))
                                     {:type "subordination-usage"
                                      :actual (:subordination-ratio features)
                                      :expected (:subordination-ratio author-avg-features)})
                               (when (significant-deviation? (:coordination-ratio features
                                                               (:coordination-ratio author-avg-features)))
                                     {:type "coordination-usage"
                                      :actual (:coordination-ratio features)
                                      :expected (:coordination-ratio author-avg-features)})])]
           anomalies))

(defn author-syntactic-fingerprint [author-texts]
      (let [all-text (str/join " " author-texts)
            features (extract-syntactic-features all-text)
            patterns (extract-author-syntactic-patterns all-text)]
           (assoc features :syntactic-patterns patterns)))

(defn -main [& args]
      (let [csv-path "resources/dataset/processed/dataset_clean.csv"
            text-column 6
            csv-data (read-csv-file csv-path)
            author-data (extract-author-metadata csv-data)
            sample-text (:text (first author-data))
            sample-features (extract-syntactic-features sample-text)]
           (println "Syntactic Analysis Demo:")
           (println "------------------------")
           (println "Sample text (truncated):" (subs sample-text 0 100) "...")
           (println "Number of sentences:" (:sentence-count sample-features))
           (println "Average sentence length:" (:avg-sentence-length sample-features))
           (println "Average sentence complexity:" (:avg-sentence-complexity sample-features))
           (println "Top 3 POS tag frequencies:")
           (doseq [[tag freq] (take 3 (sort-by val > (:pos-tag-frequencies sample-features)))]
                  (println "  " tag ":" freq))
           (let [author-groups (group-by :id author-data)
                 author-fingerprints (into {} (for [[author-id author-texts] author-groups]
                                                   [author-id (author-syntactic-fingerprint (map :text author-texts))]))
                 author-ids (take 2 (keys author-fingerprints))]
                (when (>= (count author-ids) 2)
                      (let [author1 (first author-ids)
                            author2 (second author-ids)
                            comparison (compare-syntactic-features (get author-fingerprints author1)
                                                                   (get author-fingerprints author2))]
                           (println "\nAuthor Comparison:")
                           (println "-------------------")
                           (println "Comparing authors" author1 "and" author2)
                           (println "Overall similarity score:" (:overall-similarity comparison))
                           (println "POS usage similarity:" (:pos-similarity comparison))
                           (println "Sentence structure similarity:" (:complexity-similarity comparison)))))))

(defn parallel-process-texts [texts]
      (pmap (fn [text]
                (try
                  (extract-syntactic-features text)
                  (catch Exception e
                    (println "Error processing text:" (subs text 0 (min 100 (count text))))
                    {})))
            texts))

(defn syntactic-entropy [pos-tagged-sentences]
      (let [tag-transitions (mapcat (fn [sent]
                                        (partition 2 1 (map second sent)))
                                    pos-tagged-sentences)
            transition-freqs (frequencies tag-transitions)
            total-transitions (count tag-transitions)
            probs (map #(/ (val %) total-transitions) transition-freqs)
            log2 (fn [x] (/ (Math/log x) (Math/log 2)))
            entropy (- (reduce + (map #(* % (log2 %)) probs)))]
           entropy))

(defn dataset-processing-stats [csv-file-path text-column-index]
      (let [start-time (System/currentTimeMillis)
            csv-data (read-csv-file csv-file-path)
            texts (extract-text-column csv-data text-column-index)
            total-texts (count texts)
            sample-size (min 10 total-texts)
            sample-texts (take sample-size texts)
            sample-start (System/currentTimeMillis)
            _ (doall (map extract-syntactic-features sample-texts))
            sample-end (System/currentTimeMillis)
            time-per-text (/ (- sample-end sample-start) sample-size)
            estimated-total-time (/ (* time-per-text total-texts) 60000.0)
            end-time (System/currentTimeMillis)
            setup-time (/ (- end-time start-time) 1000.0)]
           {:total-texts total-texts
            :setup-time-seconds setup-time
            :estimated-processing-time-minutes estimated-total-time}))

(defn analyze-syntax-sample []
      (let [csv-path "resources/dataset/processed/dataset_clean.csv"
            csv-data (read-csv-file csv-path)
            author-data (extract-author-metadata csv-data)
            sample-text (:text (first author-data))
            sample-features (extract-syntactic-features sample-text)]
           (println "Syntactic Analysis Sample:")
           (println "Sample text:" (subs sample-text 0 (min 100 (count sample-text))) "...")
           (println "Number of sentences:" (:sentence-count sample-features))
           (println "Average sentence length:" (:avg-sentence-length sample-features))
           (println "POS tags:" (take 3 (sort-by val > (:pos-tag-frequencies sample-features))))))