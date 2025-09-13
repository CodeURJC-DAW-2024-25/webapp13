import { Routes, RouterModule } from "@angular/router";

import { BookListComponent } from "./components/books/book-list.component";
import { BookDetailComponent } from "./components/books/book-detail.component";
import { BookFormComponent } from "./components/books/book-form.component";
import { LoanListComponent } from "./components/loans/loan-list.component";
import { LoanFormComponent } from "./components/loans/loan-form.component";
import { LoginComponent } from "./components/login/login.component";

const appRoutes: Routes = [
  // Public routes
  { path: "login", component: LoginComponent },
  
  // App routes (components handle their own auth checks)
  { path: "books", component: BookListComponent },
  { path: "books/new", component: BookFormComponent },
  { path: "books/:id", component: BookDetailComponent },
  { path: "books/edit/:id", component: BookFormComponent },
  
  // Loan routes
  { path: "loans", component: LoanListComponent },
  { path: "loans/create", component: LoanFormComponent },
  { path: "loans/edit/:id", component: LoanFormComponent },
  
  // Default route
  { path: "", redirectTo: "books", pathMatch: "full" },
  
  // Catch all route
  { path: "**", redirectTo: "books" }
];

export const routing = RouterModule.forRoot(appRoutes);