(ns forensic-nlp.frontend.components.results.authorship)

(defn component [data]
  [:div.authorship-results
   [:h3 "Authorship Analysis Results"]
   (when-let [authors (:potentialAuthors data)]
     [:div.author-likelihood-section
      [:h4 "Potential Authors"]
      [:div.author-scores
       (for [[idx author] (map-indexed vector authors)]
         [:div.author-score-card {:key idx}
          [:div.author-name (:name author)]
          [:div.score-bar-container
           [:div.score-bar {:style {:width (str (* (:probabilityScore author) 100) "%")}}]]
          [:div.probability-score (str (.toFixed (* (:probabilityScore author) 100) 2) "%")]])]])
   (when-let [features (:stylometricFeatures data)]
     [:div.stylometric-features
      [:h4 "Key Stylometric Features"]
      [:table.features-table
       [:thead
        [:tr
         [:th "Feature"]
         [:th "Value"]
         [:th "Significance"]]]
       [:tbody
        (for [[idx feature] (map-indexed vector features)]
          [:tr {:key idx}
           [:td (:name feature)]
           [:td (:value feature)]
           [:td (:significance feature)]])]]])
   (when-let [confidence (:confidenceScore data)]
     [:div.confidence-section
      [:h4 "Analysis Confidence"]
      [:div.confidence-meter
       [:div.meter-label "Low"]
       [:div.meter-bar-container
        [:div.meter-bar {:style {:width (str (* confidence 100) "%")}}]]
       [:div.meter-label "High"]
       [:div.meter-value (str (.toFixed (* confidence 100) 2) "%")]]])
   (when-let [notes (:analysisNotes data)]
     [:div.analysis-notes
      [:h4 "Analysis Notes"]
      [:p notes]])])
