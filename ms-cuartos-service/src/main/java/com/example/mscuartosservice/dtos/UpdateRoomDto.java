package com.example.mscuartosservice.dtos;

import com.example.mscuartosservice.Entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomDto {
    private Room.RoomType type;
    private Room.RoomStatus status;
    private Integer capacity;
    private BigDecimal pricePerMonth;
    private String description;
    private List<String> additionalServices;
}
