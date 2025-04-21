(ns forensic-nlp.authorship-analysis
    "Performs basic authorship attribution using k-NN classification over feature vectors."
    (:require [clojure.data.csv :as csv]
      [clojure.java.io :as io]
      [clojure.string :as str]))

;; --- Helpers ---

(defn parse-float [s]
      (try (Double/parseDouble s)
           (catch Exception _ 0.0)))

(defn load-features [path]
      "Loads CSV with features. Returns a sequence of {:author ..., :features [...]}"
      (with-open [reader (io/reader path)]
                 (let [data (csv/read-csv reader)
                       _header (first data)
                       rows (rest data)]
                      (doall
                        (map (fn [row]
                                 {:author (first row)
                                  :features (map parse-float (rest row))})
                             rows)))))

(defn split-dataset [data ratio]
      "Splits dataset into [train test] with ratio like 0.8"
      (let [shuffled (shuffle data)
            split-at (int (* ratio (count data)))]
           [(subvec (vec shuffled) 0 split-at)
            (subvec (vec shuffled) split-at)]))

(defn euclidean-distance [v1 v2]
      (Math/sqrt (reduce + (map #(Math/pow (- %1 %2) 2) v1 v2))))

(defn predict-knn [train sample k]
      "Predicts author of sample using k-NN with training data"
      (->> train
           (map #(assoc % :dist (euclidean-distance (:features %) (:features sample))))
           (sort-by :dist)
           (take k)
           (map :author)
           (frequencies)
           (sort-by val >)
           ffirst))

;; --- Evaluation ---

(defn evaluate []
      (let [[train test] (split-dataset (vec (load-features "resources/features_sample.csv")) 0.8)
            k 3
            total (count test)
            correct (count (filter (fn [sample]
                                       (= (:author sample)
                                          (predict-knn train sample k)))
                                   test))]
           (println "Total samples:" total)
           (println "Correct predictions:" correct)
           (println "Accuracy:" (format "%.2f%%" (* 100.0 (/ correct total))))))

;; --- Entry Point ---

(defn -main [& _]
      (println "🔍 Running Authorship Attribution Evaluation...")
      (evaluate))
