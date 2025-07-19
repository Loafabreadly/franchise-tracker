# Franchise Tracker

[![GitHub commits](https://img.shields.io/github/commit-activity/y/Loafabreadly/franchise-tracker)](https://github.com/Loafabreadly/franchise-tracker/commits)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/Loafabreadly/franchise-tracker)](https://github.com/Loafabreadly/franchise-tracker)
[![GitHub last commit](https://img.shields.io/github/last-commit/Loafabreadly/franchise-tracker)](https://github.com/Loafabreadly/franchise-tracker)
[![GitHub issues](https://img.shields.io/github/issues/Loafabreadly/franchise-tracker)](https://github.com/Loafabreadly/franchise-tracker/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/Loafabreadly/franchise-tracker)](https://github.com/Loafabreadly/franchise-tracker/pulls)

---

## Overview

**Franchise Tracker** is a terminal-based application for managing and tracking the progress of a hockey franchise. It allows users to create, save, and load franchise states, manage rosters, draft picks, lines, and season stats, all from a text-based interface. The project is written in Java and uses the Lanterna library for the TUI.

### Features
- Create and save new franchise states
- Load and manage existing franchises
- Edit rosters, draft picks, and lines
- Enter and track end-of-season stats
- Save data in portable JSON format

### Getting Started

#### Prerequisites
- Java 17 or newer
- Gradle (or use the provided wrapper)

#### Build and Run
```sh
./gradlew shadowJar
java -jar build/libs/franchise-tracker-all.jar
```

#### Save Files
- Save files are stored in the current directory with a `.nhl` extension.
- To load a save, use the Load Franchise menu and select a file.

### Code Structure
- `src/main/java/com/github/loafabreadly/franchisetracker/` - Main application logic
- `src/main/java/com/github/loafabreadly/franchisetracker/model/` - Data models (Team, Player, etc.)
- `src/main/java/com/github/loafabreadly/franchisetracker/scene/` - UI scenes and panels
- `src/main/java/com/github/loafabreadly/franchisetracker/service/` - Data persistence and services

### Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### License
MIT License
