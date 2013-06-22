(ns asciidoc-to-markdown.core)

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

(defn source [text]
  (clojure.string/replace text
                          #"\[source,(.*?)\]\n----\n(.*?\n+.*?)\n----"
                          "```$1\n$2```\n"))

(defn inline-code [text]
  (clojure.string/replace text
                          #"\+(.*?)\+"
                          "`$1`"))

(defn -main [& [input output]]
  (spit output (-> input
                   slurp
                   source
                   title
                   inline-code)))