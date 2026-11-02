# LedgerLock: High-Concurrency Digital Wallet Architecture

## 1. Problem Statement
In financial backend systems (like a digital wallet or ledger), concurrent transactions pose a critical risk to data integrity. When dealing with high throughput, two major concurrency anomalies occur:

*   **The "Lost Update" Anomaly (Race Condition):** If two users simultaneously attempt to withdraw or transfer funds from the same account, the application threads can read the same starting balance before either has saved their deduction. This results in the database incorrectly overwriting balances and losing money.
*   **Deadlocks:** If two accounts attempt concurrent cross-transfers (A $\rightarrow$ B and B $\rightarrow$ A), standard row-level database locking can result in an infinite circular wait state, causing the transactions to fail completely and throwing `500 Internal Server Error` exceptions.

## 2. The Solution
To guarantee 100% ACID compliance and prevent these concurrency failures, the system implements two specific database and application-level strategies:

*   **Pessimistic Database Locking:** We utilize `SELECT ... FOR UPDATE` row-level locking. This explicitly freezes the targeted rows in the PostgreSQL database at the query level. Concurrent threads are forced to wait in a queue rather than overwriting each other in application memory.
*   **Consistent Lock Ordering:** To prevent the Deadlock vulnerability during cross-transfers, the application algorithmically sorts the Wallet IDs alphabetically before acquiring the locks. This guarantees that regardless of the transaction direction, the database always locks the rows in the exact same sequence (e.g., always locking Wallet A before Wallet B), entirely eliminating the circular wait condition.

## 3. Implementation Strategy (The Tech Stack & Execution)
We engineered a production-ready "Walking Skeleton" using the modern Java ecosystem:

1.  **Database Infrastructure (Docker & PostgreSQL):** We containerized a PostgreSQL instance using `docker-compose` for an isolated, repeatable environment.
2.  **Schema Versioning (Flyway):** Instead of relying on Hibernate auto-generation, we used Flyway Migrations (`V1__init_wallet_schema.sql`) to design a highly normalized schema featuring `NUMERIC(19,4)` for exact monetary precision and `UUID`s to prevent IDOR vulnerabilities.
3.  **Data Access Layer (Spring Data JPA):** We paired JPQL queries with the `@Lock(LockModeType.PESSIMISTIC_WRITE)` annotation to safely bind parameters (preventing SQL injection) and instruct Hibernate to issue the locking SQL queries.
4.  **Business Logic (Spring Boot Service):** We wrapped the transfer logic in a strict `@Transactional` boundary. The service orders the locks, mathematically adjusts the `BigDecimal` balances, and inserts an immutable audit record into a `transaction_history` table within a single transaction.
5.  **Load Testing (Postman):** We used Postman's Collection Runner to simulate high-concurrency stress testing, successfully firing off virtual users simultaneously transferring funds back and forth to prove the lock ordering prevents database exceptions under load.

## **DeadLock Testing By 50 concurrent transactions:**

<img width="1068" height="472" alt="image" src="https://github.com/user-attachments/assets/4a7788c4-9626-4d9e-987d-f771a3214d40" />

## **Performance Testing**

<img width="1085" height="920" alt="image" src="https://github.com/user-attachments/assets/e925807d-1145-4eb3-afeb-75715de72bd3" />

