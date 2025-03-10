# 📚 LibroRed - Web Application for Book Lending Between Individuals

---

## Development Team Members (Team 13)

- **First Name**: Ana María
- **Last Name**: Jurado Crespo
- **Official University Email**: [am.juradoc@alumnos.urjc.es](mailto:am.juradoc@alumnos.urjc.es)
- **GitHub Account**: [medinaymedia](https://github.com/medinaymedia)

---

## Application Description

### 1️⃣ Application Entities

#### User
Represents the platform's users, who can be **lenders** and/or **borrowers**.
- A **lender** is a user who has books available for lending.
- A **borrower** is the person who receives a book on loan.  
  There are three types of users: **Anonymous, Registered, and Admin**.

#### Book
Represents the books available for lending.

#### Loan
Represents the process of borrowing a book between two users.

#### 🔗 Relationships Between Entities

- **User ↔ Book**: A user can have multiple books available for lending.
- **User ↔ Loan**: A loan connects two users: the **lender** (owner of the book) and the **borrower** (who receives the book).
- **Loan ↔ Book**: Each loan is associated with a specific book.

---

### 2️⃣ User Permissions

- **Anonymous User**: Can browse basic information about available books but cannot request loans or create books.
- **Registered User**: Can offer books for lending, request loans. They manage their own books and loans.
- **Administrator User**: Has full control over the platform, including user, loan and book management.

---

### 3️⃣ Images

- **Book**: Each book will have a single associated cover image.

---

### 4️⃣ Graphs 

Each user will be able to view:

** Books by Genre Graph**: A bar chart representing the number of books available in each genre.

---

### 5️⃣ Additional Features

** PDF Export of Database Information for Admins**  
Generates a PDF containing details of **Users, Books, and Loans**.

---

### 6️⃣ Advanced Algorithm

** Book Recommendation Algorithm**

#### How It Works
1. **User’s Book Preferences Analysis**: Identifies the genres of books registered by the user.
2. **User’s Loan History Analysis**: Identifies the genres of books the user has borrowed.
3. **Recommendation Generation**: The system suggests books aligned with the user's interests.

---

# Application - PART 1

## Navigation Diagram



## DB Entity Diagram



## Structured Class Diagram


---
## How to Configure your Environment (MacOs)
To configure and run the Spring Boot application in a completely new environment on macOS, follow these detailed instructions:

### 1. Install Homebrew
Homebrew is a package manager for macOS that simplifies the installation of software.

```sh
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### 2. Install Java Development Kit (JDK)
Install the latest version of OpenJDK.

```sh
brew install openjdk
```

Add the JDK to your PATH:

```sh
echo 'export PATH="/usr/local/opt/openjdk/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### 3. Install Maven
Maven is a build automation tool used for Java projects.

```sh
brew install maven
```

### 4. Install MySQL
Install MySQL server.

```sh
brew install mysql
```

Start MySQL server:

```sh
brew services start mysql
```

Secure MySQL installation (optional but recommended):

```sh
mysql_secure_installation
```

### 5. Clone the Repository
Clone the project repository from GitHub.

```sh
git clone https://github.com/medinaymedia/your-repo-name.git
cd your-repo-name
```

### 6. Configure the Database
Create a new MySQL database and user for the application.

```sh
mysql -u root -p
```

Inside the MySQL shell, run:

```sql
CREATE DATABASE librored;
CREATE USER 'librored_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON librored.* TO 'librored_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 7. Update Application Properties
Update the `application.properties` file with your database configuration. This file is typically located in `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/librored
spring.datasource.username=librored_user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

### 8. Build and Run the Application
Use Maven to build and run the Spring Boot application.

```sh
mvn clean install
mvn spring-boot:run
```

### 9. Access the Application
Once the application is running, you can access it in your web browser at:

```
https://localhost:8443
```

### 10. Additional Configuration
If your application uses HTTPS, ensure you have the keystore file (`keystore.jks`) in the appropriate location and update the `application.properties` file accordingly.

```properties
server.port=8443
server.ssl.key-store=classpath:keystore.jks
server.ssl.key-store-password=your_keystore_password
server.ssl.key-password=your_key_password
```

By following these steps, you should be able to configure and run the Spring Boot application in a new macOS environment.


# Members participation

### Top 5 Commits

**Ana María Jurado Crespo** [medinaymedia](https://github.com/medinaymedia)

Commit Hash: f84abe5
Message: Added book covers to books page
Description: Show Book CoverPic on app for USER and ADMIN. Fixed LazyInitializationException by eagerly fetching loans.Also added loan status display to books page.
---
Commit Hash: def5678  
Message: Fixed Admin Edit User Role Check
Description: Corrected the way admin roles are checked to ensure proper access control.
---
Commit Hash: 43ea2c9  
Message: User panel
Description: Create the whole user panel to give USER the ability to change password and username
---
Commit Hash: f0fdfca 
Message: Queries to fill in tables with data generated by AI
Description: Implement DatabaseInitializer.java with all the info generated to populate DB
---
Commit Hash: 85e3de3 
Message: Add self generated keystore certificate to implement HTTPS
Description: Added the keystore.jks file that have been previously generated

### Top 5 Files

Ana María Jurado Crespo  [medinaymedia](https://github.com/medinaymedia) has developed the entire code the app is based on except for Bootsrap elements.
The Bootstrap template [ToHoney](https://elements.envato.com/es/tohoney-ecommerce-bootstrap-template-9PJXT9U) has been used in frontend side. 
JS scripts develop by Ana are implemented on templates files.
The preloaded data has been generated with AI help. 


### Task Description
I have developed a fully functional **Spring Boot** web application with a **MySQL database**, ensuring all features are complete and properly integrated. **Sample data**, including users, books, and images, is preloaded into the database at startup.

The application includes **custom error pages** matching the overall design and **pagination** for Book dataset, AJAX-powered “More Results” button and a **loading spinner**.

User **authentication and authorization** are managed with **Spring Security**, restricting access based on roles. Credentials are **encrypted with BCrypt**, and registered users are preloaded. A **registration form** allows new user sign-ups.

All **backend code** is stored in a dedicated folder for a structured repository. The application runs securely over **HTTPS on port 8443**. To simplify deployment, **images are stored in the database** instead of the file system.




**Developed with passion by Team 13**  
