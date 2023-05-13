package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {

    private final User author = User
            .builder()
            .id(1L)
            .name("author")
            .email("author@mail.ru")
            .build();

    private final User owner = User
            .builder()
            .id(1L)
            .name("owner")
            .email("owner@mail.ru")
            .build();

    private final Item item = Item
            .builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .ownerId(owner.getId())
            .build();

    private final Comment comment = Comment
            .builder()
            .id(1L)
            .text("text")
            .item(item)
            .author(author)
            .created(LocalDateTime.now())
            .build();

    private final Comment comment2 = Comment
            .builder()
            .id(1L)
            .text("text")
            .item(item)
            .author(author)
            .created(LocalDateTime.now())
            .build();

    @Test
    @DisplayName("Comment set id, text, item, author and date test")
    public void testCommentSet() {
        comment.setId(10L);
        comment.setText("newText");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2025, 1, 1, 12, 0));

        assertEquals(10L, comment.getId());
        assertEquals("newText", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals("2025-01-01T12:00", comment.getCreated().toString());
    }

    @Test
    @DisplayName("Comment equals test")
    public void testCommentEquals() {
        assertEquals(comment, comment);
        assertNotEquals(null, comment);
        assertEquals(comment, comment2);
    }

    @Test
    @DisplayName("Comment hash code test")
    public void testCommentHashCode() {
        assertEquals(Objects.hash(comment.getId()), comment.hashCode());
    }
}
