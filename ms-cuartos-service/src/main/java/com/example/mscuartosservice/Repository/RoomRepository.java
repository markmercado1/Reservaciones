package com.example.mscuartosservice.Repository;

import com.example.mscuartosservice.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    
    Optional<Room> findByRoomNumber(String roomNumber);
    
    List<Room> findByStatus(Room.RoomStatus status);
    
    List<Room> findByType(Room.RoomType type);
    
    List<Room> findByFloor(Integer floor);
    
    boolean existsByRoomNumber(String roomNumber);
}