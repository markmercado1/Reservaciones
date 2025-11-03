package com.example.mscuartosservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityDto {
    private Long roomId;
    private Boolean available;
    private String message;
}