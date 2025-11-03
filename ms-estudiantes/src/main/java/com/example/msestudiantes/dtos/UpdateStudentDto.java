package com.example.msestudiantes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String career;
    private Integer academicCycle;
    private Boolean active;
}