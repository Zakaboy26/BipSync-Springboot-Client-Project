package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;

import com.structurizr.annotation.UsedByPerson;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployeesService;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.List;

@Controller
@UsedByPerson(name = "HR", description = "Manages tasks", technology = "http(s)")
@UsedByPerson(name = "Department Member", description = "Views and completes tasks", technology = "http(s)")
public class tasklistController {

    private final TaskService taskService;

    private final OnboardingEmployeesService onboardingEmployeesService;

    public tasklistController(TaskService taskService, OnboardingEmployeesService onboardingEmployeesService) {
        this.taskService = taskService;
        this.onboardingEmployeesService = onboardingEmployeesService;
    }

    @GetMapping("/on-boarding/{id}/add-task")
    public ModelAndView addTask(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("TaskList/newTaskForm.html");
        modelAndView.addObject("employeeId", id);
        modelAndView.addObject("task", new Task());
        return modelAndView;
    }

    @PostMapping("/on-boarding/{id}/add-task")
    public ModelAndView addTask(@PathVariable Long id, @Valid Task task, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("TaskList/newTaskForm.html", model.asMap());
            modelAndView.addObject("employeeId", id);
            return modelAndView;
        } else {
            task.setEmployeeId(id);
            taskService.addNewTask(task);
            return new ModelAndView("redirect:on-boarding/{id}");
        }
    }

    @GetMapping ("/task-list")
    public ModelAndView getTask() {
        ModelAndView modelAndView = new ModelAndView("TaskList/taskList");
        List<Task> tasks =taskService.getTasks();
        modelAndView.addObject("tasks", tasks);
        return modelAndView;
    }

    @GetMapping ("/on-boarding/{id}")
    public ModelAndView getTasksByEmployeeId(@PathVariable Long id, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("TaskList/taskList");

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        List<Task> tasks;
        if ("ROLE_ADMIN".equals(role)) {
            tasks = taskService.getTasksByEmployeeId(id);
        } else {
            // Non-admin users  see tasks based on their role and employee ID
            tasks = taskService.getTasksForRoleAndEmployee(role, id);
        }

        //List<Task> tasks =taskService.getTasksByEmployeeId(id);
        double taskCompletionProgress = taskService.getTaskCompletionProgressByEmployeeId(id);
        OnboardingEmployees onboardingEmployees = onboardingEmployeesService.getOnboardingEmployeeById(id);
        modelAndView.addObject("employeeId", id);
        modelAndView.addObject("employee", onboardingEmployees);
        modelAndView.addObject("tasks", tasks);
        modelAndView.addObject("localDate", LocalDate.now());
        modelAndView.addObject("taskCompletionProgress", taskCompletionProgress);
        modelAndView.addObject("isOnboardingPage", true);

        return modelAndView;
    }

    @GetMapping ("/task-list/edit/{id}")
    public ModelAndView editTask(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("TaskList/editTaskForm.html");
        Task task = taskService.getTask(id);
        modelAndView.addObject("task", task);
        return modelAndView;
    }

    @PostMapping ("/task-list/edit/{id}")
    public ModelAndView editTask(@PathVariable Long id, @Valid Task task, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("TaskList/editTaskForm.html", model.asMap());
        } else {
            Long employeeId = taskService.getTask(id).getEmployeeId();
            task.setEmployeeId(employeeId);
            taskService.updateTask(task);
            return new ModelAndView("redirect:on-boarding/" + employeeId);
        }
    }


    @PostMapping("/task-list/{id}")
    public ModelAndView deleteTask(@PathVariable("id") Long taskId) {
        Task task = taskService.getTask(taskId);
        Long employeeId = task.getEmployeeId();
        taskService.deleteTask(taskId);
        return new ModelAndView("redirect:on-boarding/" + employeeId);
    }

    @PostMapping("/task-list/movetodo/{id}")
    public ModelAndView moveToDoTask(@PathVariable("id") Long taskId) {
        Task task = taskService.getTask(taskId);
        taskService.moveToDoTask(taskId, task);
        Long employeeId = task.getEmployeeId();
        return new ModelAndView("redirect:on-boarding/" + employeeId);
    }
    @PostMapping("/task-list/moveinprogress/{id}")
    public ModelAndView moveInProgressTask(@PathVariable("id") Long taskId) {
        Task task = taskService.getTask(taskId);
        taskService.moveInProgressTask(taskId, task);
        Long employeeId = task.getEmployeeId();
        return new ModelAndView("redirect:on-boarding/" + employeeId);
    }
}

