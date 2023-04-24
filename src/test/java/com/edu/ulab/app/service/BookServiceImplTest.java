package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }
    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {

        //given

        Person person  = new Person();
        person.setId(1L);

        BookDto bookToUpdateDto = new BookDto();
        bookToUpdateDto.setUserId(1L);
        bookToUpdateDto.setAuthor("test author");
        bookToUpdateDto.setTitle("test title");
        bookToUpdateDto.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title1");
        book.setAuthor("test author1");
        book.setPerson(person);

        BookDto result = new BookDto();
        result.setUserId(1L);
        result.setAuthor("test author2");
        result.setTitle("test title2");
        result.setPageCount(1000);

        //when

        when(bookMapper.bookDtoToBook(bookToUpdateDto)).thenReturn(book);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.bookToBookDto(book)).thenReturn(result);

        //then

        BookDto bookDtoResult = bookService.updateBook(bookToUpdateDto);
        assertEquals("test author2", bookDtoResult.getAuthor());

    }

    @Test
    @DisplayName("Извлечение книги. Должно пройти успешно.")
    void getBook_Test() {

        //given

        Person person  = new Person();
        person.setId(1L);

        Long id = 1L;

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);


        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);


        //when

        when(bookRepository.findById(id)).thenReturn(Optional.of(savedBook));
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then

        BookDto getBookDto = bookService.getBookById(id);
        assertEquals(1L, getBookDto.getId());
        assertEquals(1L, getBookDto.getUserId());
        assertEquals(1000, getBookDto.getPageCount());
        assertEquals("test title", getBookDto.getTitle());
        assertEquals("test author", getBookDto.getAuthor());
    }

    @Test
    @DisplayName("Извлечение всех книг. Должно пройти успешно.")
    void getAllBook_Test() {

        //given

        Person person  = new Person();
        person.setId(1L);

        Long id = 1L;

        Book savedBook1 = new Book();
        savedBook1.setId(1L);
        savedBook1.setPageCount(1000);
        savedBook1.setTitle("test title");
        savedBook1.setAuthor("test author");
        savedBook1.setPerson(person);

        Book savedBook2 = new Book();
        savedBook2.setId(1L);
        savedBook2.setPageCount(1000);
        savedBook2.setTitle("test title");
        savedBook2.setAuthor("test author");
        savedBook2.setPerson(person);

        BookDto result1 = new BookDto();
        result1.setId(1L);
        result1.setUserId(1L);
        result1.setAuthor("test author");
        result1.setTitle("test title");
        result1.setPageCount(1000);

        BookDto result2 = new BookDto();
        result2.setId(1L);
        result2.setUserId(1L);
        result2.setAuthor("test author");
        result2.setTitle("test title");
        result2.setPageCount(1000);

        List<Book> allBooks = new ArrayList<>();
        allBooks.add(savedBook1);
        allBooks.add(savedBook2);

        //when

        when(bookRepository.getAllBooks()).thenReturn(allBooks);
        when(bookMapper.bookToBookDto(savedBook1)).thenReturn(result1);
        when(bookMapper.bookToBookDto(savedBook2)).thenReturn(result2);

        //then

        List<BookDto> getAllBookDto= bookService.getAllBooks();
        assertEquals(2, getAllBookDto.size());
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBook_Test() {

        //given

        Long id = 1L;

        //when

        doNothing().when(bookRepository).deleteById(id);

        //then

        bookService.deleteBookById(id);
    }
}
