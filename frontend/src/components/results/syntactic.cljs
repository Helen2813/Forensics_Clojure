(ns forensic-nlp.frontend.components.results.syntactic
  (:require [clojure.string :as str]))

(defn get-pos-full-name [abbreviation]
  (let [pos-map {"NN" "Noun"
                 "NNS" "Plural Noun"
                 "NNP" "Proper Noun"
                 "NNPS" "Plural Proper Noun"
                 "VB" "Verb (Base Form)"
                 "VBD" "Verb (Past Tense)"
                 "VBG" "Verb (Gerund)"
                 "VBN" "Verb (Past Participle)"
                 "VBP" "Verb (Present)"
                 "VBZ" "Verb (3rd Person)"
                 "JJ" "Adjective"
                 "JJR" "Adjective (Comparative)"
                 "JJS" "Adjective (Superlative)"
                 "RB" "Adverb"
                 "RBR" "Adverb (Comparative)"
                 "RBS" "Adverb (Superlative)"
                 "IN" "Preposition"
                 "DT" "Determiner"
                 "PRP" "Personal Pronoun"
                 "PRP$" "Possessive Pronoun"
                 "CC" "Coordinating Conjunction"
                 "CD" "Cardinal Number"
                 "MD" "Modal"
                 "TO" "Infinitive Marker"
                 "UH" "Interjection"}]
    (get pos-map abbreviation abbreviation)))

(defn format-ratio-name [ratio]
  (-> ratio
      (str/replace #"([A-Z])" " $1")
      (str/capitalize)
      (str/replace "To" " to ")))

(defn component [data]
  [:div.syntactic-results
   [:h3 "Syntactic Analysis Results"]
   (when-let [sentence-structure (:sentenceStructure data)]
     [:div.sentence-structure
      [:h4 "Sentence Structure"]
      [:div.structure-metrics
       [:div.metric-item
        [:div.metric-name "Average Sentence Length"]
        [:div.metric-value (str (.toFixed (:avgLength sentence-structure) 2) " words")]]
       [:div.metric-item
        [:div.metric-name "Complexity Score"]
        [:div.metric-value (.toFixed (:complexityScore sentence-structure) 2)]]
       [:div.metric-item
        [:div.metric-name "Sentence Variation"]
        [:div.metric-value (.toFixed (:variation sentence-structure) 2)]]]
      (when-let [length-distribution (:lengthDistribution sentence-structure)]
        [:div.length-distribution
         [:h5 "Sentence Length Distribution"]
         [:div.distribution-chart
          (for [[range percentage] length-distribution]
            ^{:key range}
            [:div.distribution-bar
             [:div.bar-label (str range " words")]
             [:div.bar-container
              [:div.bar {:style {:width (str (* percentage 100) "%")}}]]
             [:div.bar-value (str (.toFixed (* percentage 100) 1) "%")]])]])])
   (when-let [pos (:partsOfSpeech data)]
     [:div.parts-of-speech
      [:h4 "Parts of Speech Analysis"]
      [:div.pos-distribution
       (for [[pos-abbr value] (:distribution pos)]
         ^{:key pos-abbr}
         [:div.pos-item
          [:div.pos-label (get-pos-full-name pos-abbr)]
          [:div.pos-bar-container
           [:div.pos-bar {:style {:width (str (* value 100) "%")}}]]
          [:div.pos-value (str (.toFixed (* value 100) 1) "%")]])]
      (when-let [ratios (:ratios pos)]
        [:div.pos-ratios
         [:h5 "Key POS Ratios"]
         [:div.ratios-grid
          (for [[ratio value] ratios]
            ^{:key ratio}
            [:div.ratio-card
             [:div.ratio-name (format-ratio-name ratio)]
             [:div.ratio-value (.toFixed value 2)]])]])])
   (when-let [phrase-patterns (:phrasePatterns data)]
     [:div.phrase-patterns
      [:h4 "Common Phrase Patterns"]
      [:table.patterns-table
       [:thead
        [:tr
         [:th "Pattern"]
         [:th "Frequency"]
         [:th "Example"]]]
       [:tbody
        (for [[idx pattern] (map-indexed vector phrase-patterns)]
          ^{:key idx}
          [:tr
           [:td (:pattern pattern)]
           [:td (:frequency pattern)]
           [:td (:example pattern)]])]]])])
