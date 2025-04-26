# Daily Journal

A terminal-based daily journaling application built with Clojure.

## Features

- Create daily journal entries with prompts for reflection
- View all entries with color-coded formatting
- Search entries by content or date range
- Edit existing entries
- Delete entries
- Unique entry IDs in JFAyyyymmdd format

## Prerequisites

- Java Runtime Environment (JRE) 8 or higher
- Leiningen (for development and building)

## Installation

### Option 1: Run from Source with Leiningen

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/daily-journal.git
    cd daily-journal
    ```

2. Run the application:
    ```bash
    lein run
    ```

### Option 2: Build and Run Standalone JAR

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/daily-journal.git
    cd daily-journal
    ```

2. Build the uberjar:
    ```bash
    lein uberjar
    ```

3. Run the compiled JAR:
    ```bash
    java -jar target/uberjar/daily-journal-0.1.0-SNAPSHOT-standalone.jar
    ```

## Usage

After starting the application, you'll see a menu with the following options:

1. Create a new entry
2. View all entries
3. Search entries
4. Edit an entry
5. Delete an entry
6. Exit

### Creating an Entry

When creating a new entry, you'll be prompted to answer:
- How you're feeling today
- What you did well
- What you want to focus on tomorrow
- How you plan to improve

Each entry is automatically dated and assigned a unique ID in the format `JFAyyyymmdd`.

### Viewing Entries

Entries are displayed in reverse chronological order (newest first), with color-coded formatting for easy reading.

### Searching Entries

You can search by:
- Content across all fields
- Date range

### Editing and Deleting

Entries can be selected by their number in the list for editing or deletion.

## Development

### Project Structure

- `src/daily_journal/core.clj` - Main application code
- `project.clj` - Project configuration and dependencies

### Key Technologies

- Clojure - Functional programming language
- Leiningen - Build tool
- EDN (Extensible Data Notation) - Data storage format

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
