package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReportService reportService;

    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String adminDashboard() {
        return "admin";
    }


    @GetMapping("/download-report")
    public void downloadReport(HttpServletResponse response) throws IOException {
        // Set HTTP response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Admin_Report.pdf");

        // Generate PDF using service layer
        byte[] pdfData = reportService.generateAdminReport();
        
        // Write PDF data to response
        response.getOutputStream().write(pdfData);
        response.getOutputStream().flush();
    }

}
