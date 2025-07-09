# Banking Transaction System

A Java-based desktop application for managing banking transactions, featuring a Swing GUI and MySQL database integration. The system allows users to log in, create accounts, perform transactions (deposits and withdrawals), view account balances, and display transaction history, with a focus on user-friendly design and robust error handling.

## Features

- User authentication with login and account creation
- Account management (create savings/checking accounts, view balances)
- Transaction processing (deposits and withdrawals with balance validation)
- Transaction history display for the last 7 days
- Customer deletion with cascading record removal
- Responsive GUI with gradient styling, custom buttons, and clear error messages

## Technologies Used

- **Java**: Core logic and Swing for GUI
- **MySQL**: Relational database for data storage
- **JDBC**: Secure database connectivity
- **SQL**: Schema design with foreign keys and indexes

## Prerequisites

- Java JDK 8 or higher (e.g., OpenJDK or Oracle JDK)
- MySQL Server 5.7 or higher
- MySQL JDBC Driver (Connector/J)
- Optional: IDE like IntelliJ IDEA or Eclipse for easier compilation

## Setup Instructions

1. **Clone or Download the Repository**:

   ```bash
   git clone https://github.com/your-username/Banking-Transaction-System.git
   ```

   Or download the ZIP file from GitHub and extract it.

2. **Set Up MySQL Database**:

   - Create a MySQL database named `banking_db`.

   - Run the SQL script in `sql/banking_system.sql` to create tables and insert sample data:

     ```bash
     mysql -u your-username -p banking_db < sql/banking_system.sql
     ```

   - Or execute the script in MySQL Workbench or another SQL client.

3. **Configure Database Connection**:

   - Open `src/BankingTransactionSystem.java` and update the credentials:

     ```java
     private static final String URL = "jdbc:mysql://localhost:3306/banking_db";
     private static final String USER = "your-username";
     private static final String PASSWORD = "your-password";
     ```

   - Replace `your-username` and `your-password` with your MySQL credentials.

4. **Compile and Run the Application**:

   - **Using an IDE**:

     - Import the project.
     - Add the MySQL JDBC driver to the project dependencies.
     - Run `BankingTransactionSystem.java` as the main class.

   - **Using Command Line**:

     ```bash
     cd src
     javac BankingTransactionSystem.java
     java -cp .;mysql-connector-java.jar BankingTransactionSystem
     ```

     - Replace `mysql-connector-java.jar` with the path to your MySQL JDBC driver JAR (download from MySQL).
     - On Unix-based systems, use `:` instead of `;` in the classpath.

5. **Usage**:

   - Launch the application to access the login window.
   - Use sample credentials (e.g., username: `johndoe`, password: `password123`) or create a new account.
   - Navigate the dashboard to manage accounts and transactions.

## Database Schema

The project uses a MySQL database (`banking_db`) with the following tables:

- **Branch**: Stores branch details (`branch_id`, `branch_name`, `location`)
- **Customer**: Stores user details (`customer_id`, `first_name`, `last_name`, `address`, `phone`, `username`, `password`)
- **Account**: Stores account details (`account_number`, `username`, `branch_id`, `account_type`, `balance`) with foreign keys to `Customer` and `Branch`
- **Transaction**: Stores transaction history (`transaction_id`, `account_number`, `transaction_type`, `amount`, `transaction_date`) with a foreign key to `Account`

Key features:

- `ON DELETE CASCADE` constraints for automatic deletion of related records
- Indexes on `username`, `account_number`, and `transaction_date` for query performance
- Sample data included for testing

See `sql/banking_system.sql` for the full schema.

## Project Structure

```
Banking-Transaction-System/
├── src/
│   └── BankingTransactionSystem.java
├── sql/
│   └── banking_system.sql
├── README.md
├── .gitignore
└── LICENSE (optional)
```

## Screenshots

![Login Screen](screenshots/login.png)![Main Dashboard](screenshots/dashboard.png)\
*Note: Add screenshots to a* `screenshots/` *folder and update paths if available.*

## Future Improvements

- Implement password hashing (e.g., BCrypt) for secure storage
- Add fund transfer functionality between accounts
- Introduce unit tests with JUnit
- Enhance UI with themes or additional components
- Add connection pooling for database scalability
- Improve input validation (e.g., phone number format)

## Known Issues

- Hardcoded database credentials should be replaced with environment variables in production
- Hardcoded `branch_id` in `addAccount` method (future versions could add branch selection)
- Original `Customer` table lacked `username`; fixed with `UNIQUE NOT NULL` constraint

## License

MIT License

## Contact

For questions or contributions, open an issue or contact your-email@example.com.