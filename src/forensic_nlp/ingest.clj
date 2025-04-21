(ns forensic-nlp.ingest
    "Handles dataset downloading, renaming columns, cleaning text, filtering short texts, and producing a cleaned CSV."
    (:require [clojure.java.io :as io]
      [clojure.string :as str]
      [clojure.data.csv :as csv]))

;; Define paths
(def raw-dataset-path "resources/dataset/raw/dataset.csv")
(def cleaned-dataset-path "resources/dataset/processed/dataset_clean.csv")

(defn download-dataset
      "Downloads dataset from URL into raw-dataset-path."
      [url]
      (let [dest (io/file raw-dataset-path)]
           (with-open [in (io/input-stream url)
                       out (io/output-stream dest)]
                      (io/copy in out))
           (println "Dataset downloaded to" raw-dataset-path)))

(defn clean-text
      "Removes extra whitespace and trims the input."
      [text]
      (-> text
          (str/replace #"\s+" " ")
          (str/trim)))

(defn rename-sign->author
      "Renames 'sign' column to 'author'."
      [header]
      (mapv #(if (= (str/lower-case %) "sign") "author" %) header))

(defn process-dataset
      "Reads raw CSV, cleans text, filters out short texts, and writes cleaned dataset."
      []
      (io/make-parents cleaned-dataset-path)
      (with-open [reader (io/reader raw-dataset-path)]
                 (let [csv-data   (csv/read-csv reader)
                       old-header (first csv-data)
                       new-header (rename-sign->author old-header)
                       rows       (rest csv-data)
                       author-idx (.indexOf new-header "author")
                       text-idx   (.indexOf new-header "text")]

                      (when (or (= author-idx -1) (= text-idx -1))
                            (throw (ex-info "CSV must contain 'author' and 'text' columns."
                                            {:header new-header})))

                      (let [transformed-rows
                            (for [row rows
                                  :let [author (nth row author-idx nil)
                                        raw-text (nth row text-idx nil)
                                        cleaned (when raw-text (clean-text raw-text))
                                        word-count (count (str/split cleaned #"\s+"))]
                                  :when (and author raw-text (>= word-count 20))]
                                 (assoc (vec row) text-idx cleaned))]

                           (with-open [writer (io/writer cleaned-dataset-path)]
                                      (csv/write-csv writer [new-header])
                                      (csv/write-csv writer transformed-rows))
                           (println "✅ Cleaned dataset written to:" cleaned-dataset-path)))))
