package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @NotNull(message = "Отсутствует дата и время начала бронирования")
    @Future(message = "Значение даты должно быть в будущем")
    @Column(name = "start_date")
    private LocalDateTime start;

    @NotNull(message = "Отсутствует дата и время конца бронирования")
    @FutureOrPresent(message = "Значение даты должно быть в будущем (включая настоящее)")
    @Column(name = "end_date")
    private LocalDateTime end;

    @NotNull(message = "Отсутствует вещь, которую бронирует пользователь")
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull(message = "Отсутствует пользователь, который осуществляет бронирование")
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @NotNull(message = "Отсутствует статус бронирования")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
