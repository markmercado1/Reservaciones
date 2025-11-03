package com.example.msreservaciones.Service;

import com.example.msreservaciones.Entity.Reservation;
import com.example.msreservaciones.Exceptions.*;
import com.example.msreservaciones.Repository.ReservationRepository;
import com.example.msreservaciones.dtos.*;
import com.example.msreservaciones.feign.RoomServiceClient;
import com.example.msreservaciones.feign.StudentServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final StudentServiceClient studentServiceClient;
    private final RoomServiceClient roomServiceClient;
    
    @Transactional
    public ReservationDto createReservation(CreateReservationDto createDto) {
        validateCreateReservationDto(createDto);
        
        // Verificar que el estudiante existe y está activo
        StudentDto student;
        try {
            student = studentServiceClient.getStudentById(createDto.getStudentId());
            if (!student.getActive()) {
                throw new StudentNotFoundException("Student with id " + createDto.getStudentId() + " is not active");
            }
        } catch (FeignException.NotFound e) {
            throw new StudentNotFoundException("Student not found with id: " + createDto.getStudentId());
        }
        
        // Verificar disponibilidad de la habitación
        RoomAvailabilityDto availability;
        try {
            availability = roomServiceClient.checkAvailability(createDto.getRoomId());
            if (!availability.getAvailable()) {
                throw new RoomNotAvailableException("Room with id " + createDto.getRoomId() + " is not available: " + availability.getMessage());
            }
        } catch (FeignException.NotFound e) {
            throw new RoomNotAvailableException("Room not found with id: " + createDto.getRoomId());
        }

        // Verificar conflictos de reservas existentes
        checkReservationConflicts(createDto.getRoomId(), createDto.getCheckInDate(), createDto.getCheckOutDate(), null);

        // Reservar la habitación
        try {
            roomServiceClient.reserveRoom(createDto.getRoomId());
        } catch (FeignException e) {
            throw new RoomNotAvailableException("Failed to reserve room: " + e.getMessage());
        }

        Reservation reservation = new Reservation();
        reservation.setStudentId(createDto.getStudentId());
        reservation.setRoomId(createDto.getRoomId());
        reservation.setCheckInDate(createDto.getCheckInDate());
        reservation.setCheckOutDate(createDto.getCheckOutDate());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setNotes(createDto.getNotes());
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToDto(savedReservation);
    }

    @Transactional(readOnly = true)
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        return mapToDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByStudentId(Long studentId) {
        return reservationRepository.findByStudentId(studentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByStatus(Reservation.ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getActiveReservations() {
        return reservationRepository.findByStatus(Reservation.ReservationStatus.ACTIVE).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto updateReservation(Long id, UpdateReservationDto updateDto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new InvalidReservationStatusException("Cannot update a cancelled reservation");
        }

        if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            throw new InvalidReservationStatusException("Cannot update a completed reservation");
        }

        if (updateDto.getCheckInDate() != null || updateDto.getCheckOutDate() != null) {
            LocalDate newCheckIn = updateDto.getCheckInDate() != null ? updateDto.getCheckInDate() : reservation.getCheckInDate();
            LocalDate newCheckOut = updateDto.getCheckOutDate() != null ? updateDto.getCheckOutDate() : reservation.getCheckOutDate();

            validateDates(newCheckIn, newCheckOut);
            checkReservationConflicts(reservation.getRoomId(), newCheckIn, newCheckOut, id);

            if (updateDto.getCheckInDate() != null) reservation.setCheckInDate(updateDto.getCheckInDate());
            if (updateDto.getCheckOutDate() != null) reservation.setCheckOutDate(updateDto.getCheckOutDate());
        }

        if (updateDto.getStatus() != null) {
            validateStatusTransition(reservation.getStatus(), updateDto.getStatus());
            reservation.setStatus(updateDto.getStatus());
        }

        if (updateDto.getNotes() != null) reservation.setNotes(updateDto.getNotes());

        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDto(updatedReservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (reservation.getStatus() == Reservation.ReservationStatus.ACTIVE) {
            throw new InvalidReservationStatusException("Cannot delete an active reservation. Please cancel it first.");
        }

        reservationRepository.deleteById(id);
    }

    @Transactional
    public ReservationDto confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new InvalidReservationStatusException("Only pending reservations can be confirmed");
        }

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDto(updatedReservation);
    }

    @Transactional
    public ReservationDto activateReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (reservation.getStatus() != Reservation.ReservationStatus.CONFIRMED && reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new InvalidReservationStatusException("Only confirmed or pending reservations can be activated");
        }

        // Ocupar la habitación
        try {
            roomServiceClient.occupyRoom(reservation.getRoomId());
        } catch (FeignException e) {
            throw new RoomNotAvailableException("Failed to occupy room: " + e.getMessage());
        }

        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDto(updatedReservation);
    }

    @Transactional
    public ReservationDto completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (reservation.getStatus() != Reservation.ReservationStatus.ACTIVE) {
            throw new InvalidReservationStatusException("Only active reservations can be completed");
        }

        // Liberar la habitación
        try {
            roomServiceClient.releaseRoom(reservation.getRoomId());
        } catch (FeignException e) {
            throw new RoomNotAvailableException("Failed to release room: " + e.getMessage());
        }

        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDto(updatedReservation);
    }

    @Transactional
    public ReservationDto cancelReservation(Long id, CancelReservationDto cancelDto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new InvalidReservationStatusException("Reservation is already cancelled");
        }

        if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            throw new InvalidReservationStatusException("Cannot cancel a completed reservation");
        }

        // Liberar la habitación si estaba reservada u ocupada
        if (reservation.getStatus() == Reservation.ReservationStatus.ACTIVE ||
                reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED ||
                reservation.getStatus() == Reservation.ReservationStatus.PENDING) {
            try {
                roomServiceClient.releaseRoom(reservation.getRoomId());
            } catch (FeignException e) {
                // Log pero no fallar la cancelación
                System.err.println("Warning: Failed to release room: " + e.getMessage());
            }
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(cancelDto.getCancellationReason());
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToDto(updatedReservation);
    }

    private void validateCreateReservationDto(CreateReservationDto dto) {
        if (dto.getStudentId() == null) {
            throw new InvalidReservationDataException("Student ID is required");
        }
        if (dto.getRoomId() == null) {
            throw new InvalidReservationDataException("Room ID is required");
        }
        if (dto.getCheckInDate() == null) {
            throw new InvalidReservationDataException("Check-in date is required");
        }
        if (dto.getCheckOutDate() == null) {
            throw new InvalidReservationDataException("Check-out date is required");
        }

        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationDataException("Check-in date cannot be in the past");
        }
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new InvalidReservationDataException("Check-out date must be after check-in date");
        }
    }

    private void checkReservationConflicts(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeReservationId) {
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                roomId, checkIn, checkOut);

        if (excludeReservationId != null) {
            conflicts = conflicts.stream()
                    .filter(r -> !r.getId().equals(excludeReservationId))
                    .collect(Collectors.toList());
        }

        if (!conflicts.isEmpty()) {
            throw new ReservationConflictException(
                    "Room is already reserved for the selected dates. Conflicting reservation(s) found.");
        }
    }

    private void validateStatusTransition(Reservation.ReservationStatus currentStatus, Reservation.ReservationStatus newStatus) {
        if (currentStatus == Reservation.ReservationStatus.CANCELLED) {
            throw new InvalidReservationStatusException("Cannot change status of a cancelled reservation");
        }

        if (currentStatus == Reservation.ReservationStatus.COMPLETED) {
            throw new InvalidReservationStatusException("Cannot change status of a completed reservation");
        }

        // Validar transiciones específicas
        if (currentStatus == Reservation.ReservationStatus.PENDING && newStatus == Reservation.ReservationStatus.COMPLETED) {
            throw new InvalidReservationStatusException("Cannot complete a pending reservation directly");
        }
    }

    private ReservationDto mapToDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getStudentId(),
                reservation.getRoomId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getStatus(),
                reservation.getNotes(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt(),
                reservation.getCancelledAt(),
                reservation.getCancellationReason()
        );
    }
}