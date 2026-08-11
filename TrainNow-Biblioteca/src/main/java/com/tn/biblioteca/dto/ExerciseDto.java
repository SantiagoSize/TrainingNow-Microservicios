package com.tn.biblioteca.dto;

import com.tn.biblioteca.model.Ejercicio;
import lombok.*;

/** DTO de ejercicio. Contrato exacto con el cliente Android. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDto {

    private Long id;
    private String name;
    private String category;
    private String description;
    private String videoUrl;
    private String imageUrl;
    private String muscles;
    private String difficulty;
    private String equipment;
    private String instructions;
    private String tips;
    private String commonMistakes;
    private Integer recommendedSets;
    private String recommendedReps;
    private Integer restSeconds;
    private Boolean isSystemDefault;

    public static ExerciseDto fromEntity(Ejercicio e) {
        return ExerciseDto.builder()
                .id(e.getId())
                .name(e.getName())
                .category(e.getCategory())
                .description(e.getDescription())
                .videoUrl(e.getVideoUrl())
                .imageUrl(e.getImageUrl())
                .muscles(e.getMuscles())
                .difficulty(e.getDifficulty())
                .equipment(e.getEquipment())
                .instructions(e.getInstructions())
                .tips(e.getTips())
                .commonMistakes(e.getCommonMistakes())
                .recommendedSets(e.getRecommendedSets())
                .recommendedReps(e.getRecommendedReps())
                .restSeconds(e.getRestSeconds())
                .isSystemDefault(e.getIsSystemDefault())
                .build();
    }

    public Ejercicio toEntity() {
        return Ejercicio.builder()
                .id(id != null && id > 0 ? id : null)
                .name(name)
                .category(category)
                .description(description)
                .videoUrl(videoUrl)
                .imageUrl(imageUrl)
                .muscles(muscles)
                .difficulty(difficulty != null ? difficulty : "PRINCIPIANTE")
                .equipment(equipment)
                .instructions(instructions)
                .tips(tips)
                .commonMistakes(commonMistakes)
                .recommendedSets(recommendedSets != null ? recommendedSets : 3)
                .recommendedReps(recommendedReps)
                .restSeconds(restSeconds != null ? restSeconds : 60)
                .isSystemDefault(isSystemDefault != null ? isSystemDefault : true)
                .build();
    }
}
