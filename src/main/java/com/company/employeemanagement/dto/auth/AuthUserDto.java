package com.company.employeemanagement.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDto {

    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private String role;
    private String department;
    private String jobTitle;
    private String status;
}
