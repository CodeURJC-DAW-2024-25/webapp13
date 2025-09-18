import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { BookDTO } from '../../dtos/book.dto';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  books: BookDTO[] = [];
  loading = false;
  errorMessage = '';

  constructor(private bookService: BookService) {
  }

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading = true;
    this.errorMessage = '';

    this.bookService.getBooks().subscribe({
      next: (books) => {
        this.books = books;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load books';
        this.loading = false;
      }
    });
  }

  getImageUrl(book: BookDTO): string {
    return book.hasCoverImage ? this.bookService.getImageUrl(book.id!) : '/images/no_image.png';
  }
}