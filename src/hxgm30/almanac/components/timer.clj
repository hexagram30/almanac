(ns hxgm30.almanac.components.timer
  (:require
    [clojure.core.async :as async]
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.almanac.components.config :as config]
    [hxgm30.almanac.const :as const]
    [hxgm30.almanac.event.tag :as tag]
    [hxgm30.event.components.pubsub :as pubsub]
    [hxgm30.event.message :as message]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Globals & Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:dynamic *day-period-counter* (atom 0))
(def ^:dynamic *season-counter* (atom 0))

(defn create-timer
  [component period event-type side-effect-fn msg-fn]
  (async/go-loop []
    (do
      (side-effect-fn component)
      (pubsub/publish component :world event-type (msg-fn component))
      (async/<! (async/timeout (* 1000 period)))
      (recur))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Timer Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Day

(defn inc-day-period
  [_component]
  (swap! *day-period-counter* inc))

(defn get-day-period-counter
  [_component]
  @*day-period-counter*)

(defn get-day-division-index
  [component]
  (mod (get-day-period-counter component)
       (config/day-divisions component)))

(defn get-day-period
  [component]
  (/ const/day-seconds
     (config/day-divisions component)
     (config/time-multiplier component)))

(defn get-day-transition-msg
  [component]
  (config/day-transition
   component
   (get-day-division-index component)))

(defn create-day-timer
  [component]
  (create-timer component
                (get-day-period component)
                tag/day-transition
                inc-day-period
                get-day-transition-msg))

;; Year

(defn inc-year-period
  [_component]
  (swap! *season-counter* inc))

(defn get-year-period-counter
  [_component]
  @*season-counter*)

(defn get-year-division-index
  [component]
  (mod (get-year-period-counter component)
       (config/year-divisions component)))

(defn get-year-period
  [component]
  (/ const/year-seconds
     (config/year-divisions component)
     (config/time-multiplier component)))

(defn get-year-transition-msg
  [component]
  (config/year-transition
   component
   (get-year-division-index component)))

(defn create-year-timer
  [component]
  (create-timer component
                (get-year-period component)
                tag/year-transition
                inc-year-period
                get-year-transition-msg))

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
  (map->Timer
    {:day-period-counter (atom 0)
     :season-counter (atom 0)}))
