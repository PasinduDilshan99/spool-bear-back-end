package com.printing._d.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUser {
    private Long userId;
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private Integer addressId;
    private String nic;
    private Integer genderId;
    private Integer countryId;
    private String dateOfBirth;
    private String imageUrl;
    private Integer userStatusId;
    private Integer userTypeId;

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Builder.Default
    private Set<String> privileges = new HashSet<>();
}
