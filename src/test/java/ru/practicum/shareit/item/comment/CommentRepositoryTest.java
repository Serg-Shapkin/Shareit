package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/schema.sql")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private final Comment comment = new Comment();

    private final Item item = new Item();

    private final User user = User
            .builder()
            .id(1L)
            .name("user")
            .email("user@mail.ru")
            .build();

    private final LocalDateTime dateTime = LocalDateTime.now();

    @Test
    @DisplayName("Create")
    void shouldReturnCreatedUser() {
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setOwnerId(user.getId());

        userRepository.save(user);
        itemRepository.save(item);

        comment.setText("Add comment from user1");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(dateTime);

        Comment savedComment = commentRepository.save(comment);

        assertEquals(1, savedComment.getId());
        assertEquals("Add comment from user1", savedComment.getText());
        assertEquals(user, savedComment.getAuthor());
        assertEquals(item, savedComment.getItem());
        assertEquals(dateTime, savedComment.getCreated());
    }

    @Test
    @DisplayName("Find comment by item id")
    void testFindByItemId() {
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setOwnerId(user.getId());

        userRepository.save(user);
        itemRepository.save(item);

        comment.setText("Add comment from user1");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(dateTime);

        Comment savedComment = commentRepository.save(comment);

        List<Comment> commentList = commentRepository.findAllByItem_Id(item.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertEquals(1, commentList.size());
        assertEquals("Add comment from user1", commentList.get(0).getText());
        assertEquals(user, commentList.get(0).getAuthor());
        assertEquals(item, commentList.get(0).getItem());
        assertEquals(dateTime, commentList.get(0).getCreated());
    }
}
