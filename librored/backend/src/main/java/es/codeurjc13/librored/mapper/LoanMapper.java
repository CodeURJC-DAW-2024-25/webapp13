package es.codeurjc13.librored.mapper;

import es.codeurjc13.librored.dto.LoanCreateDTO;
import es.codeurjc13.librored.dto.LoanDTO;
import es.codeurjc13.librored.dto.LoanUpdateDTO;
import es.codeurjc13.librored.model.Loan;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    // Loan -> LoanDTO
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "lender.id", target = "lenderId")
    @Mapping(source = "borrower.id", target = "borrowerId")
    @Mapping(source = "status", target = "status")
    LoanDTO toDTO(Loan loan);

    // LoanCreateDTO -> Loan
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "lender", ignore = true)
    @Mapping(target = "borrower", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Loan toDomain(LoanCreateDTO dto);

    // LoanUpdateDTO -> existing Loan
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "lender", ignore = true)
    @Mapping(target = "borrower", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateLoanFromDto(LoanUpdateDTO dto, @MappingTarget Loan loan);
}
