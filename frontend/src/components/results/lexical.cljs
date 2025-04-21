(ns forensic-nlp.frontend.components.results.lexical)

(defn format-metric-name [name]
  (-> name
      (clojure.string/replace #"([A-Z])" " $1")
      (clojure.string/capitalize)))

(defn get-readability-interpretation [metric value]
  (case metric
        "fleschKincaid"
        (cond
         (> value 90) "Very Easy - 5th Grade"
         (> value 80) "Easy - 6th Grade"
         (> value 70) "Fairly Easy - 7th Grade"
         (> value 60) "Standard - 8-9th Grade"
         (> value 50) "Fairly Difficult - 10-12th Grade"
         (> value 30) "Difficult - College"
         :else "Very Difficult - Graduate Level")

        "automatedReadability" (str "Grade level: " (js/Math.round value))
        "colemanLiau" (str "Grade level: " (js/Math.round value))
        "N/A"))

(defn component [data]
  [:div.lexical-results
   [:h3 "Lexical Analysis Results"]
   (when-let [vocab (:vocabulary data)]
     [:div.vocabulary-section
      [:h4 "Vocabulary Analysis"]
      [:div.stat-cards
       [:div.stat-card
        [:div.stat-label "Unique Words"]
        [:div.stat-value (:uniqueWords vocab)]]
       [:div.stat-card
        [:div.stat-label "Lexical Density"]
        [:div.stat-value (.toFixed (:lexicalDensity vocab) 2)]]
       [:div.stat-card
        [:div.stat-label "Avg. Word Length"]
        [:div.stat-value (.toFixed (:avgWordLength vocab) 2)]]]])
   (when-let [words (:frequentWords data)]
     [:div.frequent-words
      [:h4 "Most Frequent Words"]
      [:div.word-cloud
       (for [[idx word] (map-indexed vector words)]
         [:div.word-item {:key idx
                          :style {:font-size (str (js/Math.max 1 (js/Math.min 3 (/ (:frequency word) 10))) "em")}}
          (:text word)])]])
   (when-let [metrics (:readabilityMetrics data)]
     [:div.readability-section
      [:h4 "Readability Metrics"]
      [:table.metrics-table
       [:thead
        [:tr
         [:th "Metric"]
         [:th "Score"]
         [:th "Interpretation"]]]
       [:tbody
        (for [[key value] metrics]
          [:tr {:key (name key)}
           [:td (format-metric-name (name key))]
           [:td (if (number? value) (.toFixed value 2) value)]
           [:td (get-readability-interpretation (name key) value)]])]]])])
