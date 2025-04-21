(ns forensic-nlp.frontend.components.results
  (:require [reagent.core :as r]
            [forensic-nlp.frontend.components.results.authorship :as authorship]
            [forensic-nlp.frontend.components.results.lexical :as lexical]
            [forensic-nlp.frontend.components.results.semantic :as semantic]
            [forensic-nlp.frontend.components.results.stylistic :as stylistic]
            [forensic-nlp.frontend.components.results.syntactic :as syntactic]))

(defn get-summary-description [type]
  (case type
        :authorship "Analysis of writing patterns to determine potential authors"
        :lexical "Analysis of vocabulary, word choice, and term frequency"
        :semantic "Analysis of meaning, themes, and contextual relationships"
        :stylistic "Analysis of writing style, including formality and consistency"
        :syntactic "Analysis of sentence structure, grammar, and composition"
        "Analysis complete"))

(defn component [{:keys [results analysis-options]}]
  (let [active-tab (r/atom :summary)]
    (fn []
      (let [tab-options (concat [{:id :summary :label "Summary"}]
                                (for [[key enabled?] analysis-options :when enabled?]
                                  {:id key :label (clojure.string/capitalize (name key))}))]
        [:div.results-display
         [:h2 "Analysis Results"]
         [:div.results-tabs
          (for [{:keys [id label]} tab-options]
            [:button.tab-button {:key (name id)
                                 :class (when (= @active-tab id) "active")
                                 :on-click #(reset! active-tab id)}
             label])]
         [:div.tab-content
          (case @active-tab
                :summary
                [:div.summary-results
                 [:h3 "Analysis Summary"]
                 [:div.summary-cards
                  (for [[type enabled?] analysis-options :when enabled?]
                    [:div.summary-card {:key (name type)}
                     [:h4 (str (-> type name clojure.string/capitalize) " Analysis")]
                     [:p (get-summary-description type)]
                     [:button.view-details-button {:on-click #(reset! active-tab type)}
                      "View Details"]])]
                 ]
                :authorship (when (:authorship results) [authorship/component (:authorship results)])
                :lexical (when (:lexical results) [lexical/component (:lexical results)])
                :semantic (when (:semantic results) [semantic/component (:semantic results)])
                :stylistic (when (:stylistic results) [stylistic/component (:stylistic results)])
                :syntactic (when (:syntactic results) [syntactic/component (:syntactic results)])
                nil)]]))))
