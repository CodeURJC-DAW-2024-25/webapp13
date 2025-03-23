package es.codeurjc13.librored.mapper;

import es.codeurjc13.librored.dto.LoanCreateDTO;
import es.codeurjc13.librored.dto.LoanDTO;
import es.codeurjc13.librored.model.Loan;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "lender.id", target = "lenderId")
    @Mapping(source = "borrower.id", target = "borrowerId")
    @Mapping(source = "status", target = "status")
    LoanDTO toDTO(Loan loan);

    List<LoanDTO> toDTOs(List<Loan> loans);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)       // Será asignado en el servicio
    @Mapping(target = "lender", ignore = true)
    @Mapping(target = "borrower", ignore = true)
    @Mapping(target = "status", constant = "Active")
    Loan toDomain(LoanCreateDTO dto);
}
