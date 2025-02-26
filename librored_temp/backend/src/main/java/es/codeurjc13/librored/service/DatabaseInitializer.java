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
import java.time.LocalDate;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private static final LocalDate baseDate = LocalDate.of(2025, 1, 15); // Starting date matching the SQL example

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

        userRepository.saveAll(Arrays.asList(alice, bob, charlie, diana, ethan, fiona, george));

        // Sample books
        // Owner 1 (Mix of Fiction, SciFi & Fantasy, Mystery & Thriller)
        Book book1 = new Book("The Great Adventure", "John Doe", Book.Genre.Fiction, "An epic tale of discovery and courage.", "covers/great_adventure.png", alice);
        Book book2 = new Book("Galactic Wars", "Emily Carter", Book.Genre.SciFi_Fantasy, "An interstellar battle for survival in a distant galaxy.", "covers/galactic_wars.png", alice);
        Book book3 = new Book("The Silent Killer", "Mark Johnson", Book.Genre.Mystery_Thriller, "A detective investigates a series of cryptic murders.", "covers/silent_killer.png", alice);
        Book book4 = new Book("The Last Pharaoh", "Wilbur Smith", Book.Genre.Historical_Fiction, "The final days of an Egyptian dynasty.", "covers/last_pharaoh.png", alice);

        // Owner 2 (Mix of Non-Fiction, Horror, Romance, SciFi & Fantasy)
        Book book5 = new Book("The Science of Everything", "Jane Smith", Book.Genre.Non_Fiction, "A deep dive into the wonders of modern science.", "covers/science_everything.png", bob);
        Book book6 = new Book("The Cursed Woods", "Stephen King", Book.Genre.Horror, "A mysterious forest where people disappear.", "covers/cursed_woods.png", bob);
        Book book7 = new Book("Unwritten Letters", "Nicholas Sparks", Book.Genre.Romance, "A series of letters change a woman’s fate.", "covers/unwritten_letters.png", bob);
        Book book8 = new Book("Chronicles of Eldoria", "Brandon Sanderson", Book.Genre.SciFi_Fantasy, "A young mage embarks on a heroic quest.", "covers/chronicles_eldoria.png", bob);

        // Owner 3 (Mix of Mystery & Thriller, Fiction, Historical Fiction, Horror)
        Book book9 = new Book("Whispers in the Dark", "Angela Carter", Book.Genre.Mystery_Thriller, "A journalist uncovers a hidden conspiracy.", "covers/whispers_dark.png", charlie);
        Book book10 = new Book("Echoes of Tomorrow", "Robert Martin", Book.Genre.Fiction, "A mysterious journey through time.", "covers/echoes_tomorrow.png", charlie);
        Book book11 = new Book("The Emperor’s Shadow", "Ken Follett", Book.Genre.Historical_Fiction, "A Roman general’s fight for justice.", "covers/emperors_shadow.png", charlie);
        Book book12 = new Book("The Night Visitor", "Dean Koontz", Book.Genre.Horror, "A chilling presence haunts a woman’s dreams.", "covers/night_visitor.png", charlie);

        // Owner 4 (Mix of SciFi & Fantasy, Non-Fiction, Horror, Romance)
        Book book13 = new Book("The Cyber Revolution", "Isaac Asimov", Book.Genre.SciFi_Fantasy, "The rise of AI and its impact on humanity.", "covers/cyber_revolution.png", diana);
        Book book14 = new Book("Understanding the Universe", "Neil Tyson", Book.Genre.Non_Fiction, "A simplified guide to the cosmos.", "covers/universe_guide.png", diana);
        Book book15 = new Book("Shadows Beneath", "Clive Barker", Book.Genre.Horror, "Something lurks in the depths of an abandoned hospital.", "covers/shadows_beneath.png", diana);
        Book book16 = new Book("A Parisian Affair", "Megan Hart", Book.Genre.Romance, "A love story set in the heart of Paris.", "covers/parisian_affair.png", diana);

        // Owner 5 (Mix of Romance, Mystery & Thriller, Historical Fiction, Fiction)
        Book book17 = new Book("Midnight Serenade", "Emily Bronte", Book.Genre.Romance, "A musician falls for a mysterious stranger.", "covers/midnight_serenade.png", ethan);
        Book book18 = new Book("Vanishing Shadows", "Lisa Scott", Book.Genre.Mystery_Thriller, "A small town hides dark secrets.", "covers/vanishing_shadows.png", ethan);
        Book book19 = new Book("Warrior’s Legacy", "Conn Iggulden", Book.Genre.Historical_Fiction, "A samurai’s tale of honor and revenge.", "covers/warriors_legacy.png", ethan);
        Book book20 = new Book("The Last Horizon", "Alice Walker", Book.Genre.Fiction, "A gripping story of survival in an uncharted land.", "covers/last_horizon.png", ethan);

        // Owner 6 (Mix of Historical Fiction, Non-Fiction, SciFi & Fantasy, Mystery & Thriller)
        Book book21 = new Book("The Forgotten Kingdom", "Henry Williams", Book.Genre.Historical_Fiction, "A lost kingdom and the quest to uncover its past.", "covers/forgotten_kingdom.png", fiona);
        Book book22 = new Book("History’s Greatest Inventions", "David Attenborough", Book.Genre.Non_Fiction, "The key inventions that shaped the modern world.", "covers/history_inventions.png", fiona);

        // Owner 7 (Mix of Horror, Fiction, Romance, Non-Fiction)
        Book book23 = new Book("The Haunted Manor", "Laura Brown", Book.Genre.Horror, "A family moves into a house with a terrifying past.", "covers/haunted_manor.png", george);
        Book book24 = new Book("The Great Adventure", "John Doe", Book.Genre.Fiction, "An epic tale of discovery and courage.", "covers/great_adventure.png", george);
        Book book25 = new Book("Love in the Rain", "Sophia Lee", Book.Genre.Romance, "Two souls find each other in the midst of a storm.", "covers/love_in_the_rain.png", george);

        // Save all books
        bookRepository.saveAll(Arrays.asList(book1, book2, book3, book4, book5, book6, book7, book8, book9, book10, book11, book12, book13, book14, book15, book16, book17, book18, book19, book20, book21, book22, book23, book24, book25));


        // Sample loans
        Loan loan1 = new Loan(book1, alice, bob, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15), Loan.Status.Completed);
        Loan loan2 = new Loan(book5, bob, charlie, LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 20), Loan.Status.Completed);
        Loan loan3 = new Loan(book9, charlie, diana, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 25), Loan.Status.Completed);
        Loan loan4 = new Loan(book13, diana, ethan, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 30), Loan.Status.Completed);
        Loan loan5 = new Loan(book17, ethan, fiona, LocalDate.of(2025, 1, 20), LocalDate.of(2025, 2, 4), Loan.Status.Completed);
        Loan loan6 = new Loan(book21, fiona, george, LocalDate.of(2025, 1, 25), LocalDate.of(2025, 2, 9), Loan.Status.Completed);
        Loan loan7 = new Loan(book23, george, alice, LocalDate.of(2025, 1, 30), LocalDate.of(2025, 2, 14), Loan.Status.Completed);
        Loan loan8 = new Loan(book2, alice, charlie, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 16), Loan.Status.Completed);
        Loan loan9 = new Loan(book6, bob, diana, LocalDate.of(2025, 2, 5), LocalDate.of(2025, 2, 20), Loan.Status.Completed);
        Loan loan10 = new Loan(book3, alice, ethan, LocalDate.of(2025, 2, 10), null, Loan.Status.Active);
        Loan loan11 = new Loan(book10, charlie, fiona, LocalDate.of(2025, 2, 12), null, Loan.Status.Active);
        Loan loan12 = new Loan(book16, diana, george, LocalDate.of(2025, 2, 15), null, Loan.Status.Active);

        // Save all loans
        loanRepository.saveAll(Arrays.asList(loan1, loan2, loan3, loan4, loan5, loan6, loan7, loan8, loan9, loan10, loan11, loan12));


        // Sample reviews
        Review review1 = new Review(book1, bob, Review.Rating.FOUR, "Enjoyed the adventure.");
        review1.setReview_date(baseDate.plusDays(1)); // DATE_ADD('2025-01-15', INTERVAL 1 DAY)

        Review review2 = new Review(book5, charlie, Review.Rating.FIVE, "Very informative!");
        review2.setReview_date(baseDate.plusDays(6)); // DATE_ADD('2025-01-20', INTERVAL 6 DAY)

        Review review3 = new Review(book9, diana, Review.Rating.THREE, "Good read, but a bit slow.");
        review3.setReview_date(baseDate.plusDays(3)); // DATE_ADD('2025-01-25', INTERVAL 3 DAY)

        Review review4 = new Review(book13, ethan, Review.Rating.FOUR, "Exciting insights into technology.");
        review4.setReview_date(baseDate.plusDays(5)); // DATE_ADD('2025-01-30', INTERVAL 5 DAY)

        Review review5 = new Review(book17, fiona, Review.Rating.FIVE, "Captivating and emotional.");
        review5.setReview_date(baseDate.plusDays(10)); // DATE_ADD('2025-02-04', INTERVAL 10 DAY)

        Review review6 = new Review(book21, george, Review.Rating.FOUR, "A fascinating journey.");
        review6.setReview_date(baseDate.plusDays(6)); // DATE_ADD('2025-02-09', INTERVAL 6 DAY)

        Review review7 = new Review(book23, alice, Review.Rating.FIVE, "Thrilling and spooky.");
        review7.setReview_date(baseDate.plusDays(2)); // DATE_ADD('2025-02-14', INTERVAL 2 DAY)

        Review review8 = new Review(book2, charlie, Review.Rating.FOUR, "Great space opera.");
        review8.setReview_date(baseDate.plusDays(3)); // DATE_ADD('2025-02-16', INTERVAL 3 DAY)

        Review review9 = new Review(book6, diana, Review.Rating.THREE, "Not as scary as expected.");
        review9.setReview_date(baseDate.plusDays(1)); // DATE_ADD('2025-02-20', INTERVAL 1 DAY)

        // Save all reviews
        reviewRepository.saveAll(Arrays.asList(review1, review2, review3, review4, review5, review6, review7, review8, review9));

    }


}