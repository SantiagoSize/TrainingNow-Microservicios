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
    /** Descripción/bio libre que el entrenador escribe para su perfil público. */
    private String bio;
    /** Imagen promocional del entrenador (distinta de profilePhotoUrl), su tarjeta en "Mis chats". */
    private String promoImageUrl;
    private Long suspendedUntil;
    private String suspendReason;
    private Boolean isBanned;
    private String banReason;
    private Long createdAt;
    private Long updatedAt;
    /** Última vez que la app hizo "heartbeat" con este usuario en primer plano (ver User.lastActiveAt). */
    private Long lastActiveAt;

    /** Token JWT: solo se envía en la respuesta del login. */
    private String token;

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
                .bio(u.getBio())
                .promoImageUrl(u.getPromoImageUrl())
                .suspendedUntil(u.getSuspendedUntil())
                .suspendReason(u.getSuspendReason())
                .isBanned(u.getIsBanned())
                .banReason(u.getBanReason())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .lastActiveAt(u.getLastActiveAt())
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
                .bio(bio)
                .promoImageUrl(promoImageUrl)
                .suspendedUntil(suspendedUntil)
                .suspendReason(suspendReason)
                .isBanned(isBanned != null ? isBanned : false)
                .banReason(banReason)
                .build();
    }
}
