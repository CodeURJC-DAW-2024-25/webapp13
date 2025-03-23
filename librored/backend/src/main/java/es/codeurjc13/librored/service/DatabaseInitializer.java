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

    @Autowired
    private ExternalAuthorService externalAuthorService;



    @PostConstruct
    public void init() throws IOException, URISyntaxException {

        logger.info("⚡ Running DatabaseInitializer...");

        if (userRepository.count() == 0 && bookRepository.count() == 0 && loanRepository.count() == 0) {

            logger.info("Database is empty. Initializing sample data...");

            User admin = new User("Admin", "admin@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_ADMIN);
            userRepository.save(admin);

            // Sample users
            User alice = new User("Alice Green", "alice@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_USER);
            User bob = new User("Bob Staples", "bob@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_USER);
            User charlie = new User("Charlie Dock", "charlie@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_USER);
            User diana = new User("Diana Brown", "diana@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_USER);
            User ethan = new User("Ethan Hawk", "ethan@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_USER);
            User fiona = new User("Fiona Shrek", "fiona@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_USER);
            User george = new User("George Orwell", "george@example.com", passwordEncoder.encode("pass"), User.Role.ROLE_USER);

            userRepository.saveAll(Arrays.asList(alice, bob, charlie, diana, ethan, fiona, george));


            // Sample books
            // Owner 1 (Mix of Fiction, SciFi & Fantasy, Mystery & Thriller)
            Book book1 = new Book("The Great Adventure", "John Doe", Book.Genre.Fiction, "An epic tale of discovery and courage.", alice);
            setBookImage(book1, "/static/images/covers/great_adventure.png");
            enrichAuthorInfo(book1);

            Book book2 = new Book("Galactic Wars", "Emily Carter", Book.Genre.SciFi_Fantasy, "An interstellar battle for survival in a distant galaxy.", alice);
            setBookImage(book2, "/static/images/covers/galactic_wars.png");
            enrichAuthorInfo(book2);

            Book book3 = new Book("The Silent Killer", "Mark Johnson", Book.Genre.Mystery_Thriller, "A detective investigates a series of cryptic murders.", alice);
            setBookImage(book3, "/static/images/covers/silent_killer.png");
            enrichAuthorInfo(book3);

            Book book4 = new Book("The Last Pharaoh", "Wilbur Smith", Book.Genre.Historical_Fiction, "The final days of an Egyptian dynasty.", alice);
            setBookImage(book4, "/static/images/covers/last_pharaoh.png");
            enrichAuthorInfo(book4);

            // Owner 2 (Mix of Non-Fiction, Horror, Romance, SciFi & Fantasy)
            Book book5 = new Book("The Science of Everything", "Jane Smith", Book.Genre.Non_Fiction, "A deep dive into the wonders of modern science.", bob);
            setBookImage(book5, "/static/images/covers/science_everything.png");
            enrichAuthorInfo(book5);

            Book book6 = new Book("The Cursed Woods", "Stephen King", Book.Genre.Horror, "A mysterious forest where people disappear.", bob);
            setBookImage(book6, "/static/images/covers/cursed_woods.png");
            enrichAuthorInfo(book6);

            Book book7 = new Book("Unwritten Letters", "Nicholas Sparks", Book.Genre.Romance, "A series of letters change a woman’s fate.", bob);
            setBookImage(book7, "/static/images/covers/unwritten_letters.png");
            enrichAuthorInfo(book7);

            Book book8 = new Book("Chronicles of Eldoria", "Brandon Sanderson", Book.Genre.SciFi_Fantasy, "A young mage embarks on a heroic quest.", bob);
            setBookImage(book8, "/static/images/covers/chronicles_eldoria.png");
            enrichAuthorInfo(book8);

            // Owner 3 (Mix of Mystery & Thriller, Fiction, Historical Fiction, Horror)
            Book book9 = new Book("Whispers in the Dark", "Angela Carter", Book.Genre.Mystery_Thriller, "A journalist uncovers a hidden conspiracy.", charlie);
            setBookImage(book9, "/static/images/covers/whispers_dark.png");
            enrichAuthorInfo(book9);

            Book book10 = new Book("Echoes of Tomorrow", "Robert Martin", Book.Genre.Fiction, "A mysterious journey through time.", charlie);
            setBookImage(book10, "/static/images/covers/echoes_tomorrow.png");
            enrichAuthorInfo(book10);

            Book book11 = new Book("The Emperor’s Shadow", "Ken Follett", Book.Genre.Historical_Fiction, "A Roman general’s fight for justice.", charlie);
            setBookImage(book11, "/static/images/covers/emperors_shadow.png");
            enrichAuthorInfo(book11);

            Book book12 = new Book("The Night Visitor", "Dean Koontz", Book.Genre.Horror, "A chilling presence haunts a woman’s dreams.", charlie);
            setBookImage(book12, "/static/images/covers/night_visitor.png");
            enrichAuthorInfo(book12);

            // Owner 4 (Mix of SciFi & Fantasy, Non-Fiction, Horror, Romance)
            Book book13 = new Book("The Cyber Revolution", "Isaac Asimov", Book.Genre.SciFi_Fantasy, "The rise of AI and its impact on humanity.", diana);
            setBookImage(book13, "/static/images/covers/cyber_revolution.png");
            enrichAuthorInfo(book13);

            Book book14 = new Book("Understanding the Universe", "Neil Tyson", Book.Genre.Non_Fiction, "A simplified guide to the cosmos.", diana);
            setBookImage(book14, "/static/images/covers/universe_guide.png");
            enrichAuthorInfo(book14);

            Book book15 = new Book("Shadows Beneath", "Clive Barker", Book.Genre.Horror, "Something lurks in the depths of an abandoned hospital.", diana);
            setBookImage(book15, "/static/images/covers/shadows_beneath.png");
            enrichAuthorInfo(book15);

            Book book16 = new Book("A Parisian Affair", "Megan Hart", Book.Genre.Romance, "A love story set in the heart of Paris.", diana);
            setBookImage(book16, "/static/images/covers/parisian_affair.png");
            enrichAuthorInfo(book16);

            // Owner 5 (Mix of Romance, Mystery & Thriller, Historical Fiction, Fiction)
            Book book17 = new Book("Midnight Serenade", "Emily Bronte", Book.Genre.Romance, "A musician falls for a mysterious stranger.", ethan);
            setBookImage(book17, "/static/images/covers/midnight_serenade.png");
            enrichAuthorInfo(book17);

            Book book18 = new Book("Vanishing Shadows", "Lisa Scott", Book.Genre.Mystery_Thriller, "A small town hides dark secrets.", ethan);
            setBookImage(book18, "/static/images/covers/vanishing_shadows.png");
            enrichAuthorInfo(book18);

            Book book19 = new Book("Warrior’s Legacy", "Conn Iggulden", Book.Genre.Historical_Fiction, "A samurai’s tale of honor and revenge.", ethan);
            setBookImage(book19, "/static/images/covers/warriors_legacy.png");
            enrichAuthorInfo(book19);

            Book book20 = new Book("The Last Horizon", "Alice Walker", Book.Genre.Fiction, "A gripping story of survival in an uncharted land.", ethan);
            setBookImage(book20, "/static/images/covers/last_horizon.png");
            enrichAuthorInfo(book20);

            // Owner 6 (Mix of Historical Fiction, Non-Fiction, SciFi & Fantasy, Mystery & Thriller)
            Book book21 = new Book("The Forgotten Kingdom", "Henry Williams", Book.Genre.Historical_Fiction, "A lost kingdom and the quest to uncover its past.", fiona);
            setBookImage(book21, "/static/images/covers/forgotten_kingdom.png");
            enrichAuthorInfo(book21);

            Book book22 = new Book("History’s Greatest Inventions", "David Attenborough", Book.Genre.Non_Fiction, "The key inventions that shaped the modern world.", fiona);
            setBookImage(book22, "/static/images/covers/history_inventions.png");
            enrichAuthorInfo(book22);

            // Owner 7 (Mix of Horror, Fiction, Romance, Non-Fiction)
            Book book23 = new Book("The Haunted Manor", "Laura Brown", Book.Genre.Horror, "A family moves into a house with a terrifying past.", george);
            setBookImage(book23, "/static/images/covers/haunted_manor.png");
            enrichAuthorInfo(book23);

            Book book24 = new Book("The Great Adventure", "John Doe", Book.Genre.Fiction, "An epic tale of discovery and courage.", george);
            setBookImage(book24, "/static/images/covers/great_adventure.png");
            enrichAuthorInfo(book24);

            Book book25 = new Book("Love in the Rain", "Sophia Lee", Book.Genre.Romance, "Two souls find each other in the midst of a storm.", george);
            setBookImage(book25, "/static/images/covers/love_in_the_rain.png");
            enrichAuthorInfo(book25);

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

        } // End of if

        // Log completion
        logger.info("DatabaseInitializer execution completed.");

    }

    public void setBookImage(Book book, String classpathResource) throws IOException {
        //book.setImage(true);
        Resource image = new ClassPathResource(classpathResource);
        book.setCoverPic(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
    }

    private void enrichAuthorInfo(Book book) {
        externalAuthorService.getAuthorInfo(book.getAuthor())
                // blockOption to fix bug enrichAuthorInfo(book) using Reactor's asynchronous .subscribe() inside a synchronous method
                .blockOptional()
                .ifPresentOrElse(author -> {
                    String bio = author.getName();
                    if (author.getBirth_date() != null) {
                        bio += " (born " + author.getBirth_date() + ")";
                    }
                    if (author.getTopWork() != null) {
                        bio += ", best known for *" + author.getTopWork() + "*";
                    }
                    bio += ", authored " + author.getWork_count() + " works.";

                    book.setAuthorBio(bio);
                    bookRepository.save(book);
                    System.out.println("Saved bio for: " + book.getAuthor());

                }, () -> {
                    book.setAuthorBio("No additional author info found.");
                    bookRepository.save(book);
                });
    }
}