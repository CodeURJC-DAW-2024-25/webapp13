package es.codeurjc13.librored.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private static final String DEFAULT_NAME = "Librored";
    private static final String AUTHOR_NAME = "Pepita Flores";
    private static final String TITLE_NAME = "Una Rosa";

    @GetMapping("/")
    public String index(Model viewModel) {
        addNameAttribute(viewModel, DEFAULT_NAME);
        addNameAttribute(viewModel, AUTHOR_NAME, TITLE_NAME);

        return "index";
    }

    private void addNameAttribute(Model model, String name) {
        model.addAttribute("name", name);
    }

    private void addNameAttribute(Model model, String bauthor, String btitle) {
        model.addAttribute("bauthor", bauthor);
        model.addAttribute("btitle", btitle);
    }


}
