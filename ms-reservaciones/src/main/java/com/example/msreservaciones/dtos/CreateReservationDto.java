package com.example.msreservaciones.dtos;
import com.example.msreservaciones.Entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationDto {
    private Long studentId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String notes;
}