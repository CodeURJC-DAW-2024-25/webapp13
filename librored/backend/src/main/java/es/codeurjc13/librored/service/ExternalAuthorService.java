package es.codeurjc13.librored.service;

import ch.qos.logback.classic.Logger;
import es.codeurjc13.librored.dto.external.OpenLibraryAuthorSearchResponseDTO;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ExternalAuthorService {

    private final WebClient webClient;
    private static final Logger log = (Logger) LoggerFactory.getLogger(ExternalAuthorService.class);

    public ExternalAuthorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://openlibrary.org").build();
    }

    public Mono<OpenLibraryAuthorSearchResponseDTO.AuthorData> getAuthorInfo(String name) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/authors.json")
                        .queryParam("q", name)
                        .build())
                .retrieve()
                .bodyToMono(OpenLibraryAuthorSearchResponseDTO.class)
                .map(response -> {
                    if (response.getDocs() != null && !response.getDocs().isEmpty()) {
                        return response.getDocs().stream()
                                .filter(author -> author.getName() != null &&
                                        author.getName().equalsIgnoreCase(name))
                                .findFirst()
                                .orElse(response.getDocs().getFirst()); // fallback to first result
                    }
                    return null;
                })
                .onErrorResume(error -> {
                    log.warn("Error fetching author info from OpenLibrary: {}", error.getMessage());
                    return Mono.just(new OpenLibraryAuthorSearchResponseDTO.AuthorData());
                });
    }
}
