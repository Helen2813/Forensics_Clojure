(ns forensic-nlp.frontend.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [forensic-nlp.frontend.components.header :as header]
            [forensic-nlp.frontend.components.footer :as footer]
            [forensic-nlp.frontend.components.upload :as upload]
            [forensic-nlp.frontend.components.analysis-options :as options]
            [forensic-nlp.frontend.components.results :as results])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce app-state
  (r/atom {:documents []
           :analysis-options {:authorship true
                              :lexical true
                              :semantic true
                              :stylistic true
                              :syntactic true}
           :results nil
           :loading? false
           :error nil}))

(defn handle-document-upload [files]
  (swap! app-state update :documents #(concat % files)))

(defn handle-option-change [option]
  (swap! app-state update-in [:analysis-options option] not))

(defn clear-documents []
  (swap! app-state assoc :documents []))

(defn clear-all []
  (swap! app-state assoc
         :documents []
         :results nil
         :error nil))

(defn perform-analysis []
  (let [{:keys [documents analysis-options]} @app-state]
    (when (seq documents)
          (swap! app-state assoc :loading? true :error nil)
          (let [form-data (js/FormData.)]
            (doseq [[idx file] (map-indexed vector documents)]
              (.append form-data (str "document-" idx) file))
            (doseq [[option enabled?] analysis-options]
              (.append form-data (name option) enabled?))
            (go
             (let [response (<! (http/post "/api/analyze" {:body form-data}))
                   {:keys [status body]} response]
               (if (= status 200)
                 (swap! app-state assoc :results body :loading? false)
                 (swap! app-state assoc :error (str "Analysis failed: " (:error body)) :loading? false))))))))

(defn app []
  (let [{:keys [documents analysis-options results loading? error]} @app-state]
    [:div.app-container
     [header/component]
     [:main.content
      [:section.input-section
       [upload/component {:documents documents :on-upload handle-document-upload :on-clear clear-documents}]
       [options/component {:options analysis-options :on-change handle-option-change}]
       [:div.action-buttons
        [:button.analyze-button {:on-click perform-analysis :disabled (or loading? (empty? documents))}
         (if loading? "Analyzing..." "Analyze Documents")]
        [:button.clear-button {:on-click clear-all :disabled loading?} "Clear All"]]
       (when error [:div.error-message error])]
      (when results
            [:section.results-section
             [results/component {:results results :analysis-options analysis-options}]])]
     [footer/component]]))

(defn mount-app []
  (rdom/render [app] (.getElementById js/document "app")))

(defn init [] (mount-app))

(defn ^:export main [] (init))
