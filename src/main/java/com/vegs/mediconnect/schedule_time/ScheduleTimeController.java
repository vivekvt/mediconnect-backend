package com.vegs.mediconnect.schedule_time;

import com.vegs.mediconnect.schedule.Schedule;
import com.vegs.mediconnect.schedule.ScheduleRepository;
import com.vegs.mediconnect.util.CustomCollectors;
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
@RequestMapping("/scheduleTimes")
public class ScheduleTimeController {

    private final ScheduleTimeService scheduleTimeService;
    private final ScheduleRepository scheduleRepository;

    public ScheduleTimeController(final ScheduleTimeService scheduleTimeService,
            final ScheduleRepository scheduleRepository) {
        this.scheduleTimeService = scheduleTimeService;
        this.scheduleRepository = scheduleRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("sSTscheduleTimeIdValues", scheduleRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Schedule::getId, Schedule::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("scheduleTimes", scheduleTimeService.findAll());
        return "scheduleTime/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("scheduleTime") final ScheduleTimeDTO scheduleTimeDTO) {
        return "scheduleTime/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("scheduleTime") @Valid final ScheduleTimeDTO scheduleTimeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "scheduleTime/add";
        }
        scheduleTimeService.create(scheduleTimeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("scheduleTime.create.success"));
        return "redirect:/scheduleTimes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id, final Model model) {
        model.addAttribute("scheduleTime", scheduleTimeService.get(id));
        return "scheduleTime/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id,
            @ModelAttribute("scheduleTime") @Valid final ScheduleTimeDTO scheduleTimeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "scheduleTime/edit";
        }
        scheduleTimeService.update(id, scheduleTimeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("scheduleTime.update.success"));
        return "redirect:/scheduleTimes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final UUID id,
            final RedirectAttributes redirectAttributes) {
        scheduleTimeService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("scheduleTime.delete.success"));
        return "redirect:/scheduleTimes";
    }

}
