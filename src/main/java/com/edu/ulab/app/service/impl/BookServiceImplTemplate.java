package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.DeleteExeption;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.UpdateExeption;
import com.edu.ulab.app.service.BookService;
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
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        return getPreparedStatement(bookDto, INSERT_SQL, connection);
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        log.info("Got a book update request with {}", bookDto);
        final String UPDATE_SQL = "UPDATE BOOK SET userId=?, title=?, author=?, pageCount=? WHERE id=?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if(jdbcTemplate.update(
                connection -> getPreparedStatement(bookDto, UPDATE_SQL, connection),
                keyHolder) != 1){
            throw new UpdateExeption("Failed to delete book: " + bookDto);
        }
//        Или можно использовать это:
//        jdbcTemplate.update("UPDATE BOOK SET userId=?, title=?, author=?, pageCount=? WHERE id=?",
//                bookDto.getUserId(),bookDto.getTitle(), bookDto.getAuthor(), bookDto.getPageCount(), bookDto.getId());
        return bookDto;
    }
    private PreparedStatement getPreparedStatement(BookDto bookDto, String UPDATE_SQL, Connection connection) throws SQLException {
        PreparedStatement ps =
                connection.prepareStatement(UPDATE_SQL, new String[]{"id"});
        ps.setString(1, bookDto.getTitle());
        ps.setString(2, bookDto.getAuthor());
        ps.setLong(3, bookDto.getPageCount());
        ps.setLong(4, bookDto.getUserId());
        return ps;
    }
    @Override
    public List<BookDto> getAllBooks() {
        log.info("Got a request for all books");
        String sql = "SELECT * FROM BOOK";

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper(BookDto.class));
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Got a book get request with id {}", id);
        if(id == null){
            throw new NullPointerException("Id may not be null");
        }
        final String GET_SQL = "SELECT * FROM BOOK WHERE id=?";
        return jdbcTemplate.query(GET_SQL, new Object[]{id}, (rs, rowNum) ->
                        new BookDto(
                                rs.getLong("id"),
                                rs.getLong("userId"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getLong("pageCount")))
                .stream()
                .filter(Objects::nonNull)
                .findAny()
                .orElseThrow(() ->new NotFoundException("Book with id " + id + " not found"));

        // Или можно сделать так
        //return (BookDto) jdbcTemplate.queryForObject(GET_SQL, new Object[]{id}, new BeanPropertyRowMapper(BookDto.class));

    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Got a book delete request with id {}", id);
        if(id == null){
            throw new NullPointerException("Id may not be null");
        }
        final String DELETE_SQL = "DELETE FROM BOOK WHERE id=?";
        Object[] args = new Object[] {id};

        if(jdbcTemplate.update(DELETE_SQL, args) != 1){
            throw new DeleteExeption("Failed to delete book with id " + id);
        }
    }
}
