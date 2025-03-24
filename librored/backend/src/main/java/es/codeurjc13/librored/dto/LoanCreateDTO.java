package es.codeurjc13.librored.dto;

import java.time.LocalDate;

public record LoanCreateDTO(
        Long bookId,
        Long lenderId,
        Long borrowerId,
        LocalDate startDate,
        LocalDate endDate
) {
}
