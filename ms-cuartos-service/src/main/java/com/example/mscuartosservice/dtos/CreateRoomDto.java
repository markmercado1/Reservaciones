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
public class CreateRoomDto {
    private String roomNumber;
    private Room.RoomType type;
    private Integer capacity;
    private Integer floor;
    private BigDecimal pricePerMonth;
    private String description;
    private List<String> additionalServices;
}