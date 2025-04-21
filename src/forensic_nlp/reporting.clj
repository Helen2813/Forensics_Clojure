(ns forensic-nlp.reporting
  (:require [forensic-nlp.stylistic-analysis :as sty]
            [forensic-nlp.lexical-analysis :as lex]
            [forensic-nlp.semantic-analysis :as sem]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defn analyze-and-report [text-a text-b]
  ;; Stylistic analysis
  (let [sty-a (sty/analyze-style text-a)
        sty-b (sty/analyze-style text-b)

        ;; Lexical analysis
        lex-a (lex/analyze-text text-a)
        lex-b (lex/analyze-text text-b)

        ;; Semantic comparison
        sem-res (sem/compare-texts text-a text-b)

        ;; Combine everything
        report {:text-a-style sty-a
                :text-b-style sty-b
                :text-a-lexical lex-a
                :text-b-lexical lex-b
                :semantic-comparison sem-res}]
    report))

(defn print-report [report]
  (println "\n📝 Stylistic Analysis for Text A:")
  (doseq [[k v] (:text-a-style report)]
    (println (name k) ":" v))

  (println "\n📝 Stylistic Analysis for Text B:")
  (doseq [[k v] (:text-b-style report)]
    (println (name k) ":" v))

  (println "\n🔤 Lexical Analysis for Text A:")
  (doseq [[k v] (:text-a-lexical report)]
    (println (name k) ":" v))

  (println "\n🔤 Lexical Analysis for Text B:")
  (doseq [[k v] (:text-b-lexical report)]
    (println (name k) ":" v))

  (println "\n🔍 Semantic Comparison:")
  (doseq [[k v] (:semantic-comparison report)]
    (println (name k) ":" v)))

(defn save-report-csv [report output-path]
  (with-open [writer (io/writer output-path)]
    (csv/write-csv writer [["Metric" "Text A" "Text B"]])

    ;; Write stylistic
    (doseq [k (keys (:text-a-style report))]
      (csv/write-csv writer [[(name k)
                              (get (:text-a-style report) k)
                              (get (:text-b-style report) k)]]))

    ;; Write lexical
    (doseq [k (keys (:text-a-lexical report))]
      (csv/write-csv writer [[(str "Lex-" (name k))
                              (get (:text-a-lexical report) k)
                              (get (:text-b-lexical report) k)]]))

    ;; Semantic section
    (csv/write-csv writer [["Semantic Comparison" "Value"]])
    (doseq [[k v] (:semantic-comparison report)]
      (csv/write-csv writer [[(name k) v]]))

    (println "✅ Report saved to" output-path)))

;; FINAL entry point
(defn -main []
  ;; No need to ensure models — you've already downloaded them ✅
  (let [text-a "The quick brown fox jumps over the lazy dog."
        text-b "A quick red fox leaps over sleepy canines."
        report (analyze-and-report text-a text-b)]
    (print-report report)
    (save-report-csv report "resources/report_output.csv")))
