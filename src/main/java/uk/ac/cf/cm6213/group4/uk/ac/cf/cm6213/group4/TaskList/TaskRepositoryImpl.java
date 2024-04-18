package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private JdbcTemplate jdbc;

    private RowMapper<Task> taskMapper;

    public TaskRepositoryImpl(JdbcTemplate aJdbc) {
        this.jdbc = aJdbc;
        setTaskMapper();
    }

    private void setTaskMapper() {
        taskMapper = (rs, i) -> new Task(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("type"),
                rs.getDate("deadline")==null? null: rs.getDate("deadline").toLocalDate(),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("started_at")==null? null: rs.getTimestamp("started_at").toLocalDateTime(),
                rs.getTimestamp("completed_at")==null? null: rs.getTimestamp("completed_at").toLocalDateTime(),
                rs.getLong("employee_id")
        );
    }


    public void insertTask(Task task) {
        String sql = "INSERT INTO checklist_tasks (title, description, type, deadline, created_at, started_at, completed_at, employee_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, task.getTitle(), task.getDescription(), task.getType(), task.getDeadline(), LocalDateTime.now(), task.getStartedAt(), task.getCompletedAt(), task.getEmployeeId());
    }

    public List<Task> getTasks() {
        String sql = "SELECT * FROM checklist_tasks";
        return jdbc.query(sql, taskMapper);
    }

    public Task getTask(Long id) {
        String sql = "SELECT * FROM checklist_tasks WHERE id = ?";
        return jdbc.queryForObject(sql, taskMapper, id);
    }

    public List<Task> getTasksByEmployeeId(Long employeeId) {
        String sql = "SELECT * FROM checklist_tasks WHERE employee_id = ?";
        return jdbc.query(sql, taskMapper, employeeId);
    }

    public void updateTask(Long id, Task task) {
        String sql = "UPDATE checklist_tasks SET title = ?, description = ?, type = ?, deadline = ? WHERE id = ?";
        jdbc.update(sql, task.getTitle(), task.getDescription(), task.getType(), task.getDeadline(), id);
    }
    @Override
    public void deleteTask(Long taskId) {
        String sql = "DELETE FROM checklist_tasks WHERE id = ?";
        jdbc.update(sql, taskId);
    }
    public void moveToDoTask(Long id, Task task) {
        String sql = "UPDATE checklist_tasks SET started_at = ? WHERE id = ?";
        jdbc.update(sql, task.getStartedAt(), id);
    }

    public void moveInProgressTask(Long id, Task task) {
        String sql = "UPDATE checklist_tasks SET completed_at = ? WHERE id = ?";
        jdbc.update(sql, task.getCompletedAt(), id);
    }

    @Override
    public List<Task> getTasksWithUpcomingDeadlines(LocalDateTime deadline) {
        String sql = "SELECT * FROM checklist_tasks WHERE deadline <= ? AND deadline >= ?";
        return jdbc.query(sql, taskMapper, deadline, LocalDateTime.now());
    }

    @Override
    public List<Task> getTasksByEmployeeIdAndDepartment(Long employeeId, String department) {
        String sql = "SELECT * FROM checklist_tasks WHERE employee_id = ? AND type = ?";
        return jdbc.query(sql, taskMapper, employeeId, department);
    }
}
