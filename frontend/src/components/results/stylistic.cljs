(ns forensic-nlp.frontend.components.results.stylistic
  (:require [clojure.string :as str]))

(defn format-dimension [dimension]
  (-> dimension
      name
      (str/replace #"([A-Z])" " $1")
      (str/capitalize)))

(defn get-dimension-pole [dimension is-positive]
  (let [poles {:formalityLevel {:neg "Informal" :pos "Formal"}
               :complexity {:neg "Simple" :pos "Complex"}
               :objectivity {:neg "Subjective" :pos "Objective"}
               :tone {:neg "Negative" :pos "Positive"}}]
    (if-let [pole (get poles dimension)]
      (if is-positive (:pos pole) (:neg pole))
      (if is-positive "High" "Low"))))

(defn format-metric-name [name]
  (-> name
      (str/replace #"([A-Z])" " $1")
      (str/capitalize)))

(defn format-measure-name [name]
  (-> name
      (str/replace #"([A-Z])" " $1")
      (str/capitalize)))

(defn get-consistency-interpretation [score]
  (cond
   (> score 0.9) "Very Consistent"
   (> score 0.7) "Consistent"
   (> score 0.5) "Moderately Consistent"
   (> score 0.3) "Somewhat Inconsistent"
   :else "Very Inconsistent"))

(defn component [data]
  [:div.stylistic-results
   [:h3 "Stylistic Analysis Results"]
   (when-let [writing-style (:writingStyle data)]
     [:div.writing-style-section
      [:h4 "Writing Style"]
      [:div.style-indicators
       (for [[dimension value] writing-style]
         ^{:key (name dimension)}
         [:div.style-indicator
          [:div.dimension-name (format-dimension dimension)]
          [:div.dimension-scale
           [:div.scale-start (get-dimension-pole dimension false)]
           [:div.scale-bar
            [:div.scale-position {:style {:left (str (* (+ value 1) 50) "%")}}]]
           [:div.scale-end (get-dimension-pole dimension true)]])])])
      (when-let [metrics (:styleMetrics data)]
     [:div.style-metrics
      [:h4 "Style Metrics"]
      [:div.metrics-grid
       (for [[metric value] metrics]
         ^{:key metric}
         [:div.metric-card
          [:div.metric-name (format-metric-name metric)]
          [:div.metric-value (if (number? value) (.toFixed value 2) value)]])]])
   (when-let [consistency (:consistencyAnalysis data)]
     [:div.consistency-section
      [:h4 "Style Consistency"]
      [:div.consistency-score
       [:div.score-label "Overall Consistency Score"]
       [:div.score-display
        [:div.score-value (str (.toFixed (* (:overallScore consistency) 100) 2) "%")]
        [:div.score-bar-container
         [:div.score-bar {:style {:width (str (* (:overallScore consistency) 100) "%")}}]]]]
      (when-let [measures (:measures consistency)]
        [:div.consistency-measures
         [:h5 "Consistency Measures"]
         [:table.measures-table
          [:thead
           [:tr
            [:th "Measure"]
            [:th "Score"]
            [:th "Interpretation"]]]
          [:tbody
           (for [[measure score] measures]
             ^{:key measure}
             [:tr
              [:td (format-measure-name measure)]
              [:td (if (number? score) (.toFixed score 2) score)]
              [:td (get-consistency-interpretation score)]])]]])])])
