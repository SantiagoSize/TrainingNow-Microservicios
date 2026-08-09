package com.tn.usuarios.dto;

import com.tn.usuarios.model.User;
import lombok.*;

/**
 * DTO de usuario. Contrato exacto con el cliente Android (Gson).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String role;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String profilePhotoUrl;
    private Long birthDate;
    private Double height;
    private Double weight;
    private String gender;
    private String specializations;
    private Long suspendedUntil;
    private String suspendReason;
    private Boolean isBanned;
    private String banReason;

    public static UserDto fromEntity(User u) {
        return UserDto.builder()
                .id(u.getId())
                .role(u.getRole())
                .name(u.getName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .password(u.getPassword())
                .profilePhotoUrl(u.getProfilePhotoUrl())
                .birthDate(u.getBirthDate())
                .height(u.getHeight())
                .weight(u.getWeight())
                .gender(u.getGender())
                .specializations(u.getSpecializations())
                .suspendedUntil(u.getSuspendedUntil())
                .suspendReason(u.getSuspendReason())
                .isBanned(u.getIsBanned())
                .banReason(u.getBanReason())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .id(id != null && id > 0 ? id : null)
                .role(role != null ? role : "USER")
                .name(name)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(password)
                .profilePhotoUrl(profilePhotoUrl)
                .birthDate(birthDate)
                .height(height)
                .weight(weight)
                .gender(gender)
                .specializations(specializations)
                .suspendedUntil(suspendedUntil)
                .suspendReason(suspendReason)
                .isBanned(isBanned != null ? isBanned : false)
                .banReason(banReason)
                .build();
    }
}
