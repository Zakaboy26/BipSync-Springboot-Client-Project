package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
public interface TaskService {
    List<Task> getTasks();
    Task getTask(Long id);
    List<Task> getTasksByEmployeeId(Long employeeId);
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    void addTask(Task task);
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    void addNewTask(Task task);
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    void updateTask(Task task);
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    void deleteTask(Long taskId);
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_IT', 'ROLE_HR')")
    void moveToDoTask(Long taskId, Task task);
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_IT', 'ROLE_HR')")
    void moveInProgressTask(Long taskId, Task task);
    void sendReminderEmails();

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_IT', 'ROLE_HR')")
    List<Task> getTasksForRoleAndEmployee(String role, Long employeeId);

    double getTaskCompletionProgressByEmployeeId(Long employeeId);

}
