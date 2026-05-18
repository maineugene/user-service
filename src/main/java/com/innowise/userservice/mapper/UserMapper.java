package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;
import com.innowise.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponseDto toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentCards", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDto requestDto);
}
