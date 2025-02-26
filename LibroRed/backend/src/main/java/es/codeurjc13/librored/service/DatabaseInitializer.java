package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.Review;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import es.codeurjc13.librored.repository.LoanRepository;
import es.codeurjc13.librored.repository.ReviewRepository;
import es.codeurjc13.librored.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    @PostConstruct
    public void init() throws IOException, URISyntaxException {

        logger.info("⚡ Running DatabaseInitializer...");

        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User("Admin", "admin@example.com", "pass", User.Role.Admin);
            userRepository.save(admin);
        }

        logger.info("✅ DatabaseInitializer execution completed.");


        // Sample users
        User alice = new User("Alice", "alice@example.com", "password123", User.Role.Registered);
        User bob = new User("Bob", "bob@example.com", "password123", User.Role.Registered);
        User charlie = new User("Charlie", "charlie@example.com", "password123", User.Role.Registered);
        User diana = new User("Diana", "diana@example.com", "password123", User.Role.Registered);
        User ethan = new User("Ethan", "ethan@example.com", "password123", User.Role.Registered);
        User fiona = new User("Fiona", "fiona@example.com", "password123", User.Role.Registered);
        User george = new User("George", "george@example.com", "password123", User.Role.Registered);

        userRepository.save(alice);
        userRepository.save(bob);
        userRepository.save(charlie);
        userRepository.save(diana);
        userRepository.save(ethan);
        userRepository.save(fiona);
        userRepository.save(george);

        // Sample books
        Book book1 = new Book("The Great Adventure", "John Doe", Book.Genre.Fiction, "An epic tale of discovery and courage.", "covers/great_adventure.jpg", alice);
        Book book2 = new Book("Galactic Wars", "Emily Carter", Book.Genre.SciFi_Fantasy, "An interstellar battle for survival in a distant galaxy.", "covers/galactic_wars.jpg", bob);
        Book book3 = new Book("The Silent Killer", "Mark Johnson", Book.Genre.Mystery_Thriller, "A detective investigates a series of cryptic murders.", "covers/silent_killer.jpg", charlie);
        Book book4 = new Book("The Last Pharaoh", "Wilbur Smith", Book.Genre.Historical_Fiction, "The final days of an Egyptian dynasty.", "covers/last_pharaoh.jpg", diana);
        Book book5 = new Book("The Science of Everything", "Jane Smith", Book.Genre.Non_Fiction, "A deep dive into the wonders of modern science.", "covers/science_everything.jpg", ethan);
        Book book6 = new Book("The Cursed Woods", "Stephen King", Book.Genre.Horror, "A mysterious forest where people disappear.", "covers/cursed_woods.jpg", fiona);
        Book book7 = new Book("Unwritten Letters", "Nicholas Sparks", Book.Genre.Romance, "A series of letters change a woman’s fate.", "covers/unwritten_letters.jpg", george);
        Book book8 = new Book("Chronicles of Eldoria", "Brandon Sanderson", Book.Genre.SciFi_Fantasy, "A young mage embarks on a heroic quest.", "covers/chronicles_eldoria.jpg", alice);
        Book book9 = new Book("Whispers in the Dark", "Angela Carter", Book.Genre.Mystery_Thriller, "A journalist uncovers a hidden conspiracy.", "covers/whispers_dark.jpg", bob);
        Book book10 = new Book("Echoes of Tomorrow", "Robert Martin", Book.Genre.Fiction, "A mysterious journey through time.", "covers/echoes_tomorrow.jpg", charlie);
        Book book11 = new Book("The Emperor’s Shadow", "Ken Follett", Book.Genre.Historical_Fiction, "A Roman general’s fight for justice.", "covers/emperors_shadow.jpg", diana);
        Book book12 = new Book("The Night Visitor", "Dean Koontz", Book.Genre.Horror, "A chilling presence haunts a woman’s dreams.", "covers/night_visitor.jpg", ethan);

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
        bookRepository.save(book7);
        bookRepository.save(book8);
        bookRepository.save(book9);
        bookRepository.save(book10);
        bookRepository.save(book11);
        bookRepository.save(book12);

        // Sample loans
        Loan loan1 = new Loan(book1, alice, bob, new Date(2024-1900, 2, 15), new Date(2024-1900, 3, 10), Loan.Status.Completed);
        Loan loan2 = new Loan(book2, bob, charlie, new Date(2024-1900, 4, 10), new Date(2024-1900, 4, 20), Loan.Status.Completed);
        Loan loan3 = new Loan(book2, bob, charlie, new Date(2024-1900, 4, 10), new Date(2024-1900, 4, 20), Loan.Status.Completed);
        Loan loan4 = new Loan(book4, diana, ethan, new Date(2024-1900, 7, 20), new Date(2024-1900, 8, 15), Loan.Status.Completed);
        Loan loan5 = new Loan(book5, ethan, fiona, new Date(2024-1900, 9, 5), new Date(2024-1900, 9, 25), Loan.Status.Completed);
        Loan loan6 = new Loan(book6, fiona, george, new Date(2024-1900, 10, 10), new Date(2024-1900, 11, 5), Loan.Status.Completed);
        Loan loan7 = new Loan(book7, george, alice, new Date(2025-1900, 0, 1), new Date(2025-1900, 0, 20), Loan.Status.Completed);
        Loan loan8 = new Loan(book8, alice, charlie, new Date(2025-1900, 0, 10), new Date(2025-1900, 1, 5), Loan.Status.Completed);
        Loan loan9 = new Loan(book9, bob, diana, new Date(2025-1900, 1, 1), new Date(2025-1900, 1, 20), Loan.Status.Completed);
        Loan loan10 = new Loan(book10, charlie, ethan, new Date(2025-1900, 1, 10), null, Loan.Status.Active);
        Loan loan11 = new Loan(book11, diana, fiona, new Date(2025-1900, 1, 15), null, Loan.Status.Active);
        Loan loan12 = new Loan(book12, ethan, george, new Date(2025-1900, 1, 18), null, Loan.Status.Active);

        loanRepository.save(loan1);
        loanRepository.save(loan2);
        loanRepository.save(loan3);
        loanRepository.save(loan4);
        loanRepository.save(loan5);
        loanRepository.save(loan6);
        loanRepository.save(loan7);
        loanRepository.save(loan8);
        loanRepository.save(loan9);
        loanRepository.save(loan10);
        loanRepository.save(loan11);
        loanRepository.save(loan12);

        // Sample reviews
        Review review1 = new Review(book1, bob, Review.Rating.FIVE, "Amazing book!");
        Review review2 = new Review(book2, charlie, Review.Rating.FOUR, "Great read.");
        Review review3 = new Review(book3, diana, Review.Rating.THREE, "It was okay.");
        Review review4 = new Review(book4, ethan, Review.Rating.FIVE, "Loved it!");
        Review review5 = new Review(book5, fiona, Review.Rating.FOUR, "Very informative.");
        Review review6 = new Review(book6, george, Review.Rating.FIVE, "Scary but good.");
        Review review7 = new Review(book7, alice, Review.Rating.FOUR, "Heartwarming story.");
        Review review8 = new Review(book8, charlie, Review.Rating.FIVE, "Fantastic!");
        Review review9 = new Review(book9, diana, Review.Rating.THREE, "Not bad.");

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);
        reviewRepository.save(review4);
        reviewRepository.save(review5);
        reviewRepository.save(review6);
        reviewRepository.save(review7);
        reviewRepository.save(review8);
        reviewRepository.save(review9);
    }


}