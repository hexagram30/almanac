(ns hxgm30.almanac.components.config
  (:require
   [com.stuartsierra.component :as component]
   [hxgm30.almanac.config :as config]
   [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn build-config
  []
  (config/data))

(defn- get-cfg
  [system]
  (get-in system [:config :data]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Config Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn day-divisions
  [system]
  (get-in (get-cfg system) [:almanac :event :day :divisions]))

(defn day-states
  [system]
  (get-in (get-cfg system) [:almanac :event :day :states]))

(defn day-transitions
  [system]
  (get-in (get-cfg system) [:almanac :event :day :transitions]))

(defn year-divisions
  [system]
  (get-in (get-cfg system) [:almanac :event :year :divisions]))

(defn year-states
  [system]
  (get-in (get-cfg system) [:almanac :event :year :states]))

(defn year-transitions
  [system]
  (get-in (get-cfg system) [:almanac :event :year :transitions]))

(defn time-multiplier
  [system]
  (get-in (get-cfg system) [:almanac :time :multiplier]))

(defn log-level
  [system]
  (get-in (get-cfg system) [:logging :level]))

(defn log-nss
  [system]
  (get-in (get-cfg system) [:logging :nss]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Config [data])

(defn start
  [this]
  (log/info "Starting config component ...")
  (log/debug "Using configuration:" (:data this))
  (log/debug "Started config component.")
  this)

(defn stop
  [this]
  (log/info "Stopping config component ...")
  (log/debug "Stopped config component.")
  (assoc this :data nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Config
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  [cfg-data]
  (map->Config {:data cfg-data}))
