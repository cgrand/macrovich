# Macrovich

Because any macros problem can be solved by another level of macros, Macrovich is a set of three macros to ease writing `*.cljc` supporting Clojure, Clojurescript and self-hosted Clojurescript.

Excerpt from *Being John Macrovich* script:
<dl>
<dt>Girl Macrovich
<dd>Macrovich Macrovich Macrovich Macrovich...
<dd><i>(Macrovich looks confused. The Macrovich waiter approaches, pen and pad in hand, ready to take their orders.)</i>
<dt>Waiter Macrovich
<dd>Macrovich Macrovich Macrovich?
<dt>Girl Macrovich
<dd>Macrovich Macrovich Macrovich Macrovich.
<dt>Waiter Macrovich
<dd>Macrovich Macrovich. <i>(Turning to Macrovich)</i> Macrovich?
</dl>

## Usage

Warning: this require a Clojurescript build from master else you get a StackOverflow on the recursive `require-macros`.

Below is a sample `being-john.cljc` file:

```clj
(ns being-john
  #?(:clj
     (:require [net.cgrand.macrovich :as macros])
     :cljs
     (:require-macros [net.cgrand.macrovich :as macros]
        [net.cgrand.being :refer [add]]))) ; cljs must self refer macros

(macros/deftime
  ; anything inside a deftime block will only appear at the macro compilation stage.

  (defmacro add
    [a b]
    `(+ ~a ~b)))

(macros/usetime
  ; anything inside a usetime block will not appear at the macro compilation stage.

  (defn sum
    [a b]
    (add a b)))
    
; anything outside these block is always visible as usual
```

There's a third macro in Macrovich: `case` which allows to select which form to emit in a macro based on the *target language* rather than the *macro language*. Consider these two macros:

```clj
(defmacro broken []
  #?(:clj "clojure" :cljs "clojurescript"))

(defmacro correct []
  `(macros/case :clj "clojure" :cljs "clojurescript")) ; don't forget the quote!
```

In regular (Clojure-hosted) Clojurescript `(broken)` expands to `"clojure"` while `(correct)` expands to `"clojurescript"`.

## License

Copyright © 2016 Christophe Grand

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
