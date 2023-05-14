package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

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
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

    private final CommentDto commentDto = CommentDto
            .builder()
            .id(1L)
            .text("text")
            .itemId(1L)
            .authorName("author")
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

    @Test
    @DisplayName("Comment to CommentDTO")
    void testCommentToCommentDto() {
        CommentDto commentDto1 = CommentMapper.toCommentDto(comment);

        assertEquals(commentDto1.getId(), commentDto.getId());
        assertEquals(commentDto1.getText(), commentDto.getText());
        assertEquals(commentDto1.getAuthorName(), commentDto.getAuthorName());
        assertEquals(commentDto1.getCreated(), commentDto.getCreated());
    }

    @Test
    @DisplayName("CommentDTO to Comment")
    void testCommentDtoToComment() {
        Comment comment1 = CommentMapper.toComment(commentDto, author, item);

        assertEquals(comment1.getId(), comment.getId());
        assertEquals(comment1.getText(), comment.getText());
        assertEquals(comment1.getItem().getId(), comment.getItem().getId());
        assertEquals(comment1.getAuthor(), comment.getAuthor());
    }
}
