package com.vegs.mediconnect.doctor;

import com.vegs.mediconnect.util.ReferencedWarning;
import com.vegs.mediconnect.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;


@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private static final String UPLOAD_DIRECTORY = Objects.requireNonNull(
            DoctorController.class.getResource("/static/doctor-img")).getPath();
    private final DoctorService doctorService;

    public DoctorController(final DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        return "doctor/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("doctor") final DoctorDTO doctorDTO) {
        return "doctor/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("doctor") @Valid final DoctorDTO doctorDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "doctor/add";
        }
        var doctorId = doctorService.create(doctorDTO);
        saveProfilePhoto(doctorId, doctorDTO.getImage());
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("doctor.create.success"));
        return "redirect:/doctors";
    }

    private void saveProfilePhoto(UUID doctorId, MultipartFile image) {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, image.getOriginalFilename());
        fileNames.append(image.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, image.getBytes());
        } catch (IOException e) {
            // TODO LOG
            System.out.println("Error to save file");
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id, final Model model) {
        model.addAttribute("doctor", doctorService.get(id));
        return "doctor/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id,
            @ModelAttribute("doctor") @Valid final DoctorDTO doctorDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "doctor/edit";
        }
        doctorService.update(id, doctorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("doctor.update.success"));
        return "redirect:/doctors";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final UUID id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = doctorService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            doctorService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("doctor.delete.success"));
        }
        return "redirect:/doctors";
    }

}
