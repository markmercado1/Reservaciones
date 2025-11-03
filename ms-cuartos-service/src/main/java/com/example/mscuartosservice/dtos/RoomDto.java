package com.example.mscuartosservice.dtos;

import com.example.mscuartosservice.Entity.Room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String roomNumber;
    private Room.RoomType type;
    private Room.RoomStatus status;
    private Integer capacity;
    private Integer floor;
    private BigDecimal pricePerMonth;
    private String description;
    private List<String> additionalServices;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}