import { Routes, RouterModule } from "@angular/router";

import { HomeComponent } from "./components/home/home.component";
import { BookListComponent } from "./components/books/book-list.component";
import { BookDetailComponent } from "./components/books/book-detail.component";
import { BookFormComponent } from "./components/books/book-form.component";
import { LoanListComponent } from "./components/loans/loan-list.component";
import { LoanFormComponent } from "./components/loans/loan-form.component";
import { LoginComponent } from "./components/login/login.component";
import { RegisterComponent } from "./components/register/register.component";
import { AdminUsersComponent } from "./components/admin/admin-users.component";
import { AdminDashboardComponent } from "./components/admin/admin-dashboard.component";
import { AdminBooksComponent } from "./components/admin/admin-books/admin-books.component";

const appRoutes: Routes = [
  // Public routes
  { path: "", component: HomeComponent },
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },

  // App routes (components handle their own auth checks)
  { path: "books", component: BookListComponent },
  { path: "books/new", component: BookFormComponent },
  { path: "books/:id", component: BookDetailComponent },
  { path: "books/edit/:id", component: BookFormComponent },

  // Loan routes
  { path: "loans", component: LoanListComponent },
  { path: "loans/create", component: LoanFormComponent },
  { path: "loans/edit/:id", component: LoanFormComponent },

  // Admin routes (no guard - backend will handle permissions)
  { path: "admin", component: AdminDashboardComponent },
  { path: "admin/users", component: AdminUsersComponent },
  { path: "admin/books", component: AdminBooksComponent },

  // Catch all route
  { path: "**", redirectTo: "" }
];

export const routing = RouterModule.forRoot(appRoutes);