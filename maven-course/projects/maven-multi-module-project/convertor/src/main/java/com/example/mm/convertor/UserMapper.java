package com.example.mm.convertor;

import com.example.mm.model.User;
import guru.springframework.domain.UserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper userConvertor = Mappers.getMapper(UserMapper.class);

    User userCommandToUser(UserCommand userCommand);

    UserCommand userToUserCommand(User user);

}
