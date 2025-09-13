import { Routes, RouterModule } from "@angular/router";

import { BookListComponent } from "./components/books/book-list.component";
import { BookDetailComponent } from "./components/books/book-detail.component";
import { BookFormComponent } from "./components/books/book-form.component";
import { LoginComponent } from "./components/login/login.component";
import { AuthGuard } from "./guards/auth.guard";

const appRoutes: Routes = [
  // Public routes (no authentication required)
  { path: "login", component: LoginComponent },
  
  // Protected routes (authentication required)
  { path: "books", component: BookListComponent, canActivate: [AuthGuard] },
  { path: "books/new", component: BookFormComponent, canActivate: [AuthGuard] },
  { path: "books/:id", component: BookDetailComponent, canActivate: [AuthGuard] },
  { path: "books/edit/:id", component: BookFormComponent, canActivate: [AuthGuard] },
  
  // Default route - redirect to books (will trigger auth check)
  { path: "", redirectTo: "books", pathMatch: "full" },
  
  // Catch all route - redirect to books
  { path: "**", redirectTo: "books" }
];

export const routing = RouterModule.forRoot(appRoutes);