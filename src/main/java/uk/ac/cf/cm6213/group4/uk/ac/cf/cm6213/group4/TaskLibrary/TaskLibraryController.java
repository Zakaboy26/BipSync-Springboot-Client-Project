package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import com.structurizr.annotation.UsedByPerson;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@UsedByPerson(name = "HR", description = "Manages tasks library", technology = "http(s)")
public class TaskLibraryController {

    private final TaskLibraryService taskLibraryService;

    public TaskLibraryController(TaskLibraryService taskLibraryService) {
        this.taskLibraryService = taskLibraryService;
    }

    @GetMapping("/tasks-library/{employeeId}")
    public ModelAndView getTasksLibrary(@PathVariable Long employeeId) {
        ModelAndView modelAndView = new ModelAndView("TaskLibrary/tasksLibrary");
        TaskLibrary taskLibrary = taskLibraryService.getTaskLibrary();
        modelAndView.addObject("taskLibrary", taskLibrary);
        modelAndView.addObject("employeeId", employeeId);
        return modelAndView;
    }

    @PostMapping("/tasks-library/{employeeId}")
    public ModelAndView addTasks(@PathVariable Long employeeId, @RequestParam(required = false) Boolean selectAll, @RequestParam(required = false) Boolean unselectAll, TaskLibrary taskLibrary) {
        if (Boolean.TRUE.equals(selectAll)) {
            taskLibrary = taskLibraryService.getTaskLibrary();
            taskLibrary = taskLibraryService.selectAllTasks(taskLibrary);

            ModelAndView modelAndView = new ModelAndView("TaskLibrary/tasksLibrary");
            modelAndView.addObject("taskLibrary", taskLibrary);
            modelAndView.addObject("employeeId", employeeId);
            return modelAndView;

        } else if (Boolean.TRUE.equals(unselectAll)) {
            taskLibrary = taskLibraryService.getTaskLibrary();
            taskLibrary = taskLibraryService.unselectAllTasks(taskLibrary);

            ModelAndView modelAndView = new ModelAndView("TaskLibrary/tasksLibrary");
            modelAndView.addObject("taskLibrary", taskLibrary);
            modelAndView.addObject("employeeId", employeeId);
            return modelAndView;

        } else {
            taskLibraryService.addTasks(taskLibrary, employeeId);
            return new ModelAndView("redirect:on-boarding/{employeeId}");
        }
    }

    @GetMapping("/tasks-library/{employeeId}/add-task")
    public ModelAndView addTask(@PathVariable Long employeeId) {
        ModelAndView modelAndView = new ModelAndView("TaskLibrary/newTaskLibraryForm");
        modelAndView.addObject("employeeId", employeeId);
        modelAndView.addObject("taskLibraryItem", new TaskLibraryItem());
        return modelAndView;
    }

    @PostMapping("/tasks-library/{employeeId}/add-task")
    public ModelAndView addTask(@PathVariable Long employeeId, @Valid TaskLibraryItem taskLibraryItem, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("TaskLibrary/newTaskLibraryForm", model.asMap());
        } else {
            taskLibraryService.insertTask(taskLibraryItem);
            return new ModelAndView("redirect:tasks-library/{employeeId}");
        }
    }

    @GetMapping("/tasks-library/{employeeId}/edit-task/{id}")
    public ModelAndView editTask(@PathVariable Long employeeId, @PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("TaskLibrary/editTaskLibraryForm");
        TaskLibraryItem taskLibraryItem = taskLibraryService.getTaskLibraryItem(id);
        modelAndView.addObject("employeeId", employeeId);
        modelAndView.addObject("taskLibraryItem", taskLibraryItem);
        return modelAndView;
    }

    @PostMapping("/tasks-library/{employeeId}/edit-task/{id}")
    public ModelAndView editTask(@PathVariable Long employeeId, @PathVariable Long id, @Valid TaskLibraryItem taskLibraryItem, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("TaskLibrary/editTaskLibraryForm", model.asMap());
        } else {
            taskLibraryService.updateTask(taskLibraryItem);
            return new ModelAndView("redirect:tasks-library/{employeeId}");
        }
    }

    @GetMapping("/tasks-library/{employeeId}/delete-task/{id}")
    public ModelAndView deleteTask(@PathVariable Long employeeId, @PathVariable Long id) {
        taskLibraryService.deleteTask(id);
        return new ModelAndView("redirect:tasks-library/{employeeId}");
    }
}
