-- Fill in the database with sample data for testing purposes.

-- This data is used to populate the User table.
-- The User table contains sample users with different roles.
INSERT INTO User (name, email, password, profile_pic, role)
VALUES
    ('Alice Johnson', 'alice@example.com', 'password123', 'alice.png', 'Registered'),
    ('Bob Smith', 'bob@example.com', 'password123', 'bob.png', 'Registered'),
    ('Charlie Brown', 'charlie@example.com', 'password123', 'charlie.png', 'Registered'),
    ('Diana Prince', 'diana@example.com', 'password123', 'diana.png', 'Registered'),
    ('Ethan Hunt', 'ethan@example.com', 'password123', 'ethan.png', 'Registered'),
    ('Fiona Gallagher', 'fiona@example.com', 'password123', 'fiona.png', 'Registered'),
    ('George Miller', 'george@example.com', 'password123', 'george.png', 'Registered'),
    ('Admin User', 'admin@example.com', 'adminpassword', 'admin.png', 'Admin');



-- This data is used to populate the Book table.
-- The Book table contains sample books with different genres.
INSERT INTO Book (owner_id, title, author, genre, description, cover_pic)
VALUES
    -- Owner 1 (Mix of Fiction, SciFi & Fantasy, Mystery & Thriller)
    (1, 'The Great Adventure', 'John Doe', 'Fiction', 'An epic tale of discovery and courage.', 'covers/great_adventure.png'),
    (1, 'Galactic Wars', 'Emily Carter', 'SciFi & Fantasy', 'An interstellar battle for survival in a distant galaxy.', 'covers/galactic_wars.png'),
    (1, 'The Silent Killer', 'Mark Johnson', 'Mystery & Thriller', 'A detective investigates a series of cryptic murders.', 'covers/silent_killer.png'),
    (1, 'The Last Pharaoh', 'Wilbur Smith', 'Historical Fiction', 'The final days of an Egyptian dynasty.', 'covers/last_pharaoh.png'),

    -- Owner 2 (Mix of Non-Fiction, Horror, Romance, SciFi & Fantasy)
    (2, 'The Science of Everything', 'Jane Smith', 'Non-Fiction', 'A deep dive into the wonders of modern science.', 'covers/science_everything.png'),
    (2, 'The Cursed Woods', 'Stephen King', 'Horror', 'A mysterious forest where people disappear.', 'covers/cursed_woods.png'),
    (2, 'Unwritten Letters', 'Nicholas Sparks', 'Romance', 'A series of letters change a woman’s fate.', 'covers/unwritten_letters.png'),
    (2, 'Chronicles of Eldoria', 'Brandon Sanderson', 'SciFi & Fantasy', 'A young mage embarks on a heroic quest.', 'covers/chronicles_eldoria.png'),

    -- Owner 3 (Mix of Mystery & Thriller, Fiction, Historical Fiction, Horror)
    (3, 'Whispers in the Dark', 'Angela Carter', 'Mystery & Thriller', 'A journalist uncovers a hidden conspiracy.', 'covers/whispers_dark.png'),
    (3, 'Echoes of Tomorrow', 'Robert Martin', 'Fiction', 'A mysterious journey through time.', 'covers/echoes_tomorrow.png'),
    (3, 'The Emperor’s Shadow', 'Ken Follett', 'Historical Fiction', 'A Roman general’s fight for justice.', 'covers/emperors_shadow.png'),
    (3, 'The Night Visitor', 'Dean Koontz', 'Horror', 'A chilling presence haunts a woman’s dreams.', 'covers/night_visitor.png'),

    -- Owner 4 (Mix of SciFi & Fantasy, Non-Fiction, Horror, Romance)
    (4, 'The Cyber Revolution', 'Isaac Asimov', 'SciFi & Fantasy', 'The rise of AI and its impact on humanity.', 'covers/cyber_revolution.png'),
    (4, 'Understanding the Universe', 'Neil Tyson', 'Non-Fiction', 'A simplified guide to the cosmos.', 'covers/universe_guide.png'),
    (4, 'Shadows Beneath', 'Clive Barker', 'Horror', 'Something lurks in the depths of an abandoned hospital.', 'covers/shadows_beneath.png'),
    (4, 'A Parisian Affair', 'Megan Hart', 'Romance', 'A love story set in the heart of Paris.', 'covers/parisian_affair.png'),

    -- Owner 5 (Mix of Romance, Mystery & Thriller, Historical Fiction, Fiction)
    (5, 'Midnight Serenade', 'Emily Bronte', 'Romance', 'A musician falls for a mysterious stranger.', 'covers/midnight_serenade.png'),
    (5, 'Vanishing Shadows', 'Lisa Scott', 'Mystery & Thriller', 'A small town hides dark secrets.', 'covers/vanishing_shadows.png'),
    (5, 'Warrior’s Legacy', 'Conn Iggulden', 'Historical Fiction', 'A samurai’s tale of honor and revenge.', 'covers/warriors_legacy.png'),
    (5, 'The Last Horizon', 'Alice Walker', 'Fiction', 'A gripping story of survival in an uncharted land.', 'covers/last_horizon.png'),

    -- Owner 6 (Mix of Historical Fiction, Non-Fiction, SciFi & Fantasy, Mystery & Thriller)
    (6, 'The Forgotten Kingdom', 'Henry Williams', 'Historical Fiction', 'A lost kingdom and the quest to uncover its past.', 'covers/forgotten_kingdom.png'),
    (6, 'History’s Greatest Inventions', 'David Attenborough', 'Non-Fiction', 'The key inventions that shaped the modern world.', 'covers/history_inventions.png'),

    -- Owner 7 (Mix of Horror, Fiction, Romance, Non-Fiction)
    (7, 'The Haunted Manor', 'Laura Brown', 'Horror', 'A family moves into a house with a terrifying past.', 'covers/haunted_manor.png'),
    (7, 'The Great Adventure', 'John Doe', 'Fiction', 'An epic tale of discovery and courage.', 'covers/great_adventure.png'),
    (7, 'Love in the Rain', 'Sophia Lee', 'Romance', 'Two souls find each other in the midst of a storm.', 'covers/love_in_the_rain.png')
;

-- This data is used to populate the Loan table.
-- Insert data into the Loan table:
INSERT INTO Loan (book_id, lender_id, borrower_id, start_date, end_date, status) VALUES
                                                                                     (1, 1, 2, '2025-01-01', '2025-01-15', 'Completed'),   -- Alice lends her book ("The Great Adventure") to Bob
                                                                                     (5, 2, 3, '2025-01-05', '2025-01-20', 'Completed'),   -- Bob lends his book ("The Science of Everything") to Charlie
                                                                                     (9, 3, 4, '2025-01-10', '2025-01-25', 'Completed'),   -- Charlie lends his book ("Whispers in the Dark") to Diana
                                                                                     (13, 4, 5, '2025-01-15', '2025-01-30', 'Completed'),  -- Diana lends her book ("The Cyber Revolution") to Ethan
                                                                                     (17, 5, 6, '2025-01-20', '2025-02-04', 'Completed'),  -- Ethan lends his book ("Midnight Serenade") to Fiona
                                                                                     (21, 6, 7, '2025-01-25', '2025-02-09', 'Completed'),  -- Fiona lends her book ("The Forgotten Kingdom") to George
                                                                                     (23, 7, 1, '2025-01-30', '2025-02-14', 'Completed'),  -- George lends his book ("The Haunted Manor") to Alice
                                                                                     (2, 1, 3, '2025-02-01', '2025-02-16', 'Completed'),   -- Alice (again) lends another book ("Galactic Wars") to Charlie
                                                                                     (6, 2, 4, '2025-02-05', '2025-02-20', 'Completed'),   -- Bob lends his book ("The Cursed Woods") to Diana
                                                                                     (3, 1, 5, '2025-02-10', NULL, 'Active'),              -- Active loan: Alice lends ("The Silent Killer") to Ethan
                                                                                     (10, 3, 6, '2025-02-12', NULL, 'Active'),             -- Active loan: Charlie lends ("Echoes of Tomorrow") to Fiona
                                                                                     (16, 4, 7, '2025-02-15', NULL, 'Active');             -- Active loan: Diana lends ("A Parisian Affair") to George

-- This data is used to populate the Review table.
-- Insert data into the Review table (for the 9 completed loans):
-- Here, the review is done by the borrower of each completed loan.
INSERT INTO Review (book_id, user_id, rating, comment) VALUES
                                                           (1, 2, '4', 'Enjoyed the adventure.'),
                                                           (5, 3, '5', 'Very informative!'),
                                                           (9, 4, '3', 'Good read, but a bit slow.'),
                                                           (13, 5, '4', 'Exciting insights into technology.'),
                                                           (17, 6, '5', 'Captivating and emotional.'),
                                                           (21, 7, '4', 'A fascinating journey.'),
                                                           (23, 1, '5', 'Thrilling and spooky.'),
                                                           (2, 3, '4', 'Great space opera.'),
                                                           (6, 4, '3', 'Not as scary as expected.');
