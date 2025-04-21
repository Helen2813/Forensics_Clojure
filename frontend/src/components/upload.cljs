(ns forensic-nlp.frontend.components.upload
  (:require [reagent.core :as r]))

(defn component [{:keys [documents on-upload on-clear]}]
  (let [drag-active? (r/atom false)
        file-input-ref (r/atom nil)]
    (fn []
      [:div.upload-component
       [:h2 "Document Upload"]
       [:div.dropzone
        {:on-click #(when @file-input-ref (.click @file-input-ref))
         :on-drag-over #(do (.preventDefault %) (.stopPropagation %) (reset! drag-active? true))
         :on-drag-leave #(do (.preventDefault %) (.stopPropagation %) (reset! drag-active? false))
         :on-drop #(do (.preventDefault %) (.stopPropagation %)
                    (reset! drag-active? false)
                    (when (pos? (.. % -dataTransfer -files -length))
                          (on-upload (array-seq (.. % -dataTransfer -files)))))}
        [:p "Drag & drop files here or click to select"]
        [:input {:type "file"
                 :ref #(reset! file-input-ref %)
                 :style {:display "none"}
                 :multiple true
                 :on-change #(when (pos? (.. % -target -files -length))
                              (on-upload (array-seq (.. % -target -files)))
                              (set! (.-value (.-target %)) ""))}]]
       (when (seq documents)
             [:div.document-list
              [:h3 (str "Uploaded Documents (" (count documents) ")")]
              [:ul
               (for [[idx doc] (map-indexed vector documents)]
                 [:li {:key idx} (.-name doc) " (" (.toFixed (/ (.-size doc) 1024) 2) " KB)"])
               ]
              [:button.clear-documents-button {:on-click on-clear} "Clear Documents"]])])))
