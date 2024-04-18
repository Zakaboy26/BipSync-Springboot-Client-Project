package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;

import org.springframework.security.access.prepost.PreAuthorize;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TaskEmailService taskEmailService;
    @Autowired
    private OnboardingEmployeesService onboardingEmployeesService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, JavaMailSender javaMailSender, OnboardingEmployeesService onboardingEmployeesService) {
        this.taskRepository = taskRepository;
        this.javaMailSender = javaMailSender;
        this.onboardingEmployeesService = onboardingEmployeesService;
    }

    public List<Task> getTasks() {
        return taskRepository.getTasks();
    }

    public Task getTask(Long id) {
        return taskRepository.getTask(id);
    }

    public List<Task> getTasksByEmployeeId(Long employeeId) {
        return taskRepository.getTasksByEmployeeId(employeeId);
    }

    public void addTask(Task task) {
        taskRepository.insertTask(task);
        sendTaskAssignmentEmail(task);
    }

    public void addNewTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setStartedAt(null);
        task.setCompletedAt(null);
        addTask(task);
    }

    public void updateTask(Task task) {
        Task existingTask = taskRepository.getTask(task.getId());

        // Update the properties (excluding employeeId)
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setType(task.getType());
        existingTask.setDeadline(task.getDeadline());

        // Save the updated task back to the repository and send email
        taskRepository.updateTask(task.getId(), existingTask);
        sendTaskUpdateEmail(existingTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        taskRepository.deleteTask(taskId);
    }

    public void moveToDoTask(Long taskId, Task task) {
        task.setStartedAt(LocalDateTime.now());
        taskRepository.moveToDoTask(taskId, task);
    }

    public void moveInProgressTask(Long taskId, Task task) {
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.moveInProgressTask(taskId, task);
    }


    @Override
    public void sendReminderEmails() {
        List<Task> tasksWithUpcomingDeadlines = getTasksWithUpcomingDeadlines();
        for (Task task : tasksWithUpcomingDeadlines) {
            sendTaskReminderEmail(task);
        }
    }
    private void sendTaskAssignmentEmail(Task task) {
        OnboardingEmployees onboardingEmployeeTask = onboardingEmployeesService.getOnboardingEmployeeById(task.getEmployeeId());
        taskEmailService.sendTaskAssignmentEmail(task, onboardingEmployeeTask);
        onboardingEmployeeTask.setTaskCreationEmail(true);
        onboardingEmployeesService.editOnboardingEmployee(onboardingEmployeeTask);
    }

    private void sendTaskUpdateEmail(Task task) {
        OnboardingEmployees updatedEmployeeTask = onboardingEmployeesService.getOnboardingEmployeeById(task.getEmployeeId());
        taskEmailService.sendTaskUpdateEmail(task, updatedEmployeeTask);
        updatedEmployeeTask.setTaskEditEmail(true);
        onboardingEmployeesService.editOnboardingEmployee(updatedEmployeeTask);

    }

    private void sendTaskReminderEmail(Task task) {
        OnboardingEmployees onboardingEmployeeTask = onboardingEmployeesService.getOnboardingEmployeeById(task.getEmployeeId());
        taskEmailService.sendTaskReminderEmail(task, onboardingEmployeeTask);
        onboardingEmployeeTask.setTaskReminderEmail(true);
        onboardingEmployeesService.editOnboardingEmployee(onboardingEmployeeTask);
    }

    private List<Task> getTasksWithUpcomingDeadlines() {
        LocalDateTime sevenDaysLater = LocalDateTime.now().plusDays(7);
        return taskRepository.getTasksWithUpcomingDeadlines(sevenDaysLater);
    }

    @Override
    public List<Task> getTasksForRoleAndEmployee(String role, Long employeeId) {
        if ("ROLE_ADMIN".equals(role) || "ROLE_HR".equals(role)) {
            return taskRepository.getTasks();
        } else if ("ROLE_IT".equals(role)) {
            return taskRepository.getTasksByEmployeeIdAndDepartment(employeeId, "IT");
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public double getTaskCompletionProgressByEmployeeId(Long employeeId) {
        List<Task> tasksByEmployee = taskRepository.getTasksByEmployeeId(employeeId);
        if (tasksByEmployee.isEmpty()) {
            return 0.0;
        }

        double totalWeight = tasksByEmployee.size() * 100.0;
        double weightbar = 0.0;
        for (Task task : tasksByEmployee) {
            if (task.getCompletedAt() != null) {
                weightbar += 100.0;
            } else if (task.getStartedAt() != null) {
                weightbar += 50.0;
            }

        }
        return Math.round(weightbar / totalWeight * 100);
    }




}
