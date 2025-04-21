(ns forensic-nlp.data-utils
    (:require [clojure.java.io :as io]
      [clojure.data.csv :as csv]))

(defn read-cleaned-dataset []
      "Reads dataset_clean.csv into a sequence of rows."
      (with-open [reader (io/reader "resources/dataset/processed/dataset_clean.csv")]
                 (doall (csv/read-csv reader))))