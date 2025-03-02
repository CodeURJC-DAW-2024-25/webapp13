package es.codeurjc13.librored.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostLoginController {

    private static final Logger logger = LoggerFactory.getLogger(PostLoginController.class);

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {
        return "index";
    }

}
