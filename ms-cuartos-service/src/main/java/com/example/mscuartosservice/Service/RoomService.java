package com.example.mscuartosservice.Service;

import com.example.mscuartosservice.Entity.Room;
import com.example.mscuartosservice.Exceptions.InvalidRoomDataException;
import com.example.mscuartosservice.Exceptions.RoomAlreadyExistsException;
import com.example.mscuartosservice.Exceptions.RoomNotAvailableException;
import com.example.mscuartosservice.Exceptions.RoomNotFoundException;
import com.example.mscuartosservice.Repository.RoomRepository;
import com.example.mscuartosservice.dtos.CreateRoomDto;
import com.example.mscuartosservice.dtos.RoomAvailabilityDto;
import com.example.mscuartosservice.dtos.RoomDto;
import com.example.mscuartosservice.dtos.UpdateRoomDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomRepository roomRepository;
    
    @Transactional
    public RoomDto createRoom(CreateRoomDto createDto) {
        validateCreateRoomDto(createDto);
        
        if (roomRepository.existsByRoomNumber(createDto.getRoomNumber())) {
            throw new RoomAlreadyExistsException("Room with number " + createDto.getRoomNumber() + " already exists");
        }
        
        Room room = new Room();
        room.setRoomNumber(createDto.getRoomNumber());
        room.setType(createDto.getType());
        room.setStatus(Room.RoomStatus.AVAILABLE);
        room.setCapacity(createDto.getCapacity());
        room.setFloor(createDto.getFloor());
        room.setPricePerMonth(createDto.getPricePerMonth());
        room.setDescription(createDto.getDescription());
        room.setAdditionalServices(createDto.getAdditionalServices());
        room.setCreatedAt(LocalDateTime.now());
        
        Room savedRoom = roomRepository.save(room);
        return mapToDto(savedRoom);
    }
    
    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        return mapToDto(room);
    }
    
    public RoomDto getRoomByNumber(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with number: " + roomNumber));
        return mapToDto(room);
    }
    
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RoomDto> getAvailableRooms() {
        return roomRepository.findByStatus(Room.RoomStatus.AVAILABLE).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RoomDto> getRoomsByType(Room.RoomType type) {
        return roomRepository.findByType(type).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<RoomDto> getRoomsByStatus(Room.RoomStatus status) {
        return roomRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public RoomDto updateRoom(Long id, UpdateRoomDto updateDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        
        if (updateDto.getType() != null) room.setType(updateDto.getType());
        if (updateDto.getStatus() != null) room.setStatus(updateDto.getStatus());
        if (updateDto.getCapacity() != null) room.setCapacity(updateDto.getCapacity());
        if (updateDto.getPricePerMonth() != null) room.setPricePerMonth(updateDto.getPricePerMonth());
        if (updateDto.getDescription() != null) room.setDescription(updateDto.getDescription());
        if (updateDto.getAdditionalServices() != null) room.setAdditionalServices(updateDto.getAdditionalServices());
        
        room.setUpdatedAt(LocalDateTime.now());
        
        Room updatedRoom = roomRepository.save(room);
        return mapToDto(updatedRoom);
    }
    
    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
        roomRepository.deleteById(id);
    }
    
    public RoomAvailabilityDto checkAvailability(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        
        boolean available = room.getStatus() == Room.RoomStatus.AVAILABLE;
        String message = available ? "Room is available" : "Room is " + room.getStatus().toString().toLowerCase();
        
        return new RoomAvailabilityDto(roomId, available, message);
    }
    
    @Transactional
    public void updateRoomStatus(Long roomId, Room.RoomStatus newStatus) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        
        room.setStatus(newStatus);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
    }
    
    @Transactional
    public void reserveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        
        if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not available for reservation");
        }
        
        room.setStatus(Room.RoomStatus.RESERVED);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
    }
    
    @Transactional
    public void occupyRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        
        if (room.getStatus() != Room.RoomStatus.RESERVED && room.getStatus() != Room.RoomStatus.AVAILABLE) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " cannot be occupied");
        }
        
        room.setStatus(Room.RoomStatus.OCCUPIED);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
    }
    
    @Transactional
    public void releaseRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        
        room.setStatus(Room.RoomStatus.AVAILABLE);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
    }
    
    private void validateCreateRoomDto(CreateRoomDto dto) {
        if (dto.getRoomNumber() == null || dto.getRoomNumber().trim().isEmpty()) {
            throw new InvalidRoomDataException("Room number is required");
        }
        if (dto.getType() == null) {
            throw new InvalidRoomDataException("Room type is required");
        }
        if (dto.getCapacity() == null || dto.getCapacity() < 1) {
            throw new InvalidRoomDataException("Valid capacity is required");
        }
        if (dto.getFloor() == null) {
            throw new InvalidRoomDataException("Floor is required");
        }
        if (dto.getPricePerMonth() == null || dto.getPricePerMonth().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidRoomDataException("Valid price per month is required");
        }
    }
    
    private RoomDto mapToDto(Room room) {
        return new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getStatus(),
                room.getCapacity(),
                room.getFloor(),
                room.getPricePerMonth(),
                room.getDescription(),
                room.getAdditionalServices(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}