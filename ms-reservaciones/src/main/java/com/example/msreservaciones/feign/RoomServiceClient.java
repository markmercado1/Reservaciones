package com.example.msreservaciones.feign;

import com.example.msreservaciones.dtos.RoomAvailabilityDto;
import com.example.msreservaciones.dtos.RoomDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@FeignClient(name = "ms-cuartos-service", path = "/rooms")
public interface RoomServiceClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "roomByIdCB", fallbackMethod = "fallbackGetRoomById")
    RoomDto getRoomById(@PathVariable("id") Long id);

    default RoomDto fallbackGetRoomById(Long id, Throwable e) {
        System.err.println(" CircuitBreaker: ms-cuartos no disponible (ID: " + id + ")");
        return RoomDto.builder()
                .id(0L)
                .roomNumber("Desconocido")
                .status(null)
                .build();
    }

    @GetMapping("/{id}/availability")
    @CircuitBreaker(name = "roomAvailabilityCB", fallbackMethod = "fallbackCheckAvailability")
    RoomAvailabilityDto checkAvailability(@PathVariable("id") Long id);

    default RoomAvailabilityDto fallbackCheckAvailability(Long id, Throwable e) {
        System.err.println(" CircuitBreaker: ms-cuartos no disponible (Availability ID: " + id + ")");
        return new RoomAvailabilityDto(id, false, "Servicio no disponible temporalmente");
    }

    @PostMapping("/{id}/reserve")
    @CircuitBreaker(name = "roomReserveCB", fallbackMethod = "fallbackReserveRoom")
    void reserveRoom(@PathVariable("id") Long id);

    default void fallbackReserveRoom(Long id, Throwable e) {
        System.err.println(" CircuitBreaker: ms-cuartos no disponible (Reserve ID: " + id + ")");
        throw new RuntimeException("No se pudo reservar la habitación " + id + " - servicio temporalmente indisponible");
    }

    @PostMapping("/{id}/occupy")
    @CircuitBreaker(name = "roomOccupyCB", fallbackMethod = "fallbackOccupyRoom")
    void occupyRoom(@PathVariable("id") Long id);

    default void fallbackOccupyRoom(Long id, Throwable e) {
        System.err.println(" CircuitBreaker: ms-cuartos no disponible (Occupy ID: " + id + ")");
        throw new RuntimeException("No se pudo ocupar la habitación " + id + " - servicio temporalmente indisponible");
    }

    @PostMapping("/{id}/release")
    @CircuitBreaker(name = "roomReleaseCB", fallbackMethod = "fallbackReleaseRoom")
    void releaseRoom(@PathVariable("id") Long id);

    default void fallbackReleaseRoom(Long id, Throwable e) {
        System.err.println(" CircuitBreaker: ms-cuartos no disponible (Release ID: " + id + ")");
        throw new RuntimeException("No se pudo liberar la habitación " + id + " - servicio temporalmente indisponible");
    }
}
