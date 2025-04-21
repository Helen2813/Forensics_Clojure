(ns forensic-nlp.lexical-analysis
    (:require [opennlp.nlp :refer [make-tokenizer]]
      [clojure.string :as str]))

(def tokenizer (make-tokenizer "resources/models/en-token.bin"))

(defn tokenize-text [text]
      (tokenizer text))

(defn simple-stem [word]
      (-> word
          (str/lower-case)
          (str/replace #"(ing|ed|s)\b" "")))

(defn normalize-tokens [tokens]
      (map simple-stem tokens))

(defn word-frequency [tokens]
      (frequencies tokens))

(defn vocabulary-diversity [tokens]
      (let [total (count tokens)
            unique (count (set tokens))]
           (if (zero? total) 0.0 (double (/ unique total)))))

(defn n-grams [n tokens]
      (when (>= (count tokens) n)
            (map vec (partition n 1 tokens))))

(defn top-n-grams [n tokens limit]
      (take limit (sort-by (comp - val)
                           (frequencies (n-grams n tokens)))))

(defn average-word-length [tokens]
      (let [total (reduce + (map count tokens))
            count-tokens (count tokens)]
           (if (zero? count-tokens) 0.0 (double (/ total count-tokens)))))

(defn analyze-text [text]
      (let [tokens (tokenize-text text)
            norm-tokens (normalize-tokens tokens)]
           {:total-tokens         (count norm-tokens)
            :unique-tokens        (count (set norm-tokens))
            :vocabulary-diversity (vocabulary-diversity norm-tokens)
            :word-frequency       (word-frequency norm-tokens)
            :common-bigrams       (top-n-grams 2 norm-tokens 5)
            :common-trigrams      (top-n-grams 3 norm-tokens 5)
            :average-word-length  (average-word-length norm-tokens)}))
