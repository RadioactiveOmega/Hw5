package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {

        //given
        UserDto personToUpdateDTO = new UserDto();
        personToUpdateDTO.setId(1L);
        personToUpdateDTO.setAge(11);
        personToUpdateDTO.setFullName("test name");
        personToUpdateDTO.setTitle("test title");

        Person updatePerson  = new Person();
        updatePerson.setId(1L);
        updatePerson.setFullName("test name2");
        updatePerson.setAge(11);
        updatePerson.setTitle("test title2");

        UserDto updateResult = new UserDto();
        updateResult.setId(1L);
        updateResult.setAge(11);
        updateResult.setFullName("test name2");
        updateResult.setTitle("test title");

        //when
        when(userMapper.userDtoToPerson(personToUpdateDTO)).thenReturn(updatePerson);
        when(userRepository.findById(updatePerson.getId())).thenReturn(Optional.of(updatePerson));
        when(userRepository.save(updatePerson)).thenReturn(updatePerson);
        when(userMapper.personToUserDto(updatePerson)).thenReturn(updateResult);

        //then
        UserDto result = userService.updateUser(personToUpdateDTO);
        assertEquals("test name2", result.getFullName());
    }

    @Test
    @DisplayName("Извлечение пользователя. Должно пройти успешно.")
    void getPerson_Test() {
        Long id = 1L;

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(100);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(100);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userRepository.findById(id)).thenReturn(Optional.of(savedPerson));
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then

        UserDto getUserDto = userService.getUserById(savedPerson.getId());

        assertEquals(1L, getUserDto.getId());
        assertEquals(100, getUserDto.getAge());
        assertEquals("test name", getUserDto.getFullName());
        assertEquals("test title", getUserDto.getTitle());
    }

    @Test
    @DisplayName("Извлечение всех пользователей. Должно пройти успешно.")
    void getAllPersons_Test() {

        //given

        Person savedPerson1 = new Person();
        savedPerson1.setId(1L);
        savedPerson1.setFullName("test name1");
        savedPerson1.setAge(100);
        savedPerson1.setTitle("test title1");

        Person savedPerson2 = new Person();
        savedPerson2.setId(2L);
        savedPerson2.setFullName("test name2");
        savedPerson2.setAge(100);
        savedPerson2.setTitle("test title2");

        UserDto result1 = new UserDto();
        result1.setId(1L);
        result1.setAge(100);
        result1.setFullName("test name1");
        result1.setTitle("test title1");

        UserDto result2 = new UserDto();
        result2.setId(2L);
        result2.setAge(100);
        result2.setFullName("test name2");
        result2.setTitle("test title2");

        List<Person> allPersons = new ArrayList<>();
        allPersons.add(savedPerson1);
        allPersons.add(savedPerson2);

        //when

        when(userRepository.getAllUsers()).thenReturn(allPersons);
        when(userMapper.personToUserDto(savedPerson1)).thenReturn(result1);
        when(userMapper.personToUserDto(savedPerson2)).thenReturn(result2);

        //then

        List<UserDto> getAllUserDto = userService.getAllUsers();

        assertEquals(1L, getAllUserDto.get(0).getId());
        assertEquals(100, getAllUserDto.get(0).getAge());
        assertEquals("test name1", getAllUserDto.get(0).getFullName());
        assertEquals("test title1", getAllUserDto.get(0).getTitle());

        assertEquals(2L, getAllUserDto.get(1).getId());
        assertEquals(100, getAllUserDto.get(1).getAge());
        assertEquals("test name2", getAllUserDto.get(1).getFullName());
        assertEquals("test title2", getAllUserDto.get(1).getTitle());

        //Или можно просто проверить количество
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deletePerson_Test() {

        //given

        Long id = 1L;

        //when

        doNothing().when(userRepository).deleteById(id);

        //then

        userService.deleteUserById(id);

    }
}
