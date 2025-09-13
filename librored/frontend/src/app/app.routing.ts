import { Routes, RouterModule } from "@angular/router";

import { BookListComponent } from "./components/books/book-list.component";
import { BookDetailComponent } from "./components/books/book-detail.component";
import { BookFormComponent } from "./components/books/book-form.component";
import { LoginComponent } from "./components/login/login.component";

const appRoutes: Routes = [
  // Public routes
  { path: "login", component: LoginComponent },
  
  // App routes (components handle their own auth checks)
  { path: "books", component: BookListComponent },
  { path: "books/new", component: BookFormComponent },
  { path: "books/:id", component: BookDetailComponent },
  { path: "books/edit/:id", component: BookFormComponent },
  
  // Default route
  { path: "", redirectTo: "books", pathMatch: "full" },
  
  // Catch all route
  { path: "**", redirectTo: "books" }
];

export const routing = RouterModule.forRoot(appRoutes);