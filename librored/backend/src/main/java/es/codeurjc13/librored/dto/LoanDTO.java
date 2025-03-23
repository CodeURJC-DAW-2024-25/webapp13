package es.codeurjc13.librored.dto;

import java.time.LocalDate;

public record LoanDTO(
        Long id,
        Long bookId,
        Long lenderId,
        Long borrowerId,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
