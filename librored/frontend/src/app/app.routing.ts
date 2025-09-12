import { Routes, RouterModule } from "@angular/router";

import { BookListComponent } from "./components/books/book-list.component";
import { BookDetailComponent } from "./components/books/book-detail.component";
import { BookFormComponent } from "./components/books/book-form.component";
import { LoginComponent } from "./components/login/login.component";

const appRoutes: Routes = [
  { path: "books", component: BookListComponent },
  { path: "books/new", component: BookFormComponent },
  { path: "books/:id", component: BookDetailComponent },
  { path: "books/edit/:id", component: BookFormComponent },
  { path: "login", component: LoginComponent },
  { path: "", redirectTo: "books", pathMatch: "full" },
];

export const routing = RouterModule.forRoot(appRoutes);