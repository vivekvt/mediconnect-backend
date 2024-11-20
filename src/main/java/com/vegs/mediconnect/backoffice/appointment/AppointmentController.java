package com.vegs.mediconnect.backoffice.appointment;

import com.vegs.mediconnect.backoffice.util.CustomCollectors;
import com.vegs.mediconnect.backoffice.util.ReferencedWarning;
import com.vegs.mediconnect.backoffice.util.WebUtils;
import com.vegs.mediconnect.datasource.doctor.Doctor;
import com.vegs.mediconnect.datasource.doctor.DoctorRepository;
import com.vegs.mediconnect.datasource.patient.Patient;
import com.vegs.mediconnect.datasource.patient.PatientRepository;
import com.vegs.mediconnect.datasource.schedule.Schedule;
import com.vegs.mediconnect.datasource.schedule.ScheduleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        var dataFormat = DateTimeFormatter.ofPattern("EEE, d MMM");
        model.addAttribute("patientIdValues", patientRepository.findAll(
                Sort.by("lastName", "firstName"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Patient::getId, Patient::getFullName)));
        model.addAttribute("doctorIdValues", doctorRepository.findAll(
                Sort.by("lastName", "firstName"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Doctor::getId, Doctor::getFullName)));
        model.addAttribute("scheduleTimeIdValues", scheduleRepository.findAll( // TODO Review me! refatorar-me
                Sort.by("date"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Schedule::getId,
                        schedule -> schedule.getDate().format(dataFormat))));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("appointments", appointmentService.findAll());
        return "appointment/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("appointment") final AppointmentDTO appointmentDTO) {
        return "appointment/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("appointment") @Valid final AppointmentDTO appointmentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "appointment/add";
        }
        appointmentService.create(appointmentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("appointment.create.success"));
        return "redirect:/appointments";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id, final Model model) {
        model.addAttribute("appointment", appointmentService.get(id));
        return "appointment/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id,
            @ModelAttribute("appointment") @Valid final AppointmentDTO appointmentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "appointment/edit";
        }
        appointmentService.update(id, appointmentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("appointment.update.success"));
        return "redirect:/appointments";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final UUID id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = appointmentService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            appointmentService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("appointment.delete.success"));
        }
        return "redirect:/appointments";
    }

}
