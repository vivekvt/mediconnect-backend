package com.vegs.mediconnect.doctor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DoctorDTO {

    private UUID id;

    @NotNull
    @Size(max = 255)
    private String firstName;

    @NotNull
    @Size(max = 255)
    private String lastName;

    @NotNull
    @Size(max = 255)
    private String experienceInYears;

    @NotNull
    @Size(max = 10)
    private String score = "5.0";

    private Integer reviewCount;

    private String about;

    private MultipartFile image;

}
