(ns ui.core
  (:require [goog.dom :as gdom]
            [cljs.pprint]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(def init-data
  {:list/one [{:name "John" :points 0}
              {:name "Mary" :points 0}
              {:name "Bob"  :points 0}]
   :list/two [{:name "Mary" :points 0 :age 27}
              {:name "Gwen" :points 0}
              {:name "Jeff" :points 0}]})

(defmulti read om/dispatch)

(defn get-people [state key]
  (let [st @state]
    (into [] (map :name) (get st key))))

(defmethod read :list/one
  [{:keys [state] :as env} key params]
  (do
    (cljs.pprint/pprint
      {:state @state
       :key key})
  {:value (get-people state key)}))

(defmethod read :list/two
  [{:keys [state] :as env} key params]
  {:value (get-people state key)})

(defui Person
  static om/Ident
  (ident [this {:keys [name]}]
    [:person/by-name name])
  static om/IQuery
  (query [this]
    '[:name :points]))

(defui RootView
  static om/IQuery
  (query [this]
    (let [subquery (om/get-query Person)]
     `[{:list/one ~subquery} {:list/two ~subquery}])))

(def norm-data (om/tree->db RootView init-data true))

(def parser (om/parser {:read read}))

(cljs.pprint/pprint (parser {:state (atom norm-data)} '[:list/one]))
