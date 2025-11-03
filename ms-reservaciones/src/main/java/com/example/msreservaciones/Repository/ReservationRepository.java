package com.example.msreservaciones.Repository;


import com.example.msreservaciones.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByStudentId(Long studentId);
    
    List<Reservation> findByRoomId(Long roomId);
    
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId " +
           "AND r.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((r.checkInDate <= :checkOut AND r.checkOutDate >= :checkIn))")
    List<Reservation> findConflictingReservations(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' " +
           "AND r.checkOutDate < :currentDate")
    List<Reservation> findExpiredActiveReservations(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED' " +
           "AND r.checkInDate <= :currentDate")
    List<Reservation> findReservationsReadyForActivation(@Param("currentDate") LocalDate currentDate);
}