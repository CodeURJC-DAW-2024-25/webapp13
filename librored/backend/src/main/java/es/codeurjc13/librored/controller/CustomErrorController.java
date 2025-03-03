package es.codeurjc13.librored.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            switch (statusCode) {
                case 404:
                    return "error/404";
                case 500:
                    return "error/500";
                case 403:
                    return "error/403";
                case 401:
                    return "error/401";
                default:
                    return "error/default"; // Ensure this file exists
            }

        }
        return "error/404";
    }

    @GetMapping("/loginerror")
    public String loginError() {
        return "loginerror";
    }
}
