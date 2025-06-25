# Java-kanban (Yandex Practicum Java Course)

![Java Logo](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)

## Project Description

This project is the backend implementation for a task tracker application, developed as part of Yandex Practicum Java Course. The application is designed to help users organize their work by creating, managing, and tracking tasks, including complex, multi-stage projects (Epics) with their individual subtasks.

The backend provides a robust data model and a set of functionalities to manage different types of tasks effectively, ensuring data integrity and logical progression of work.

## Key Features

* **Task Management:**
    * Create, retrieve, update, and delete (CRUD) operations for three types of tasks:
        * **Tasks:** Simple, standalone tasks.
        * **Epics:** Large tasks that can be broken down into multiple subtasks.
        * **Subtasks:** Smaller, dependent tasks that belong to a specific Epic.
    * Unique ID generation for all task types.
* **Status Management:**
    * Tasks and Subtasks have explicit statuses (`NEW`, `IN_PROGRESS`, `DONE`).
    * Epic statuses are automatically calculated based on the statuses of their associated Subtasks:
        * `NEW`: If an Epic has no Subtasks or all Subtasks are `NEW`.
        * `DONE`: If all Subtasks are `DONE`.
        * `IN_PROGRESS`: In all other cases (mixed statuses or at least one `IN_PROGRESS` Subtask).
* **Hierarchical Structure:**
    * Each Subtask is linked to a parent Epic.
    * Epics maintain a list of their Subtask IDs.
* **Task History (Viewing History):**
    * A dedicated `HistoryManager` tracks recently viewed tasks, epics, and subtasks.
    * The history currently stores up to 10 last viewed items, allowing duplicates.
* **Architectural Principles:**
    * **Dependency Injection:** `InMemoryTaskManager` receives `HistoryManager` via its constructor.
    * **Interface-based Programming:** Utilizes interfaces (`TaskManager`, `HistoryManager`) to decouple implementation details from core logic, allowing for future extensions (e.g., file-backed or database storage) without modifying client code.
    * **Singleton Pattern:** The `Managers` utility class ensures that only one instance of `TaskManager` and `HistoryManager` is used throughout the application, promoting consistent state management.
* **Unit Testing:**
    * Comprehensive unit tests implemented with JUnit 5 to ensure the correctness and robustness of all core functionalities, including:
        * `equals()` and `hashCode()` contract adherence for `Task` and its subclasses.
        * Correct CRUD operations for all task types.
        * Accurate Epic status calculation.
        * Proper history management logic.
        * Verification of manager initialization and singleton behavior.

## Technologies Used

* Java (JDK 21)
* Object-Oriented Programming (OOP) Principles
* Data Structures (HashMap, ArrayList)
* JUnit 5 (for unit testing)
* Git / GitHub (for version control)
* IntelliJ IDEA (IDE)

## How to Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/NikolaiMurtazin/java-kanban.git
    ```
2.  **Open in IntelliJ IDEA:**
    * Open the project in IntelliJ IDEA. If prompted, import it as a Maven/Gradle project.
    * If you are managing dependencies manually, ensure all required JUnit JARs are added to the module dependencies (`File -> Project Structure -> Modules -> Dependencies`).
3.  **Run the Main class:**
    * Navigate to `src/main/Main.java`.
    * Right-click on the `Main` class and select `Run 'Main.main()'`.
    * The console output will demonstrate the functionality of the task manager.
4.  **Run Tests:**
    * Navigate to `src/test/`.
    * Right-click on any test class (e.g., `InMemoryTaskManagerTest.java`) and select `Run Tests`.
