package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository {
    void insertTask(Task task);
    List<Task> getTasks();
    Task getTask(Long id);
    List<Task> getTasksByEmployeeId(Long employeeId);
    void updateTask(Long id, Task task);
    void deleteTask(Long taskId);
    void moveToDoTask(Long id, Task task);
    void moveInProgressTask(Long id, Task task);
    List<Task> getTasksWithUpcomingDeadlines(LocalDateTime deadline);
    List<Task> getTasksByEmployeeIdAndDepartment(Long employeeId, String department);

}
