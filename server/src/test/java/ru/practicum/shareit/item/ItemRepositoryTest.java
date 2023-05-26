package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema.sql"})
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user = User
            .builder()
            .name("user")
            .email("user@mail.ru")
            .build();

    private final Item item = Item
            .builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .ownerId(user.getId())
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();

        user.setId(null);
        user = userRepository.save(user);

        item.setId(null);
        item.setOwnerId(user.getId());
        item.setRequestId(1L);
    }

    @Test
    @DisplayName("Save item")
    void testSaveItem() {
        Item savedItem = itemRepository.save(item);

        assertEquals(1, savedItem.getId());
        assertEquals(item.getOwnerId(), savedItem.getOwnerId());
    }

    @Test
    @DisplayName("Find by owner id")
    void testFindByOwnerId() {
        itemRepository.save(item);
        List<Item> items = itemRepository.findByOwnerId(user.getId(), PageRequest.of(0, 10));

        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals(item.getOwnerId(), items.get(0).getOwnerId());
    }

    @Test
    @DisplayName("Find by request id")
    void testFindByRequestId() {
        itemRepository.save(item);
        List<Item> items = itemRepository.findByRequestId(1L);

        assertEquals(1, items.get(0).getId());
        assertEquals(item.getOwnerId(), items.get(0).getOwnerId());
    }

    @Test
    @DisplayName("Get items by search")
    void testGetItemsBySearch() {
        itemRepository.save(item);
        List<Item> items = itemRepository.getItemsBySearch("item", PageRequest.of(0, 10));

        assertEquals(1, items.get(0).getId());
        assertEquals(item.getOwnerId(), items.get(0).getOwnerId());
    }
}

