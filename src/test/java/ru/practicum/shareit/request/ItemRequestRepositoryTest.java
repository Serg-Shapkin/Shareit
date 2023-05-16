package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema.sql"})
public class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    private User user = User
            .builder()
            .id(1L)
            .name("User")
            .email("user@mail.ru")
            .build();

    private final ItemRequest itemRequest = ItemRequest
            .builder()
            .id(1L)
            .description("description")
            .requestor(user)
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();

        user.setId(null);
        user = userRepository.save(user);

        itemRequest.setId(null);
        itemRequest.setRequestor(user);
    }

    @Test
    @DisplayName("Save request")
    void testSaveRequest() {
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        assertEquals(1, savedRequest.getId());
    }

    @Test
    @DisplayName("Find by requestor id")
    public void testFindByRequestorId() {
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequestorId(
                user.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertEquals(1, itemRequestList.get(0).getId());
        assertEquals(user.getId(), itemRequestList.get(0).getRequestor().getId());
    }

    @Test
    @DisplayName("Find all by requestor id not")
    public void testFindAllByRequestorIdNot() {
        itemRequestRepository.save(itemRequest);

        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByRequestorIdNot(
                2L, PageRequest.of(0, 10));

        List<ItemRequest> itemRequestList = itemRequestPage.getContent();

        assertEquals(1, itemRequestList.get(0).getId());
        assertEquals(user.getId(), itemRequestList.get(0).getRequestor().getId());
    }
}
