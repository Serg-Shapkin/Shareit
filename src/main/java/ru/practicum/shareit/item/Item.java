package ru.practicum.shareit.item;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;


@Entity
@Table(name = "items")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @NotBlank(message = "Название вещи отсутствует или передана пустая строка")
    @Column(name = "item_name")
    private String name;

    @NotEmpty(message = "Описание вещи отсутствует")
    @Size(max = 200)
    @Column(name = "item_description")
    private String description;

    @NotNull(message = "Статус доступности вещи отсутствует")
    @Column(name = "item_available")
    private Boolean available;

    @NotNull(message = "Владелец вещи отсутствует")
    @Column(name = "item_owner_id")
    private Long ownerId;

    @Column(name = "item_request_id")
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
