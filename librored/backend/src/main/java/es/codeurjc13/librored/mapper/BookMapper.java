package es.codeurjc13.librored.mapper;

import es.codeurjc13.librored.dto.BookCreateDTO;
import es.codeurjc13.librored.dto.BookDTO;
import es.codeurjc13.librored.dto.BookUpdateDTO;
import es.codeurjc13.librored.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // Dominio a DTO
    @Mapping(target = "coverEndpoint", expression = "java(\"/api/books/\" + book.getId() + \"/cover\")")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.username", target = "ownerName")
    BookDTO toDTO(Book book);

    List<BookDTO> toDTOs(List<Book> books);

    // CreateDTO to Entity (without owner nor id)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "coverPic", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Book toDomain(BookCreateDTO dto);

    // UpdateDTO to Entity (without id nor owner, update coverPic and available)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "coverPic", ignore = true)
    Book toDomain(BookUpdateDTO dto);

    default String map(java.sql.Blob blob) {
        return null; // Set to null to avoid compilation error
    }

}
