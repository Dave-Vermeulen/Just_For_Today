(ns just-for-today.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:gen-class))

;; Data Storage Path
(def journal-file "journal-entries.edn")

;; Entry structure
(defn create-entry [date feeling achievements focus improvement]
  {:date date
   :feeling feeling
   :achievements achievements
   :focus focus
   :improvement improvement})

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

;; Display a single entry
(defn display-entry [date entry]
  (println "\n===== Journal Entry:" date "=====")
  (println "Feeling:" (:feeling entry))
  (println "Achievements:" (:achievements entry))
  (println "Tomorrow's Focus:" (:focus entry))
  (println "Plan for Improvement:" (:improvement entry))
  (println "==================================="))

;; Display all entries
(defn display-all-entries [entries]
  (if (empty? entries)
    (println "\nNo journal entries found.")
    (doseq [[date entry] (sort entries)]
      (display-entry date entry))))

;; Create a new entry
(defn new-entry []
  (let [date (today)]
    (println "\n--- New Journal Entry for" date "---")
    (println "How are you feeling today?")
    (let [feeling (read-line)
          _ (println "What did you do well today?")
          achievements (read-line)
          _ (println "What do you want to focus on tomorrow?")
          focus (read-line)
          _ (println "How will you improve?")
          improvement (read-line)
          entry (create-entry date feeling achievements focus improvement)
          entries (load-entries)
          updated-entries (assoc entries date entry)]
      (save-entries updated-entries)
      (println "\nEntry saved successfully!")
      (display-entry date entry))))

;; Edit an existing entry
(defn edit-entry []
  (let [entries (load-entries)]
    (if (empty? entries)
      (println "\nNo entries to edit.")
      (do
        (println "\n--- Available entries ---")
        (doseq [date (sort (keys entries))]
          (println date))
        (println "\nEnter date to edit (YYYY-MM-DD):")
        (let [date (read-line)]
          (if (contains? entries date)
            (let [entry (get entries date)]
              (display-entry date entry)
              (println "\nHow are you feeling today? (Current:" (:feeling entry) ")")
              (let [feeling (read-line)
                    feeling (if (str/blank? feeling) (:feeling entry) feeling)
                    _ (println "What did you do well today? (Current:" (:achievements entry) ")")
                    achievements (read-line)
                    achievements (if (str/blank? achievements) (:achievements entry) achievements)
                    _ (println "What do you want to focus on tomorrow? (Current:" (:focus entry) ")")
                    focus (read-line)
                    focus (if (str/blank? focus) (:focus entry) focus)
                    _ (println "How will you improve? (Current:" (:improvement entry) ")")
                    improvement (read-line)
                    improvement (if (str/blank? improvement) (:improvement entry) improvement)
                    updated-entry (create-entry date feeling achievements focus improvement)
                    updated-entries (assoc entries date updated-entry)]
                (save-entries updated-entries)
                (println "\nEntry updated successfully!")
                (display-entry date updated-entry)))
            (println "\nEntry not found for date:" date)))))))

;; Delete an entry
(defn delete-entry []
  (let [entries (load-entries)]
    (if (empty? entries)
      (println "\nNo entries to delete.")
      (do
        (println "\n--- Available entries ---")
        (doseq [date (sort (keys entries))]
          (println date))
        (println "\nEnter date to delete (YYYY-MM-DD):")
        (let [date (read-line)]
          (if (contains? entries date)
            (do
              (println "\nAre you sure you want to delete the entry for" date "? (y/n)")
              (let [confirm (read-line)]
                (if (= (str/lower-case confirm) "y")
                  (let [updated-entries (dissoc entries date)]
                    (save-entries updated-entries)
                    (println "\nEntry deleted successfully!"))
                  (println "\nDeletion cancelled."))))
            (println "\nEntry not found for date:" date)))))))

;; Main menu
(defn display-menu []
  (println "\n=== Daily Journal ===")
  (println "1. Create a new entry")
  (println "2. View all entries")
  (println "3. Edit an entry")
  (println "4. Delete an entry")
  (println "5. Exit")
  (print "\nChoose an option: ")
  (flush))

;; Main function
(defn -main []
  (println "\nWelcome to your Daily Journal!")
  (loop []
    (display-menu)
    (let [choice (read-line)]
      (case choice
        "1" (do (new-entry) (recur))
        "2" (do (display-all-entries (load-entries)) (recur))
        "3" (do (edit-entry) (recur))
        "4" (do (delete-entry) (recur))
        "5" (println "\nThank you for journaling today. Goodbye!")
        (do (println "\nInvalid option. Please try again.") (recur))))))
