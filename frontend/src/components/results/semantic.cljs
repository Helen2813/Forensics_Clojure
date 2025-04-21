(ns forensic-nlp.frontend.components.results.semantic)

(defn get-sentiment-color [score]
  (cond
   (< score -0.5) "#ff4d4d"
   (< score 0) "#ffcccc"
   (= score 0) "#f0f0f0"
   (<= score 0.5) "#ccffcc"
   :else "#4dff4d"))

(defn format-entity-type [type]
  (-> type
      (clojure.string/replace #"([A-Z])" " $1")
      (clojure.string/trim)
      (clojure.string/capitalize)))

(defn component [data]
  [:div.semantic-results
   [:h3 "Semantic Analysis Results"]
   (when-let [topic-data (:topicModeling data)]
     [:div.topic-modeling-section
      [:h4 "Topic Modeling"]
      [:div.topics-grid
       (for [[idx topic] (map-indexed vector (:topics topic-data))]
         [:div.topic-card {:key idx}
          [:h5 (str "Topic " (inc idx))]
          [:div.topic-keywords
           (for [[kidx keyword] (map-indexed vector (:keywords topic))]
             [:span.keyword-tag {:key kidx} keyword])]
          [:div.topic-weight (str "Weight: " (.toFixed (* (:weight topic) 100) 2) "%")]])]])
   (when-let [sentiment (:sentimentAnalysis data)]
     [:div.sentiment-section
      [:h4 "Sentiment Analysis"]
      [:div.sentiment-gauge
       [:div.sentiment-scale
        [:div.scale-negative "Negative"]
        [:div.scale-neutral "Neutral"]
        [:div.scale-positive "Positive"]]
       [:div.gauge-pointer {:style {:left (str (* (+ (:overall sentiment) 1) 50) "%")}}]
       [:div.sentiment-score (str "Score: " (.toFixed (:overall sentiment) 2))]]
      (when-let [segments (:segments sentiment)]
        [:div.sentiment-segments
         [:h5 "Document Segments"]
         [:div.segments-container
          (for [[idx segment] (map-indexed vector segments)]
            [:div.segment-item {:key idx}
             [:div.segment-text (:text segment)]
             [:div.segment-sentiment {:style {:background-color (get-sentiment-color (:score segment))}}
              (.toFixed (:score segment) 2)]])]])])
   (when-let [entities (:entityRecognition data)]
     [:div.entity-recognition
      [:h4 "Named Entity Recognition"]
      [:div.entities-list
       (for [[type entities-list] entities]
         [:div.entity-group {:key (name type)}
          [:h5 (format-entity-type (name type))]
          [:div.entity-tags
           (for [[idx entity] (map-indexed vector entities-list)]
             [:span.entity-tag {:key idx} entity])]])]])])
