(ns hxgm30.almanac.event.tag)

(def day-transition ::day-transition)
(def year-transition ::year-transition)

(def subscribers
  {:world day-transition [:default]
   :world year-transition [:default]})
