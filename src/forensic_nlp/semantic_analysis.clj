(ns forensic-nlp.semantic-analysis
    "Performs semantic analysis using OpenNLP, including tokenization, POS tagging,
     word overlap, and Jaccard similarity. Designed to support authorship analysis."
    (:require [clojure.string :as str]
      [opennlp.nlp :as nlp]
      ;[opennlp.tools.models :as models]
      [clojure.set :as set]))

;; ------------------------------
;; Load OpenNLP Models (Configurable Path)
;; ------------------------------

(def model-dir "resources/models/")

(def tokenizer-model (str model-dir "en-token.bin"))
(def pos-model (str model-dir "en-pos-maxent.bin"))

(def tokenize (nlp/make-tokenizer tokenizer-model))
(def pos-tag (nlp/make-pos-tagger pos-model))

;; ------------------------------
;; Helper Functions
;; ------------------------------

(defn tokenize-text
      "Tokenizes input text into words."
      [text]
      (tokenize text))

(defn pos-tag-text
      "Tags parts of speech for the input text."
      [text]
      (pos-tag (tokenize text)))

(defn normalize-token
      "Lowercases and removes punctuation from a token."
      [token]
      (-> token
          str/lower-case
          (str/replace #"[^a-zA-Z0-9]" "")))

(defn word-frequencies
      "Returns a frequency map of normalized tokens."
      [tokens]
      (frequencies (map normalize-token tokens)))

(defn jaccard-similarity
      "Computes Jaccard similarity between two sets."
      [set1 set2]
      (let [intersection (count (set/intersection set1 set2))
            union (count (set/union set1 set2))]
           (if (zero? union) 0 (/ intersection union))))

;; ------------------------------
;; Main Comparison Function
;; ------------------------------

(defn compare-texts
      "Compares two texts semantically using token overlap and POS overlap."
      [text1 text2]
      (let [tokens1        (map normalize-token (tokenize-text text1))
            tokens2        (map normalize-token (tokenize-text text2))
            token-set1     (set tokens1)
            token-set2     (set tokens2)
            pos-tags1      (map second (pos-tag-text text1))
            pos-tags2      (map second (pos-tag-text text2))
            pos-set1       (set pos-tags1)
            pos-set2       (set pos-tags2)]
           {:token-jaccard        (jaccard-similarity token-set1 token-set2)
            :pos-jaccard          (jaccard-similarity pos-set1 pos-set2)
            :common-words         (count (set/intersection token-set1 token-set2))
            :common-pos-tags      (count (set/intersection pos-set1 pos-set2))
            :unique-words-text1   (count token-set1)
            :unique-words-text2   (count token-set2)}))

;; ------------------------------
;; Example Main Function
;; ------------------------------

(defn -main []
      (let [t1 "The quick brown fox jumps over the lazy dog."
            t2 "A quick red fox leaps over sleepy canines."
            result (compare-texts t1 t2)]
           (println "Semantic Comparison Results:")
           (doseq [[k v] result]
                  (println (name k) ":" v))))
