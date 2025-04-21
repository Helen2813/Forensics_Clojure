(ns forensic-nlp.frontend.components.analysis-options)

(defn component [{:keys [options on-change]}]
  [:div.analysis-options
   [:h2 "Analysis Options"]
   [:div.options-grid
    (for [[option enabled?] options]
      [:div.option-item {:key (name option)}
       [:label.checkbox-label
        [:input {:type "checkbox" :checked enabled? :on-change #(on-change option)}]
        (str (-> option name clojure.string/capitalize) " Analysis")]])]])
