(ns hxgm30.almanac.config
  (:require
   [hxgm30.common.file :as common]))

(def config-file "hexagram30-config/almanac.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (common/read-edn-resource filename)))
