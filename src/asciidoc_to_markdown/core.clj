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
                          #"\[source,(.*?)\]\n----\n(.*?\n+.*?)\n----"
                          (if (first jekyll)
                            "{% highlight $1 %}\n$2\n{% endhighlight %}"
                            "``` $1\n$2\n```")))

(defn inline-code [text]
  (clojure.string/replace text
                          #"\+(.*?)\+"
                          "`$1`"))

(defn -main [input output & args]
  (let [[args opts banner]
        (cli args
             ["-h" "--help" "Show help" :default false]
             ["-j" "--jekyll" "Make jekyll ready markdown file" :flag true :default true])]
    (println args opts banner)
    (println (:jekyll args))
    (spit output (-> input
                     slurp
                     (source (:jekyll args))
                     title
                     inline-code))))