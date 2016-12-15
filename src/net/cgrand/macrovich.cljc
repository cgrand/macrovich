(ns net.cgrand.macrovich
  (:refer-clojure :exclude [case]))

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

(defmacro case [& {:keys [cljs clj]}]
  (if (contains? &env '&env)
    `(if (:ns ~'&env) ~cljs ~clj)
    (if #?(:clj (:ns &env) :cljs true)
      cljs
      clj)))
