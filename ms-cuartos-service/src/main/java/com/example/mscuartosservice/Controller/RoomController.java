package com.example.mscuartosservice.Controller;

import com.example.mscuartosservice.Entity.Room;
import com.example.mscuartosservice.Service.RoomService;
import com.example.mscuartosservice.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // Crear una nueva habitación
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody CreateRoomDto createDto) {
        RoomDto createdRoom = roomService.createRoom(createDto);
        return ResponseEntity.ok(createdRoom);
    }

    // Obtener habitación por ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // Obtener habitación por número
    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<RoomDto> getRoomByNumber(@PathVariable String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomByNumber(roomNumber));
    }

    // Obtener todas las habitaciones
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // Obtener habitaciones disponibles
    @GetMapping("/available")
    public ResponseEntity<List<RoomDto>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    // Obtener habitaciones por tipo
    @GetMapping("/type/{type}")
    public ResponseEntity<List<RoomDto>> getRoomsByType(@PathVariable String type) {
        return ResponseEntity.ok(roomService.getRoomsByType(Room.RoomType.valueOf(type.toUpperCase())));
    }

    // Obtener habitaciones por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoomDto>> getRoomsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(roomService.getRoomsByStatus(Room.RoomStatus.valueOf(status.toUpperCase())));
    }

    // Actualizar habitación
    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody UpdateRoomDto updateDto) {
        return ResponseEntity.ok(roomService.updateRoom(id, updateDto));
    }

    // Eliminar habitación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // Verificar disponibilidad
    @GetMapping("/{id}/availability")
    public ResponseEntity<RoomAvailabilityDto> checkAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.checkAvailability(id));
    }

    // Cambiar estado de habitación
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateRoomStatus(@PathVariable Long id, @RequestParam String status) {
        roomService.updateRoomStatus(id, Room.RoomStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok().build();
    }

    // Reservar habitación
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveRoom(@PathVariable Long id) {
        roomService.reserveRoom(id);
        return ResponseEntity.ok().build();
    }

    // Ocupar habitación
    @PostMapping("/{id}/occupy")
    public ResponseEntity<Void> occupyRoom(@PathVariable Long id) {
        roomService.occupyRoom(id);
        return ResponseEntity.ok().build();
    }

    // Liberar habitación
    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseRoom(@PathVariable Long id) {
        roomService.releaseRoom(id);
        return ResponseEntity.ok().build();
    }
}
