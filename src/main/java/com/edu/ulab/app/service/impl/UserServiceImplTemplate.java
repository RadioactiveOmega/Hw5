package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.DeleteExeption;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        log.info("Got a user update request with {}", userDto);
        final String UPDATE_SQL = "UPDATE USER SET fullName=?, title=?, age=? WHERE id=?";
        jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(), userDto.getTitle(), userDto.getAge(), userDto.getId());
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Got a user get request with id {}", id);
        if(id == null){
            throw new NullPointerException("Id may not be null");
        }
        final String GET_SQL = "SELECT * FROM USER WHERE id=?";
        return (UserDto) jdbcTemplate.queryForObject(GET_SQL, new Object[]{id}, new BeanPropertyRowMapper(UserDto.class));
    }

    @Override
    public List<UserDto> getAllUsers() {
        final String GET_ALL_SQL = "SELECT * FROM USER";
        return jdbcTemplate.query(GET_ALL_SQL, new BeanPropertyRowMapper(UserDto.class));
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Got a user delete request with id {}", id);
        if(id == null){
            throw new NullPointerException("Id may not be null");
        }
        final String DELETE_SQL = "DELETE FROM USER WHERE id=?";
        Object[] args = new Object[] {id};

        if(jdbcTemplate.update(DELETE_SQL, args) != 1){
            throw new DeleteExeption("Failed to delete user with id " + id);
        }
    }
}
