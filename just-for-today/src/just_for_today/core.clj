(ns just-for-today.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:gen-class))

;; ANSI color codes for terminal output
(def color-reset "\u001B[0m")
(def color-blue "\u001B[34m")
(def color-green "\u001B[32m")
(def color-yellow "\u001B[33m")
(def color-cyan "\u001B[36m")
(def color-magenta "\u001B[35m")
(def color-red "\u001B[31m")

;; Data Storage Path
(def journal-file "journal-entries.edn")

;; Entry structure
(defn create-entry [date feeling achievements focus improvement]
  {:date date
   :feeling feeling
   :achievements achievements
   :focus focus
   :improvement improvement})

;; generate entry
(defn generate-entry-id [date]
  (let [date-parts (str/split date #"-")
        year (first date-parts)
        month (second date-parts)
        day (last date-parts)]
    (str "JFA" year month day)))

;; Save entries to file
(defn save-entries [entries]
  (with-open [w (io/writer journal-file)]
    (binding [*out* w]
      (pr entries))))

;; Load entries from file
(defn load-entries []
  (try
    (if (.exists (io/file journal-file))
      (edn/read-string (slurp journal-file))
      {})
    (catch Exception e
      (println "Error loading journal entries:" (.getMessage e))
      {})))

;; Get today's date as a string
(defn today []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.)))

;; Convert date string to Date object for comparison
(defn parse-date [date-str]
  (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") date-str))

;; Display a single entry
(defn display-entry [date entry]
  (let [entry-id (generate-entry-id date)]
    (println (str "\n" color-cyan "═══════════════════════════════════════════" color-reset))
    (println (str color-blue "║ " entry-id ": Journal Entry " date color-reset))
    (println (str color-cyan "───────────────────────────────────────────" color-reset))
    (println (str color-yellow "║ Feeling: " color-reset (:feeling entry)))
    (println (str color-green "║ Achievements: " color-reset (:achievements entry)))
    (println (str color-magenta "║ Tomorrow's Focus: " color-reset (:focus entry)))
    (println (str color-cyan "║ Plan for Improvement: " color-reset (:improvement entry)))
    (println (str color-cyan "═══════════════════════════════════════════" color-reset))))

;; Display all entries with formatting
(defn display-all-entries [entries]
  (if (empty? entries)
    (println (str "\n" color-yellow "No journal entries found." color-reset))
    (let [sorted-entries (into (sorted-map-by 
                               (fn [k1 k2] 
                                 (.after (parse-date k1) (parse-date k2))))
                               entries)]
      (println (str "\n" color-blue "┌─────────────────────────────────────────┐" color-reset))
      (println (str color-blue "│      JOURNAL ENTRIES - NEWEST FIRST      │" color-reset))
      (println (str color-blue "└─────────────────────────────────────────┘" color-reset))
      (doseq [[idx [date entry]] (map-indexed vector sorted-entries)]
        (println (str "\n" color-green "[" (inc idx) "] " (generate-entry-id date) color-reset))
        (display-entry date entry)))))

;; Search entries by content
(defn search-entries []
  (let [entries (load-entries)]
    (if (empty? entries)
      (println (str "\n" color-yellow "No entries to search." color-reset))
      (do
        (println (str "\n" color-cyan "=== Search Journal Entries ===" color-reset))
        (println "1. Search by content")
        (println "2. Search by date range")
        (println "3. Back to main menu")
        (print "\nChoose an option: ")
        (flush)
        (let [choice (read-line)]
          (case choice
            "1" (do
                  (println "\nEnter search term:")
                  (let [term (str/lower-case (read-line))
                        results (filter (fn [[_ entry]]
                                          (or (str/includes? (str/lower-case (:feeling entry)) term)
                                              (str/includes? (str/lower-case (:achievements entry)) term)
                                              (str/includes? (str/lower-case (:focus entry)) term)
                                              (str/includes? (str/lower-case (:improvement entry)) term)))
                                        entries)]
                    (if (empty? results)
                      (println (str "\n" color-yellow "No entries found matching your search." color-reset))
                      (do
                        (println (str "\n" color-green "Found " (count results) " matching entries:" color-reset))
                        (doseq [[date entry] (sort-by first results)]
                          (display-entry date entry))))))
            
            "2" (do
                  (println "\nEnter start date (YYYY-MM-DD) or leave blank for earliest:")
                  (let [start-date (read-line)
                        start-date (if (str/blank? start-date) "0000-00-00" start-date)]
                    (println "Enter end date (YYYY-MM-DD) or leave blank for today:")
                    (let [end-date (read-line)
                          end-date (if (str/blank? end-date) (today) end-date)
                          results (filter (fn [[date _]]
                                            (let [date-obj (parse-date date)
                                                  start-obj (try (parse-date start-date) (catch Exception _ (parse-date "0000-00-00")))
                                                  end-obj (try (parse-date end-date) (catch Exception _ (parse-date (today))))]
                                              (and (not (.before date-obj start-obj))
                                                   (not (.after date-obj end-obj)))))
                                          entries)]
                      (if (empty? results)
                        (println (str "\n" color-yellow "No entries found in that date range." color-reset))
                        (do
                          (println (str "\n" color-green "Found " (count results) " entries from " start-date " to " end-date ":" color-reset))
                          (doseq [[date entry] (sort-by first results)]
                            (display-entry date entry)))))))
            
            "3" nil
            (println (str "\n" color-red "Invalid option. Please try again." color-reset))))))))

;; Create a new entry
(defn new-entry []
  (let [date (today)]
    (println (str "\n" color-cyan "--- New Journal Entry for " date " ---" color-reset))
    (println (str "Entry ID: " color-green (generate-entry-id date) color-reset))
    (println (str color-yellow "How are you feeling today?" color-reset))
    (let [feeling (read-line)
          _ (println (str color-green "What did you do well today?" color-reset))
          achievements (read-line)
          _ (println (str color-magenta "What do you want to focus on tomorrow?" color-reset))
          focus (read-line)
          _ (println (str color-cyan "How will you improve?" color-reset))
          improvement (read-line)
          entry (create-entry date feeling achievements focus improvement)
          entries (load-entries)
          updated-entries (assoc entries date entry)]
      (save-entries updated-entries)
      (println (str "\n" color-green "Entry saved successfully!" color-reset))
      (display-entry date entry))))

;; Edit an existing entry
(defn edit-entry []
  (let [entries (load-entries)]
    (if (empty? entries)
      (println (str "\n" color-yellow "No entries to edit." color-reset))
      (do
        (println (str "\n" color-cyan "--- Available entries ---" color-reset))
        (doseq [[idx [date _]] (map-indexed vector (sort entries))]
          (println (str (inc idx) ". " (generate-entry-id date) " (" date ")")))
        (println "\nEnter number to edit:")
        (let [choice (read-line)
              entry-idx (try (dec (Integer/parseInt choice)) (catch Exception _ -1))]
          (if (and (>= entry-idx 0) (< entry-idx (count entries)))
            (let [date (first (nth (sort entries) entry-idx))
                  entry (get entries date)]
              (display-entry date entry)
              (println (str "\n" color-yellow "How are you feeling today? (Current: " (:feeling entry) ")" color-reset))
              (let [feeling (read-line)
                    feeling (if (str/blank? feeling) (:feeling entry) feeling)
                    _ (println (str color-green "What did you do well today? (Current: " (:achievements entry) ")" color-reset))
                    achievements (read-line)
                    achievements (if (str/blank? achievements) (:achievements entry) achievements)
                    _ (println (str color-magenta "What do you want to focus on tomorrow? (Current: " (:focus entry) ")" color-reset))
                    focus (read-line)
                    focus (if (str/blank? focus) (:focus entry) focus)
                    _ (println (str color-cyan "How will you improve? (Current: " (:improvement entry) ")" color-reset))
                    improvement (read-line)
                    improvement (if (str/blank? improvement) (:improvement entry) improvement)
                    updated-entry (create-entry date feeling achievements focus improvement)
                    updated-entries (assoc entries date updated-entry)]
                (save-entries updated-entries)
                (println (str "\n" color-green "Entry updated successfully!" color-reset))
                (display-entry date updated-entry)))
            (println (str "\n" color-red "Invalid selection." color-reset))))))))

;; Delete an entry
(defn delete-entry []
  (let [entries (load-entries)]
    (if (empty? entries)
      (println (str "\n" color-yellow "No entries to delete." color-reset))
      (do
        (println (str "\n" color-cyan "--- Available entries ---" color-reset))
        (doseq [[idx [date _]] (map-indexed vector (sort entries))]
          (println (str (inc idx) ". " (generate-entry-id date) " (" date ")")))
        (println "\nEnter number to delete:")
        (let [choice (read-line)
              entry-idx (try (dec (Integer/parseInt choice)) (catch Exception _ -1))]
          (if (and (>= entry-idx 0) (< entry-idx (count entries)))
            (let [date (first (nth (sort entries) entry-idx))]
              (println (str "\n" color-yellow "Are you sure you want to delete the entry " 
                           (generate-entry-id date) " for " date "? (y/n)" color-reset))
              (let [confirm (read-line)]
                (if (= (str/lower-case confirm) "y")
                  (let [updated-entries (dissoc entries date)]
                    (save-entries updated-entries)
                    (println (str "\n" color-green "Entry deleted successfully!" color-reset)))
                  (println (str "\n" color-yellow "Deletion cancelled." color-reset)))))
            (println (str "\n" color-red "Invalid selection." color-reset))))))))

;; Main menu
(defn display-menu []
  (println (str "\n" color-blue "┌─────────────────────────────────┐" color-reset))
  (println (str color-blue "│         DAILY JOURNAL           │" color-reset))
  (println (str color-blue "└─────────────────────────────────┘" color-reset))
  (println (str "1. " color-green "Create a new entry" color-reset))
  (println (str "2. " color-cyan "View all entries" color-reset))
  (println (str "3. " color-yellow "Search entries" color-reset))
  (println (str "4. " color-magenta "Edit an entry" color-reset))
  (println (str "5. " color-red "Delete an entry" color-reset))
  (println (str "6. " color-blue "Exit" color-reset))
  (print (str "\n" color-cyan "Choose an option: " color-reset))
  (flush))

;; Main function
(defn -main []
  (println (str "\n" color-green "Welcome to your Daily Journal!" color-reset))
  (loop []
    (display-menu)
    (let [choice (read-line)]
      (case choice
        "1" (do (new-entry) (recur))
        "2" (do (display-all-entries (load-entries)) (recur))
        "3" (do (search-entries) (recur))
        "4" (do (edit-entry) (recur))
        "5" (do (delete-entry) (recur))
        "6" (println (str "\n" color-green "Thank you for journaling today. Goodbye!" color-reset))
        (do (println (str "\n" color-red "Invalid option. Please try again." color-reset)) (recur))))))
