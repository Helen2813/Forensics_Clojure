(ns forensic-nlp.model-downloader
    (:import [opennlp.tools.util.model ModelUtil]
      [opennlp.tools.sentdetect SentenceModel]
      [opennlp.tools.tokenize TokenizerModel]
      [opennlp.tools.postag POSModel])
    (:require [clojure.java.io :as io]))

(def models
  {"en-token.bin"    "https://dlcdn.apache.org/opennlp/models/en-token.bin"
   "en-sent.bin"     "https://dlcdn.apache.org/opennlp/models/en-sent.bin"
   "en-pos-maxent.bin" "https://dlcdn.apache.org/opennlp/models/en-pos-maxent.bin"})

(def model-dir "resources/models")

(defn download-model [filename url]
      (let [file (io/file model-dir filename)]
           (when-not (.exists file)
                     (println (str "⬇ Downloading model: " filename))
                     (with-open [in (io/input-stream (java.net.URL. url))
                                 out (io/output-stream file)]
                                (io/copy in out))
                     (println (str "✔ Download complete: " filename)))))

(defn ensure-models []
      (println "Downloading required OpenNLP models...")
      (.mkdirs (io/file model-dir))
      (doseq [[filename url] models]
             (download-model filename url)))
