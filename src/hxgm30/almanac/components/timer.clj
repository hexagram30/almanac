(ns hxgm30.almanac.components.timer
  (:require
    [clojure.core.async :as async]
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.almanac.components.config :as config]
    [hxgm30.almanac.const :as const]
    [hxgm30.almanac.event.tag :as tag]
    [hxgm30.event.components.pubsub :as pubsub]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-timer
  [system period event-type]
  (async/go-loop []
    (do
      (pubsub/publish system :world event-type {})
      (async/<! (async/timeout (* 1000 period)))
      (recur))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Timer Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-day-inc
  [system]
  )

(defn get-day-division-index
  [system]
  ; (mod (get-day-inc system) (config/day-divisions system))
  )

(defn get-day-period
  [system]
  (/ const/day-seconds
     (config/day-divisions system)
     (config/time-multiplier system)))

(defn create-day-timer
  [system]
  (create-timer system
                (get-day-period system)
                tag/day-transition))

(defn get-day-inc
  [system]
  )

(defn get-year-division-index
  [system]
  ; (mod (get-year-inc system) (config/year-divisions system))
  )

(defn get-year-period
  [system]
  (/ const/year-seconds
     (config/year-divisions system)
     (config/time-multiplier system)))

(defn create-year-timer
  [system]
  (create-timer system
                (get-year-period system)
                tag/year-transition))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Timer [day year])

(defn start
  [this]
  (log/info "Starting timer component ...")
  (log/debug "Started timer component.")
  (assoc this :day (create-day-timer this)
              :year (create-year-timer this)))

(defn stop
  [this]
  (log/info "Stopping timer component ...")
  (log/debug "Stopped timer component.")
  (assoc this :day nil
              :year nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Timer
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->Timer {}))
