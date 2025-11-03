package com.example.msestudiantes.Controller;


import com.example.msestudiantes.Service.StudentService;
import com.example.msestudiantes.dtos.CreateStudentDto;
import com.example.msestudiantes.dtos.StudentDto;
import com.example.msestudiantes.dtos.UpdateStudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Crear un estudiante
    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody CreateStudentDto createDto) {
        StudentDto student = studentService.createStudent(createDto);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    // Obtener estudiante por ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    // Obtener estudiante por código
    @GetMapping("/code/{studentCode}")
    public ResponseEntity<StudentDto> getStudentByCode(@PathVariable String studentCode) {
        StudentDto student = studentService.getStudentByCode(studentCode);
        return ResponseEntity.ok(student);
    }

    // Obtener todos los estudiantes
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // Obtener estudiantes activos
    @GetMapping("/active")
    public ResponseEntity<List<StudentDto>> getActiveStudents() {
        List<StudentDto> students = studentService.getActiveStudents();
        return ResponseEntity.ok(students);
    }

    // Actualizar estudiante
    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id,
                                                    @RequestBody UpdateStudentDto updateDto) {
        StudentDto updatedStudent = studentService.updateStudent(id, updateDto);
        return ResponseEntity.ok(updatedStudent);
    }

    // Eliminar estudiante
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // Agregar habitación al historial
    @PostMapping("/{id}/rooms/{roomId}")
    public ResponseEntity<Void> addRoomToHistory(@PathVariable Long id, @PathVariable Long roomId) {
        studentService.addRoomToHistory(id, roomId);
        return ResponseEntity.ok().build();
    }
}
