(ns forensic-nlp.frontend.components.footer)

(defn component []
  [:footer.app-footer
   [:p "© " (.getFullYear (js/Date.)) " Forensic NLP Analysis Tool"]])
