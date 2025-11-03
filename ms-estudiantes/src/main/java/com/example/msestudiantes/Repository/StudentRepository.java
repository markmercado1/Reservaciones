package com.example.msestudiantes.Repository;

import com.example.msestudiantes.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByStudentCode(String studentCode);

    Optional<Student> findByStudentCode(String studentCode);

    List<Student> findByActiveTrue();
}
