package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.BookRepository;
import es.codeurjc13.librored.repository.LoanRepository;
import es.codeurjc13.librored.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;


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
    private PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() throws IOException, URISyntaxException {

        logger.info("⚡ Running DatabaseInitializer...");

        if (userRepository.count() == 0 && bookRepository.count() == 0 && loanRepository.count() == 0) {

            logger.info("Database is empty. Initializing sample data...");

            User admin = new User("Admin", "admin@example.com", passwordEncoder.encode("pass"), User.Role.ADMIN);
            userRepository.save(admin);

            // Sample users
            User alice = new User("Alice Green", "alice@example.com", passwordEncoder.encode("pass"), User.Role.USER);
            User bob = new User("Bob Staples", "bob@example.com", passwordEncoder.encode("pass"), User.Role.USER);
            User charlie = new User("Charlie Dock", "charlie@example.com", passwordEncoder.encode("pass"), User.Role.USER);
            User diana = new User("Diana Brown", "diana@example.com", passwordEncoder.encode("pass"), User.Role.USER);
            User ethan = new User("Ethan Hawk", "ethan@example.com", passwordEncoder.encode("pass"), User.Role.USER);
            User fiona = new User("Fiona Shrek", "fiona@example.com", passwordEncoder.encode("pass"), User.Role.USER);
            User george = new User("George Orwell", "george@example.com", passwordEncoder.encode("pass"), User.Role.USER);

            userRepository.saveAll(Arrays.asList(alice, bob, charlie, diana, ethan, fiona, george));


            // Sample books
            // Owner 1 (Mix of Fiction, SciFi & Fantasy, Mystery & Thriller)
            Book book1 = new Book("The Great Adventure", "John Doe", Book.Genre.Fiction, "An epic tale of discovery and courage.", alice);
            setBookImage(book1, "/static/images/covers/great_adventure.png");
            bookRepository.save(book1);
            /*Book book2 = new Book("Galactic Wars", "Emily Carter", Book.Genre.SciFi_Fantasy, "An interstellar battle for survival in a distant galaxy.", "images/covers/galactic_wars.png", alice);
            Book book3 = new Book("The Silent Killer", "Mark Johnson", Book.Genre.Mystery_Thriller, "A detective investigates a series of cryptic murders.", "images/covers/silent_killer.png", alice);
            Book book4 = new Book("The Last Pharaoh", "Wilbur Smith", Book.Genre.Historical_Fiction, "The final days of an Egyptian dynasty.", "images/covers/last_pharaoh.png", alice);

            // Owner 2 (Mix of Non-Fiction, Horror, Romance, SciFi & Fantasy)
            Book book5 = new Book("The Science of Everything", "Jane Smith", Book.Genre.Non_Fiction, "A deep dive into the wonders of modern science.", "images/covers/science_everything.png", bob);
            Book book6 = new Book("The Cursed Woods", "Stephen King", Book.Genre.Horror, "A mysterious forest where people disappear.", "images/covers/cursed_woods.png", bob);
            Book book7 = new Book("Unwritten Letters", "Nicholas Sparks", Book.Genre.Romance, "A series of letters change a woman’s fate.", "images/covers/unwritten_letters.png", bob);
            Book book8 = new Book("Chronicles of Eldoria", "Brandon Sanderson", Book.Genre.SciFi_Fantasy, "A young mage embarks on a heroic quest.", "images/covers/chronicles_eldoria.png", bob);

            // Owner 3 (Mix of Mystery & Thriller, Fiction, Historical Fiction, Horror)
            Book book9 = new Book("Whispers in the Dark", "Angela Carter", Book.Genre.Mystery_Thriller, "A journalist uncovers a hidden conspiracy.", "images/covers/whispers_dark.png", charlie);
            Book book10 = new Book("Echoes of Tomorrow", "Robert Martin", Book.Genre.Fiction, "A mysterious journey through time.", "images/covers/echoes_tomorrow.png", charlie);
            Book book11 = new Book("The Emperor’s Shadow", "Ken Follett", Book.Genre.Historical_Fiction, "A Roman general’s fight for justice.", "images/covers/emperors_shadow.png", charlie);
            Book book12 = new Book("The Night Visitor", "Dean Koontz", Book.Genre.Horror, "A chilling presence haunts a woman’s dreams.", "images/covers/night_visitor.png", charlie);

            // Owner 4 (Mix of SciFi & Fantasy, Non-Fiction, Horror, Romance)
            Book book13 = new Book("The Cyber Revolution", "Isaac Asimov", Book.Genre.SciFi_Fantasy, "The rise of AI and its impact on humanity.", "images/covers/cyber_revolution.png", diana);
            Book book14 = new Book("Understanding the Universe", "Neil Tyson", Book.Genre.Non_Fiction, "A simplified guide to the cosmos.", "images/covers/universe_guide.png", diana);
            Book book15 = new Book("Shadows Beneath", "Clive Barker", Book.Genre.Horror, "Something lurks in the depths of an abandoned hospital.", "images/covers/shadows_beneath.png", diana);
            Book book16 = new Book("A Parisian Affair", "Megan Hart", Book.Genre.Romance, "A love story set in the heart of Paris.", "images/covers/parisian_affair.png", diana);

            // Owner 5 (Mix of Romance, Mystery & Thriller, Historical Fiction, Fiction)
            Book book17 = new Book("Midnight Serenade", "Emily Bronte", Book.Genre.Romance, "A musician falls for a mysterious stranger.", "images/covers/midnight_serenade.png", ethan);
            Book book18 = new Book("Vanishing Shadows", "Lisa Scott", Book.Genre.Mystery_Thriller, "A small town hides dark secrets.", "images/covers/vanishing_shadows.png", ethan);
            Book book19 = new Book("Warrior’s Legacy", "Conn Iggulden", Book.Genre.Historical_Fiction, "A samurai’s tale of honor and revenge.", "images/covers/warriors_legacy.png", ethan);
            Book book20 = new Book("The Last Horizon", "Alice Walker", Book.Genre.Fiction, "A gripping story of survival in an uncharted land.", "images/covers/last_horizon.png", ethan);

            // Owner 6 (Mix of Historical Fiction, Non-Fiction, SciFi & Fantasy, Mystery & Thriller)
            Book book21 = new Book("The Forgotten Kingdom", "Henry Williams", Book.Genre.Historical_Fiction, "A lost kingdom and the quest to uncover its past.", "images/covers/forgotten_kingdom.png", fiona);
            Book book22 = new Book("History’s Greatest Inventions", "David Attenborough", Book.Genre.Non_Fiction, "The key inventions that shaped the modern world.", "images/covers/history_inventions.png", fiona);

            // Owner 7 (Mix of Horror, Fiction, Romance, Non-Fiction)
            Book book23 = new Book("The Haunted Manor", "Laura Brown", Book.Genre.Horror, "A family moves into a house with a terrifying past.", "images/covers/haunted_manor.png", george);
            Book book24 = new Book("The Great Adventure", "John Doe", Book.Genre.Fiction, "An epic tale of discovery and courage.", "images/covers/great_adventure.png", george);
            Book book25 = new Book("Love in the Rain", "Sophia Lee", Book.Genre.Romance, "Two souls find each other in the midst of a storm.", "images/covers/love_in_the_rain.png", george);

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
            loanRepository.saveAll(Arrays.asList(loan1, loan2, loan3, loan4, loan5, loan6, loan7, loan8, loan9, loan10, loan11, loan12));*/



        } // End of if

        // Log completion
        logger.info("DatabaseInitializer execution completed.");


    }

    public void setBookImage(Book book, String classpathResource) throws IOException {
        //book.setImage(true);
        Resource image = new ClassPathResource(classpathResource);
        book.setCoverPic(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
    }
}