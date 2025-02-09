Budget Manager App - Personal Finance Assistant

Project Overview Personal Finance Assistant is a Java desktop application that helps users manage their personal finances. The application demonstrates the implementation of object-oriented design principles, GUI development using Swing, and secure user data management.

Key Classes and Functionality

Main Functionality Classes • FinanceApp: Main application window implementing tabbed interface for expenses, budgets, and reports. • ExpenseManager: Manages expense data and operations, including adding, editing, and filtering expenses. • BudgetManager: Handles budget allocations and tracks spending against budgets. • ReportManager: Generates financial reports and provides data visualization.

User Interface Classes • DashboardPanel: Shows financial overview and recent transactions. • ExpenseTrackerPanel: Provides interface for managing expenses with filtering capabilities. • BudgetManagerPanel: Displays budget progress with visual indicators for spending thresholds • ReportManagerPanel: Shows financial reports with pie charts and spending analysis

Data & Security Classes • User: Handles user authentication with secure password hashing • DataPersistenceManager: Manages file-based storage of user data and transactions • TransactionFileHandler: Handles CSV import/export functionality

Features • User authentication and account management • Expense tracking and categorization • Monthly budget management with visual progress tracking • Financial reporting with charts • Data persistence and file handling • Transaction import/export

How to Run

Place the Project in you preferred IDE and run Main.java
The application will automatically create necessary data directories
Use "Create Account" to register a new user
Login with your credentials
Add expenses through the Expense tab
Set budgets in the Budget tab
View reports in the Reports tab
Can switch between different months of data in Budget and Report Panels.
Try importing/exporting transactions using CSV files
Testing JUnit tests are provided for core functionality including:

User authentication
Expense operations
Budget calculations
Data persistence
