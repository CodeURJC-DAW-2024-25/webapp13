package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.Book;
import es.codeurjc13.librored.model.Loan;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.util.PDFPageHelper;
import es.codeurjc13.librored.util.PDFResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
public class ReportService {

    private final UserService userService;
    private final BookService bookService;
    private final LoanService loanService;

    public ReportService(UserService userService, BookService bookService, LoanService loanService) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanService = loanService;
    }

    // Generate a complete admin report with users, books, and loans
    public byte[] generateAdminReport() throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
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
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private PDFResult addSectionAndTable(PDDocument document, PDPageContentStream contentStream, 
                                        PDPage currentPage, int y, PDFPageHelper pageHelper, 
                                        String sectionTitle, String tableType) throws IOException {
        // Check if section header needs new page
        if (pageHelper.needsNewPage(y + 30)) {
            contentStream.close();
            currentPage = pageHelper.createNewPage();
            contentStream = pageHelper.createContentStream(currentPage);
            y = PDFPageHelper.getTopMargin();
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

    private PDFResult addUserTableWithPagination(PDDocument document, PDPageContentStream contentStream, 
                                                 PDPage currentPage, int y, PDFPageHelper pageHelper) throws IOException {
        List<User> users = userService.getAllUsers();
        
        // Add column headers
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin(), y);
        contentStream.showText("ID | Username | Email | Role");
        contentStream.endText();
        y -= PDFPageHelper.getLineHeight() + 5;
        
        for (User user : users) {
            if (pageHelper.needsNewPage(y)) {
                contentStream.close();
                currentPage = pageHelper.createNewPage();
                contentStream = pageHelper.createContentStream(currentPage);
                y = PDFPageHelper.getTopMargin();
                
                // Re-add headers on new page
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin(), y);
                contentStream.showText("ID | Username | Email | Role");
                contentStream.endText();
                y -= PDFPageHelper.getLineHeight() + 5;
            }

            String userText = user.getId() + " | " + user.getUsername() + " | " + user.getEmail() + " | " + user.getRole().name().replace("ROLE_", "");
            String truncatedText = pageHelper.truncateText(userText, PDType1Font.HELVETICA, 12);
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin(), y);
            contentStream.showText(truncatedText);
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();
        }
        
        return new PDFResult(contentStream, currentPage, y - 10);
    }

    private PDFResult addBookTableWithPagination(PDDocument document, PDPageContentStream contentStream, 
                                                 PDPage currentPage, int y, PDFPageHelper pageHelper) throws IOException {
        List<Book> books = bookService.getAllBooks();
        
        // Add column headers
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin(), y);
        contentStream.showText("ID | Title | Author | Genre");
        contentStream.endText();
        y -= PDFPageHelper.getLineHeight() + 5;
        
        for (Book book : books) {
            if (pageHelper.needsNewPage(y)) {
                contentStream.close();
                currentPage = pageHelper.createNewPage();
                contentStream = pageHelper.createContentStream(currentPage);
                y = PDFPageHelper.getTopMargin();
                
                // Re-add headers on new page
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin(), y);
                contentStream.showText("ID | Title | Author | Genre");
                contentStream.endText();
                y -= PDFPageHelper.getLineHeight() + 5;
            }

            String bookText = book.getId() + " | " + book.getTitle() + " | " + book.getAuthor() + " | " + book.getGenre();
            String truncatedText = pageHelper.truncateText(bookText, PDType1Font.HELVETICA, 12);
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin(), y);
            contentStream.showText(truncatedText);
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();
        }
        
        return new PDFResult(contentStream, currentPage, y - 10);
    }

    private PDFResult addLoanTableWithPagination(PDDocument document, PDPageContentStream contentStream, 
                                                 PDPage currentPage, int y, PDFPageHelper pageHelper) throws IOException {
        List<Loan> loans = loanService.getAllLoans();
        
        for (Loan loan : loans) {
            // Check if we need space for a complete loan entry (6 lines)
            if (pageHelper.needsNewPage(y - (6 * PDFPageHelper.getLineHeight()))) {
                contentStream.close();
                currentPage = pageHelper.createNewPage();
                contentStream = pageHelper.createContentStream(currentPage);
                y = PDFPageHelper.getTopMargin();
            }

            // Add loan header with ID
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin(), y);
            contentStream.showText("Loan ID: " + loan.getId());
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();

            // Add book information
            String bookText = "Book: " + loan.getBook().getTitle();
            String truncatedBookText = pageHelper.truncateText(bookText, PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin() + 15, y);
            contentStream.showText(truncatedBookText);
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();

            // Add lender information
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin() + 15, y);
            contentStream.showText("Lender: " + loan.getLender().getUsername());
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();

            // Add borrower information
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin() + 15, y);
            contentStream.showText("Borrower: " + loan.getBorrower().getUsername());
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();

            // Add status information
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin() + 15, y);
            contentStream.showText("Status: " + loan.getStatus());
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();

            // Add dates if available
            String dateInfo = "Start: " + loan.getStartDate();
            if (loan.getEndDate() != null) {
                dateInfo += " | End: " + loan.getEndDate();
            }
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(PDFPageHelper.getLeftMargin() + 15, y);
            contentStream.showText(dateInfo);
            contentStream.endText();
            y -= PDFPageHelper.getLineHeight();

            // Add spacing between loans
            y -= 5;
        }
        
        return new PDFResult(contentStream, currentPage, y);
    }
}