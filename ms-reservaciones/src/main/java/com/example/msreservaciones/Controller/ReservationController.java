package com.example.msreservaciones.Controller;

import com.example.msreservaciones.Entity.Reservation;
import com.example.msreservaciones.Service.ReservationService;
import com.example.msreservaciones.dtos.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // Crear reserva
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody CreateReservationDto createDto) {
        ReservationDto created = reservationService.createReservation(createDto);
        return ResponseEntity.ok(created);
    }

    // Obtener reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    // Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    // Obtener reservas por estudiante
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(reservationService.getReservationsByStudentId(studentId));
    }

    // Obtener reservas por habitación
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(reservationService.getReservationsByRoomId(roomId));
    }

    // Obtener reservas por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationDto>> getReservationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(
                Reservation.ReservationStatus.valueOf(status.toUpperCase())
        ));
    }

    // Obtener reservas activas
    @GetMapping("/active")
    public ResponseEntity<List<ReservationDto>> getActiveReservations() {
        return ResponseEntity.ok(reservationService.getActiveReservations());
    }

    // Actualizar reserva
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id,
                                                            @RequestBody UpdateReservationDto updateDto) {
        return ResponseEntity.ok(reservationService.updateReservation(id, updateDto));
    }

    // Eliminar reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    // Confirmar reserva
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    // Activar reserva (ocupación)
    @PostMapping("/{id}/activate")
    public ResponseEntity<ReservationDto> activateReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.activateReservation(id));
    }

    // Completar reserva (check-out)
    @PostMapping("/{id}/complete")
    public ResponseEntity<ReservationDto> completeReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.completeReservation(id));
    }

    // Cancelar reserva
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable Long id,
                                                            @RequestBody CancelReservationDto cancelDto) {
        return ResponseEntity.ok(reservationService.cancelReservation(id, cancelDto));
    }
}
