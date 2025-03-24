package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.dto.external.OpenLibraryAuthorSearchResponseDTO;
import es.codeurjc13.librored.service.ExternalAuthorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/openlibrary/authors")
public class ExternalAuthorRestController {

    private final ExternalAuthorService externalAuthorService;

    public ExternalAuthorRestController(ExternalAuthorService externalAuthorService) {
        this.externalAuthorService = externalAuthorService;
    }

    @GetMapping("/info")
    public Mono<OpenLibraryAuthorSearchResponseDTO.AuthorData> getAuthorInfo(@RequestParam String name) {
        return externalAuthorService.getAuthorInfo(name);
    }
}
