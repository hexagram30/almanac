(ns hxgm30.almanac.config
  (:require
   [hxgm30.common.file :as file]
   [hxgm30.common.util :as util]))

(def config-file "hexagram30-config/almanac.edn")
(def event-config-file "hexagram30-config/event.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (util/deep-merge
     (file/read-edn-resource event-config-file)
     (file/read-edn-resource filename))))
