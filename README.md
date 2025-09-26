# Java Kanban (Yandex Practicum Java Course)

![Java](https://img.shields.io/badge/Java-21-red?logo=java)
![HTTP](https://img.shields.io/badge/HTTP-API-blue)
![Gson](https://img.shields.io/badge/JSON-Gson-orange)
![JUnit5](https://img.shields.io/badge/JUnit-5-green)
![Git](https://img.shields.io/badge/GitHub-Repo-black?logo=github)
![IntelliJ IDEA](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-black?logo=intellij-idea)

---

## 📌 Project Description

**Java Kanban** is a backend implementation of a task tracker application,  
developed as part of the **Yandex Practicum Java Developer Course**.

The app helps users organize their work by creating, managing, and tracking tasks,  
including **Epics** (multi-stage projects) and their **Subtasks**.

Over time the project evolved to support:  
✅ File persistence  
✅ Task prioritization with overlap prevention  
✅ Full HTTP API with JSON serialization

---

## ✨ Key Features

- **Task Management**
    - CRUD operations for three task types:
        - `Task` — simple, standalone task.
        - `Epic` — larger task grouping multiple subtasks.
        - `Subtask` — dependent task linked to an epic.
    - Unique ID generation.

- **Status Management**
    - `Task` and `Subtask` statuses: `NEW`, `IN_PROGRESS`, `DONE`.
    - `Epic` status is calculated automatically based on its subtasks.

- **Hierarchical Structure**
    - Each `Subtask` references a parent `Epic`.
    - `Epic` stores a list of its `subtaskIds`.

- **History Management**
    - `HistoryManager` tracks recently viewed tasks.
    - Implemented with a linked list for **O(1) removal** and no duplicates.

- **Persistence**
    - `InMemoryTaskManager` — in-memory storage.
    - `FileBackedTaskManager` — CSV-based storage and recovery.

- **Prioritized Tasks**
    - Tasks stored in a `TreeSet`, sorted by `startTime`.
    - Overlap prevention via `hasOverlapping`.

- **HTTP API**
    - Built-in `HttpServer` running on port **8080**.
    - JSON serialization with `Gson` (custom adapters for `Duration` and `LocalDateTime`).
    - Endpoints:
        - `/tasks`
        - `/epics`
        - `/subtasks`
        - `/subtasks/epic?id=X`
        - `/history`
        - `/prioritized`

- **Error Handling**
    - `200` — success
    - `201` — created
    - `404` — not found
    - `406` — task overlaps with existing
    - `500` — server error

- **Unit Testing**
    - JUnit 5 tests for managers, history, epic status logic, and HTTP API.

---

## 🛠 Technologies Used

- Java 21
- Collections API (`HashMap`, `ArrayList`, `TreeSet`)
- OOP, Generics, Enums
- `com.sun.net.httpserver.HttpServer`
- Gson (JSON serialization)
- JUnit 5 (unit testing)
- Git / GitHub
- IntelliJ IDEA

---

## 🚀 How to Run

### 1. Clone the repository
```bash
git clone https://github.com/NikolaiMurtazin/java-kanban.git
cd java-kanban
