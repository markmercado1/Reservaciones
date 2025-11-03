package com.example.msreservaciones.feign;
import com.example.msreservaciones.dtos.StudentDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-estudiantes-service", path = "/students")
public interface StudentServiceClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "studentByIdCB", fallbackMethod = "fallbackGetStudentById")
    StudentDto getStudentById(@PathVariable("id") Long id);

    default StudentDto fallbackGetStudentById(Long id, Throwable e) {
        System.err.println("⚠️ CircuitBreaker: ms-estudiantes no disponible (ID: " + id + ")");
        return StudentDto.builder()
                .id(0L)
                .firstName("Desconocido")
                .email("desconocido@service.local")
                .active(false)
                .build();
    }
}