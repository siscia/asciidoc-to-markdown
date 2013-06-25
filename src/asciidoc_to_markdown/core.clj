(ns asciidoc-to-markdown.core
  (:use [clojure.tools.cli :only [cli]]))

(def titles-regex
  (sorted-map-by #(> (count %1) (count %2))
                 "###### $1" #"====== +([^ \t\n\r\f\v].*?)"
                 "##### $1" #"===== +([^ \t\n\r\f\v].*?)"
                 "#### $1" #"==== +([^ \t\n\r\f\v].*?)"
                 "### $1" #"=== +([^ \t\n\r\f\v].*?)"
                 "## $1" #"== +([^ \t\n\r\f\v].*?)"
                 "# $1" #"= +([^ \t\n\r\f\v].*?)"))

(defn title [text]
  (reduce (fn [text regex]
            (clojure.string/replace text (get titles-regex regex) regex))
          text
          (keys titles-regex)))

(defn source [text & jekyll]
  (clojure.string/replace text
                          ;#"\[source,(.*?)\]\n----\n(.*?\n*+.*)\n----"
                          ;#"\[source,(.*?)\]\n----\n([\s\S])\n----"
                          ;#"\[source,(.*)\][\s\S]----[\s\S](?s:(.*))----"
                          ;#"\[source,(.*)\][\s\S]----[\s\S]([^-{4}]+)"
                          ;#"\[source,(.*)\][\s\S]----[\s\S]^(?:(?!----).)+$"
                          #"\[source,(.*)\][\s\S]----[\s\S](?s:(.*?))----"
                          (if (first jekyll)
                            "{% highlight $1 %}\n$2{% endhighlight %}"
                            "``` $1\n$2```")))

(defn inline-code [text]
  (clojure.string/replace text
                          #"\+(.*?)\+"
                          "`$1`"))

(defn meta-info [text]
  (clojure.string/replace text
                          #"^.*(Author:|Email:|Date:|Revision:).*$"
                          ""))

(defn -main [input output & args]
  (let [[args opts banner]
        (cli args
             ["-h" "--help" "Show help" :default false]
             ["-j" "--jekyll" "Make jekyll ready markdown file" :flag true :default true])]
    ;(println args opts banner)
    ;(println (:jekyll args))
    (spit output (-> input
                     slurp
                     (source (:jekyll args))
                     title
                     inline-code
                     ;meta-info
                     ))))