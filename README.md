System for Managing Book Inventories, UserControl Accounts, and Borrowing/Returning Processes

System Overview
The system provides two roles: Admin and UserControl, each with specific functionalities for managing book inventories, user accounts, and the borrowing/returning process. It utilizes various data structures for efficient operations.

Functionalities
1. Admin Functionalities
Admins oversee the system’s operations, including book inventories and user accounts.
Book Inventory Management


Add a New Book
Data Structure: Linked List. 
Add books with attributes: Title, Author, Genre, ISBN, local Date Time and Copies.
Update Book Information
Modify details likeTitle, Author, Genre, ISBN, local Date Time and Copies.
Undo any deleted  (optional feature)
Remove a Book
Delete a book entry or reduce its copies.
Search Books
Use filters like Title, Author, and Genre (Book ID (for admin))
View All Books
Display all books with details, including available and borrowed quantities.
Display all borrowed and returned books.

UserControl Account Management


Create UserControl Accounts
Data Structure: Hash Table.
Add new users with details: UserControl ID (attribute), Name, Email, Password, and Borrowing Limit (3).
Update UserControl Information 
Modify user details such as name and email. 
Remove UserControl Accounts
Delete a user account (if no books are outstanding).
View UserControl Information
Display user data and borrowing history.
Reports and Logs (Cabilan)


Generate Reports 
Summarize inventory, overdue books, and user activity.
Activity Logs
Record borrowing, returning, and inventory changes for auditing.
Overdue Book Management (Cabilan)


Notify users about overdue books..

2. UserControl Functionalities 
Users interact with the system to borrow or return books and manage their accounts.
Account Management (Tio & Pilar) 


View Account Details 
Access personal information and borrowing history.
Update Account Information
Modify email or password.
View All Borrowed Books
Display all borrowed and returned books.


Book Borrowing (Baroro)


Search for Books
Use filters like title, author, or genre to find desired books.
Borrow a Book
Data Structure: Queue for managing borrowing requests.
Validate against borrowing limits and book availability.
Update borrowing history and inventory.
Book Returning (Maranga)


Return a Book
Mark a book as returned, update inventory, and change status to returned  
Track overdue returns.
Notify waitlisted users of book availability.
Notifications (Cabilan)


Receive reminders about due dates, overdue returns, or availability of waitlisted books.



Data Structures Used
Books (Inventory Management):


Linked List: For storing book details with attributes such as Title, Author, Genre, Quantity, etc.
Users (Account Management):


Hash Table: {UserID: {Name, Email, BorrowingHistory: [BookIDs], BorrowLimit}}
Borrowing Queue (Waitlisted Books):


Queue: Maintain a list of users waiting for specific books.
Borrowing History:


Linked List: {UserID: [BorrowingRecords]}, where each record includes BookID, BorrowDate, and DueDate.
Activity Logs (optional doh):


Array or Linked List: Store activity logs in chronological order for auditing.

Admin and UserControl Workflows
Admin Workflow
Login as Admin.
Add, update, or remove books in the inventory.
Manage user accounts: create, update, or delete accounts.
View inventory reports and logs.
Notify users about overdue books.
UserControl Workflow
Login as UserControl.
Search for available books.
Borrow desired books if within the borrowing limit.
Return books on or before the due date to avoid penalties.
Receive notifications about overdue books or availability of waitlisted items.

System Extensions (mga options rani doh)
Fine Management: Introduce a system to calculate and track overdue fines.
Reservation System: Allow users to reserve books in advance.
Online Portal: Develop a web or mobile interface for remote access.
Advanced Search: Implement fuzzy search to improve book discovery.


