package es.codeurjc13.librored.dto;

import java.time.LocalDate;

public record LoanUpdateDTO(
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
