package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList;
import com.structurizr.annotation.UsedByPerson;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;


import org.springframework.web.servlet.ModelAndView;

@Controller
@UsedByPerson(name = "HR", description = "Manages onboarding employees", technology = "http(s)")
@UsedByPerson(name = "New Employee", description = "Views their data", technology = "http(s)")
@UsedByPerson(name = "Department Member", description = "View onboarding employees", technology = "http(s)")
public class OnboardingEmployeesController {
	private OnboardingEmployeesService onboardingEmployeesService;

	public OnboardingEmployeesController(OnboardingEmployeesService onboardingEmployeesService) {
		super();
		this.onboardingEmployeesService = onboardingEmployeesService;
	}

	@GetMapping("/list")
	public ModelAndView listOnboardingEmployees(Model model) {
		ModelAndView modelAndView = new ModelAndView("OnboardingList/onboardingList");
		modelAndView.addObject("OnboardingEmployees", onboardingEmployeesService.getAllOnboardingEmployees());
		return modelAndView;
	}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping("/list")
	public ModelAndView saveOnboardingEmployee(@Valid @ModelAttribute("OnboardingEmployee") OnboardingEmployees onboardingEmployees, BindingResult result) {
		if (result.hasErrors()) {
			// Validation failed, return to the form with errors
			ModelAndView modelAndView = new ModelAndView("OnboardingList/newEmployee");
			modelAndView.addObject("OnboardingEmployee", onboardingEmployees);
			return modelAndView;
		} else {
			onboardingEmployeesService.saveOnboardingEmployee(onboardingEmployees);
			return new ModelAndView("redirect:/list");
		}
	}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/new")
	public ModelAndView createOnboardingEmployeeForm(Model model) {
		ModelAndView modelAndView = new ModelAndView("OnboardingList/newEmployee");
		modelAndView.addObject("OnboardingEmployee", new OnboardingEmployees());
		return modelAndView;
	}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/list/edit/{id}")
	public ModelAndView editOnboardingEmployeeForm(@PathVariable Long id, Model model) {
		ModelAndView modelAndView = new ModelAndView("OnboardingList/updateEmployee");
		modelAndView.addObject("OnboardingEmployee", onboardingEmployeesService.getOnboardingEmployeeById(id));
		return modelAndView;
	}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/list/{id}")
	public ModelAndView deleteOnboardingEmployee(@PathVariable Long id) {
		onboardingEmployeesService.deleteOnboardingEmployeeById(id);
		return new ModelAndView("redirect:/list");
	}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping("/list/{id}")
	public ModelAndView updateOnboardingEmployee(@PathVariable Long id,@Valid @ModelAttribute("OnboardingEmployee") OnboardingEmployees onboardingEmployees, BindingResult result) {
		if (result.hasErrors()) {
			// Validation failed, return to the form with errors
			ModelAndView modelAndView = new ModelAndView("OnboardingList/updateEmployee");
			modelAndView.addObject("OnboardingEmployee", onboardingEmployees);
			return modelAndView;
		} else {
			onboardingEmployeesService.editOnboardingEmployee(onboardingEmployees);
			return new ModelAndView("redirect:/list");
		}
	}
}
