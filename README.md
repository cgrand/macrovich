# Macrovich

Because any macros problem can be solved by another level of macros, Macrovich is a set of four macros to ease writing `*.cljc` supporting Clojure, Clojurescript and self-hosted Clojurescript.

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

Clojurescript >= 1.9.293 is required. This means Planck 2.0.0 or later is required. Lumo 1.0.0 is ok.

Add `[net.cgrand/macrovich "0.2.0"]` to your dependencies.

Macrovich exposes four macros:

 * `macros/deftime` and `macros/usetime` to clearly demarcate regions of code that should be run in the macro-definition stage or in the macro-usage stage. (In Clojure there's no distinction; in pure Clojurescript it's easy: just wrap the first stage in `#?(:clj ...)` and the latter one in `#?(:cljs ...)`; in self-hosted Clojurescript it's messy or everything gets evaluated twice; supporting the three at the same time is Macrovich's _raison d'être_.)
 * `macros/case` is a macro to use instead of reader conditionals in macros or macros-supporting fns. This solves a problem with regular Clojurescript where macros are Clojure code and thus are read by taking the `:clj` branch of conditionals. So `macros/case` is like reader conditionals except the branch is picked at expansion time and not at definition time.
 * `macros/replace` is a macro to avoid repeating similar reader conditionals, see https://github.com/cgrand/xforms/blob/d4f0280bb50d8cc53c3a5dfe24b17fe7701b4e43/src/net/cgrand/xforms.cljc#L276 for an example.

## Sample

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

`case` allows to select which form to emit in a macro based on the *target language* rather than the *macro language*. Consider these two macros:

```clj
(defmacro broken []
  #?(:clj "clojure" :cljs "clojurescript"))

(defmacro correct []
  (macros/case :clj "clojure" :cljs "clojurescript"))

; or
(defmacro correct []
  `(macros/case :clj "clojure" :cljs "clojurescript")) ; this works too, so no need to unquote in the middle of a syntax quotation and mess with gensyms
```

In regular (Clojure-hosted) Clojurescript `(broken)` expands to `"clojure"` while `(correct)` expands to `"clojurescript"`.

## Example

The xforms lib has been converted to cljc:

 * https://github.com/cgrand/xforms/blob/cljc/src/net/cgrand/xforms.cljc
 * https://github.com/cgrand/xforms/blob/cljc/src/net/cgrand/xforms/rfs.cljc
 * https://github.com/cgrand/xforms/blob/cljc/test/net/cgrand/xforms_test.clj


## License

Copyright © 2016-2017 Christophe Grand

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
