package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/schema.sql")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private final User user = User
            .builder()
            .id(1L)
            .name("user")
            .email("user@mail.ru")
            .build();

    @Test
    @DisplayName("Return create user")
    public void testReturnCreatedUser() {
        User savedUser = userRepository.save(user);

        assertEquals(1, savedUser.getId());
        assertEquals("user", savedUser.getName());
        assertEquals("user@mail.ru", savedUser.getEmail());
    }
}
