package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.UpdateExeption;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);


        if (userRepository.findById(user.getId()).isPresent()) {
            Person updatedUser = userRepository
                    .findById(user.getId()).get();

        log.info("Get user for update: {}", updatedUser);
        updatedUser.setFullName(user.getFullName());
        updatedUser.setAge(user.getAge());
        updatedUser.setTitle(user.getTitle());
        log.info("Updated book: {}", updatedUser);
        userRepository.save(updatedUser);
        return userMapper.personToUserDto(updatedUser);
        }
        throw new UpdateExeption("Failed to update with user: " + user + "\nUser with id " + user.getId() + " is not exist");

    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Got a request for a user with id: {}", id);
        if(id == null){
            throw new NullPointerException("Id may not be null");
        }
        Person foundPerson = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        log.info("Found user: {}", foundPerson);
        return userMapper.personToUserDto(foundPerson);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::personToUserDto)
                .toList();
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Received a request to delete a user with id: {}", id);
        if(id == null) {
            throw new NullPointerException("Id may not be null");
        }
        log.info("Delete a user with id: {}", id);
        userRepository.deleteById(id);
    }

}
