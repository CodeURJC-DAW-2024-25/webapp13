package es.codeurjc13.librored.mapper;

import es.codeurjc13.librored.dto.UserDTO;
import es.codeurjc13.librored.dto.UserCreateDTO;
import es.codeurjc13.librored.dto.UserUpdateDTO;
import es.codeurjc13.librored.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "encodedPassword", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toDomain(UserCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "encodedPassword", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);
}
