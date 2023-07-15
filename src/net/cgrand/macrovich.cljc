(ns net.cgrand.macrovich
  (:refer-clojure :exclude [case replace])
  #?(:cljs (:require-macros net.cgrand.macrovich)))

(defmacro deftime
  "This block will only be evaluated at the correct time for macro definition, at other times its content
   are removed.
   For Clojure it always behaves like a `do` block.
   For Clojurescript/JVM the block is only visible to Clojure.
   For self-hosted Clojurescript the block is only visible when defining macros in the pseudo-namespace."
  [& body]
  (when #?(:clj (not (:ns &env)) :cljs (re-matches #".*\$macros" (name (ns-name *ns*))))
    `(do ~@body)))

(defmacro usetime
  "This block content is not included at macro definition time.
   For Clojure it always behaves like a `do` block.
   For Clojurescript/JVM the block is only visible to Clojurescript.
   For self-hosted Clojurescript the block is invisible when defining macros in the pseudo-namespace."
  [& body]
  (when #?(:clj true :cljs (not (re-matches #".*\$macros" (name (ns-name *ns*)))))
    `(do ~@body)))

(defmacro case [& {:keys [cljd cljs clj]}]
  (cond
    (contains? &env '&env)
    `(cond (:ns ~'&env) ~cljs (:nses ~'&env) ~cljd :else ~clj)
    #?(:clj (:ns &env) :cljs true) cljs
    #?(:clj (:nses &env) :cljd true) cljd
    :else clj))

(defmacro replace [map-or-maps & body]
  (let [smap (if (map? map-or-maps) map-or-maps (reduce into {} map-or-maps))
        walk (fn walk [form]
               (cond
                 (contains? smap form) (smap form)
                 (map? form) (with-meta
                               (into (empty form)
                                 (for [[k v] form]
                                   [(walk k) (walk v)]))
                               (meta form))
                 (seq? form) (with-meta
                                (map walk form)
                                (meta form))
                 (coll? form) (with-meta
                                (into (empty form) (map walk) form)
                                (meta form))
                 :else form))]
    `(do ~@(map walk body))))
