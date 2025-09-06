package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.BookService;
import es.codeurjc13.librored.service.LoanService;
import es.codeurjc13.librored.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final LoanService loanService;

    public AdminController(UserService userService, BookService bookService, LoanService loanService) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @GetMapping
    public String adminDashboard() {
        return "admin";
    }


    @GetMapping("/download-report")
    public void downloadReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Admin_Report.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPageContentStream contentStream = null;
            PDPage currentPage = null;
            int y = 0;

            // Create first page and initialize
            PDFPageHelper pageHelper = new PDFPageHelper(document);
            currentPage = pageHelper.createNewPage();
            contentStream = pageHelper.createContentStream(currentPage);
            y = pageHelper.addTitle(contentStream, "ADMIN REPORT");

            // Add Users Table
            PDFResult result = addSectionAndTable(document, contentStream, currentPage, y, pageHelper, "Users List", "users");
            contentStream = result.contentStream;
            currentPage = result.currentPage;
            y = result.y;

            // Add Books Table
            result = addSectionAndTable(document, contentStream, currentPage, y, pageHelper, "Books List", "books");
            contentStream = result.contentStream;
            currentPage = result.currentPage;
            y = result.y;

            // Add Loans Table
            result = addSectionAndTable(document, contentStream, currentPage, y, pageHelper, "Loans List", "loans");

            result.contentStream.close();
            document.save(response.getOutputStream());
        }
    }

    // Helper class for managing PDF page creation and layout
    private static class PDFPageHelper {
        private final PDDocument document;
        private static final int TOP_MARGIN = 750;
        private static final int BOTTOM_MARGIN = 50;
        private static final int LEFT_MARGIN = 50;
        private static final int RIGHT_MARGIN = 50;
        private static final int LINE_HEIGHT = 15;
        private static final int PAGE_WIDTH = 595; // A4 width in points
        private static final int MAX_LINE_WIDTH = PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN;

        public PDFPageHelper(PDDocument document) {
            this.document = document;
        }
        
        public String truncateText(String text, PDType1Font font, float fontSize) {
            try {
                float textWidth = font.getStringWidth(text) / 1000 * fontSize;
                if (textWidth <= MAX_LINE_WIDTH) {
                    return text;
                }
                
                // Binary search to find maximum characters that fit
                int left = 0;
                int right = text.length();
                String result = text;
                
                while (left < right) {
                    int mid = (left + right + 1) / 2;
                    String candidate = text.substring(0, mid) + "...";
                    float candidateWidth = font.getStringWidth(candidate) / 1000 * fontSize;
                    
                    if (candidateWidth <= MAX_LINE_WIDTH) {
                        left = mid;
                        result = candidate;
                    } else {
                        right = mid - 1;
                    }
                }
                return result;
            } catch (IOException e) {
                // Fallback to simple truncation
                return text.length() > 80 ? text.substring(0, 77) + "..." : text;
            }
        }

        public PDPage createNewPage() {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            return page;
        }

        public PDPageContentStream createContentStream(PDPage page) throws IOException {
            return new PDPageContentStream(document, page);
        }

        public int addTitle(PDPageContentStream contentStream, String title) throws IOException {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(200, TOP_MARGIN);
            contentStream.showText(title);
            contentStream.endText();
            return TOP_MARGIN - 50;
        }

        public int addSectionHeader(PDPageContentStream contentStream, String title, int y) throws IOException {
            if (y < BOTTOM_MARGIN + 30) {
                return -1; // Signal that new page is needed
            }
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(LEFT_MARGIN, y);
            contentStream.showText(title);
            contentStream.endText();
            return y - 25;
        }

        public boolean needsNewPage(int y) {
            return y < BOTTOM_MARGIN + LINE_HEIGHT;
        }
    }

    // Helper class for returning pagination results
    private static class PDFResult {
        public PDPageContentStream contentStream;
        public PDPage currentPage;
        public int y;

        public PDFResult(PDPageContentStream contentStream, PDPage currentPage, int y) {
            this.contentStream = contentStream;
            this.currentPage = currentPage;
            this.y = y;
        }
    }

    private PDFResult addUserTableWithPagination(PDDocument document, PDPageContentStream contentStream, 
                                                 PDPage currentPage, int y, PDFPageHelper pageHelper) throws IOException {
        List<User> users = userService.getAllUsers();
        
        // Add column headers
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN, y);
        contentStream.showText("ID | Username | Email | Role");
        contentStream.endText();
        y -= PDFPageHelper.LINE_HEIGHT + 5;
        
        for (User user : users) {
            if (pageHelper.needsNewPage(y)) {
                contentStream.close();
                currentPage = pageHelper.createNewPage();
                contentStream = pageHelper.createContentStream(currentPage);
                y = PDFPageHelper.TOP_MARGIN;
                
                // Re-add headers on new page
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN, y);
                contentStream.showText("ID | Username | Email | Role");
                contentStream.endText();
                y -= PDFPageHelper.LINE_HEIGHT + 5;
            }

            String userText = user.getId() + " | " + user.getUsername() + " | " + user.getEmail() + " | " + user.getRole().name().replace("ROLE_", "");
            String truncatedText = pageHelper.truncateText(userText, PDType1Font.HELVETICA, 12);
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN, y);
            contentStream.showText(truncatedText);
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;
        }
        
        return new PDFResult(contentStream, currentPage, y - 10);
    }

    private PDFResult addBookTableWithPagination(PDDocument document, PDPageContentStream contentStream, 
                                                 PDPage currentPage, int y, PDFPageHelper pageHelper) throws IOException {
        List<Book> books = bookService.getAllBooks();
        
        // Add column headers
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN, y);
        contentStream.showText("ID | Title | Author | Genre");
        contentStream.endText();
        y -= PDFPageHelper.LINE_HEIGHT + 5;
        
        for (Book book : books) {
            if (pageHelper.needsNewPage(y)) {
                contentStream.close();
                currentPage = pageHelper.createNewPage();
                contentStream = pageHelper.createContentStream(currentPage);
                y = PDFPageHelper.TOP_MARGIN;
                
                // Re-add headers on new page
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN, y);
                contentStream.showText("ID | Title | Author | Genre");
                contentStream.endText();
                y -= PDFPageHelper.LINE_HEIGHT + 5;
            }

            String bookText = book.getId() + " | " + book.getTitle() + " | " + book.getAuthor() + " | " + book.getGenre();
            String truncatedText = pageHelper.truncateText(bookText, PDType1Font.HELVETICA, 12);
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN, y);
            contentStream.showText(truncatedText);
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;
        }
        
        return new PDFResult(contentStream, currentPage, y - 10);
    }

    private PDFResult addLoanTableWithPagination(PDDocument document, PDPageContentStream contentStream, 
                                                 PDPage currentPage, int y, PDFPageHelper pageHelper) throws IOException {
        List<Loan> loans = loanService.getAllLoans();
        
        for (Loan loan : loans) {
            // Check if we need space for a complete loan entry (6 lines)
            if (pageHelper.needsNewPage(y - (6 * PDFPageHelper.LINE_HEIGHT))) {
                contentStream.close();
                currentPage = pageHelper.createNewPage();
                contentStream = pageHelper.createContentStream(currentPage);
                y = PDFPageHelper.TOP_MARGIN;
            }

            // Add loan header with ID
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN, y);
            contentStream.showText("Loan ID: " + loan.getId());
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;

            // Add book information
            String bookText = "Book: " + loan.getBook().getTitle();
            String truncatedBookText = pageHelper.truncateText(bookText, PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN + 15, y);
            contentStream.showText(truncatedBookText);
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;

            // Add lender information
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN + 15, y);
            contentStream.showText("Lender: " + loan.getLender().getUsername());
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;

            // Add borrower information
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN + 15, y);
            contentStream.showText("Borrower: " + loan.getBorrower().getUsername());
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;

            // Add status information
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN + 15, y);
            contentStream.showText("Status: " + loan.getStatus());
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;

            // Add dates if available
            String dateInfo = "Start: " + loan.getStartDate();
            if (loan.getEndDate() != null) {
                dateInfo += " | End: " + loan.getEndDate();
            }
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.LEFT_MARGIN + 15, y);
            contentStream.showText(dateInfo);
            contentStream.endText();
            y -= PDFPageHelper.LINE_HEIGHT;

            // Add spacing between loans
            y -= 5;
        }
        
        return new PDFResult(contentStream, currentPage, y);
    }

    private PDFResult addSectionAndTable(PDDocument document, PDPageContentStream contentStream, 
                                        PDPage currentPage, int y, PDFPageHelper pageHelper, 
                                        String sectionTitle, String tableType) throws IOException {
        // Check if section header needs new page
        if (pageHelper.needsNewPage(y + 30)) {
            contentStream.close();
            currentPage = pageHelper.createNewPage();
            contentStream = pageHelper.createContentStream(currentPage);
            y = PDFPageHelper.TOP_MARGIN;
        }

        // Add section header
        y = pageHelper.addSectionHeader(contentStream, sectionTitle, y);

        // Add appropriate table
        switch (tableType) {
            case "users":
                return addUserTableWithPagination(document, contentStream, currentPage, y, pageHelper);
            case "books":
                return addBookTableWithPagination(document, contentStream, currentPage, y, pageHelper);
            case "loans":
                return addLoanTableWithPagination(document, contentStream, currentPage, y, pageHelper);
            default:
                return new PDFResult(contentStream, currentPage, y);
        }
    }

}
