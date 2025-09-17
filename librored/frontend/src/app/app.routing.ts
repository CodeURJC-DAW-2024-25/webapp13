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
import { AdminGuard } from "./guards/admin.guard";

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

  // Admin routes (protected by AdminGuard)
  { path: "admin/users", component: AdminUsersComponent, canActivate: [AdminGuard] },

  // Catch all route
  { path: "**", redirectTo: "" }
];

export const routing = RouterModule.forRoot(appRoutes);