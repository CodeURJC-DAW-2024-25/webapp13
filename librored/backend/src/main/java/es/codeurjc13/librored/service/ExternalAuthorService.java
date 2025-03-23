package es.codeurjc13.librored.service;

import ch.qos.logback.classic.Logger;
import es.codeurjc13.librored.dto.external.OpenLibraryAuthorSearchResponseDTO;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Service
public class ExternalAuthorService {

    private final WebClient webClient;
    private static final Logger log = (Logger) LoggerFactory.getLogger(ExternalAuthorService.class);

    public ExternalAuthorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://openlibrary.org").build();
    }

    public Mono<OpenLibraryAuthorSearchResponseDTO.AuthorData> getAuthorInfo(String authorName) {
        String uri = "/search/authors.json?q=" + UriUtils.encode(authorName, StandardCharsets.UTF_8);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(OpenLibraryAuthorSearchResponseDTO.class)
                .flatMap(response -> {
                    System.out.println("Received response from OpenLibrary for: " + authorName);
                    if (response.getDocs() != null && !response.getDocs().isEmpty()) {
                        OpenLibraryAuthorSearchResponseDTO.AuthorData author = response.getDocs().get(0);
                        System.out.println("Author found: " + author.getName());
                        return Mono.just(author);
                    } else {
                        System.out.println("No author found for: " + authorName);
                        return Mono.empty();
                    }
                })
                .onErrorResume(error -> {
                    log.warn("Error fetching author info from OpenLibrary: {}", error.getMessage());
                    return Mono.empty();
                });
    }
}
