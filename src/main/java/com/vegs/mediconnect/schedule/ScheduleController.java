package com.vegs.mediconnect.schedule;

import com.vegs.mediconnect.appointment.Appointment;
import com.vegs.mediconnect.appointment.AppointmentRepository;
import com.vegs.mediconnect.doctor.Doctor;
import com.vegs.mediconnect.doctor.DoctorRepository;
import com.vegs.mediconnect.util.CustomCollectors;
import com.vegs.mediconnect.util.ReferencedWarning;
import com.vegs.mediconnect.util.WebUtils;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public ScheduleController(final ScheduleService scheduleService,
            final DoctorRepository doctorRepository,
            final AppointmentRepository appointmentRepository) {
        this.scheduleService = scheduleService;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("dSscheduleIdValues", doctorRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Doctor::getId, Doctor::getId)));
        model.addAttribute("sAscheduleIdValues", appointmentRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Appointment::getId, Appointment::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("schedules", scheduleService.findAll());
        return "schedule/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("schedule") final ScheduleDTO scheduleDTO) {
        return "schedule/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("schedule") @Valid final ScheduleDTO scheduleDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "schedule/add";
        }
        scheduleService.create(scheduleDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("schedule.create.success"));
        return "redirect:/schedules";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id, final Model model) {
        model.addAttribute("schedule", scheduleService.get(id));
        return "schedule/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id,
            @ModelAttribute("schedule") @Valid final ScheduleDTO scheduleDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "schedule/edit";
        }
        scheduleService.update(id, scheduleDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("schedule.update.success"));
        return "redirect:/schedules";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final UUID id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = scheduleService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            scheduleService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("schedule.delete.success"));
        }
        return "redirect:/schedules";
    }

}
