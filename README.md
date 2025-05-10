
# Siemens Java Internship – Code Refactoring Project

## 🔍 About the Project

This project was developed as part of the Java Developer - Trainee program at Siemens.
The main objective was to refactor an existing Spring Boot application, identify and fix logical issues, add proper validation, and ensure full test coverage using JUnit and MockMvc.

---

## ✅ Key Improvements

- Cleaned up and **refactored the asynchronous method** `processItemsAsync()` to make it truly parallel, thread-safe, and correctly return processed items.
- Added full **validation** to the `Item` entity using annotations like `@NotBlank` and `@Email`.
- Ensured **error handling** using `BindingResult` in controllers.
- Added full **unit and integration tests**:
  - ✅ Create item (valid + invalid)
  - ✅ Get item by ID (existing + non-existing)
  - ✅ Update, delete item
  - ✅ Async `/process` endpoint with `asyncDispatch`
- Added **test assertions for response status codes**, ensuring all REST operations behave correctly.
- Applied **clear, human-readable documentation** throughout the codebase.

---

## 🧪 How to Run the Project

This is a Maven project using Spring Boot and H2 in-memory database.

To run the tests:

```
./mvnw.cmd clean test
```

You can also open the project in IntelliJ or VS Code with Java 17+ installed.

---

## 📂 Project Structure

- `ItemController` – handles HTTP requests
- `ItemService` – core business logic
- `ItemRepository` – database layer (Spring Data JPA)
- `Item.java` – the main entity with validation rules
- `InternshipApplicationTests` – context and service smoke tests
- `ItemControllerTest` – REST API unit tests
- `ItemProcessTest` – async processing logic test

---

## ✅ Siemens Requirements – Covered

- ✔️ Fix all logical errors while maintaining functionality
- ✔️ Implement proper error handling and validation
- ✔️ Be well-documented with clear, concise comments
- ✔️ Write test functions with full coverage
- ✔️ Use correct HTTP status codes
- ✔️ Add email validation
- ✔️ Refactor `processItemsAsync()` to be truly async and return accurate results

---

## 🧾 Notes

- The project uses Spring Boot 3+ (Jakarta namespace).
- Validation powered by `hibernate-validator`.
- Async processing is fully tested using `MockMvc` with `asyncDispatch`.

---

## ✍️ Author

Refactored and tested by  
**Tomoiagă Vasile**  
Cluj-Napoca, Mai 2025

---

## 📌 GitHub Submission

To submit this, fork the original Siemens repository and push this project under your GitHub account.

Good luck and thank you!
