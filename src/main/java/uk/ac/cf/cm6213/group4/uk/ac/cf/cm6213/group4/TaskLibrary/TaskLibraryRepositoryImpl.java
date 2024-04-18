package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList.Task;

import java.util.List;

@Repository
public class TaskLibraryRepositoryImpl implements TaskLibraryRepository {
    private JdbcTemplate jdbc;

    private RowMapper<TaskLibraryItem> taskLibraryMapper;

    public TaskLibraryRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        setTaskLibraryMapper();
    }

    private void setTaskLibraryMapper() {
        taskLibraryMapper = (rs, i) -> new TaskLibraryItem(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("type"),
                false // always set isSelected to false
        );
    }

    public TaskLibrary getTaskLibrary() {
        String sql = "SELECT * FROM tasks_library";
        return new TaskLibrary(jdbc.query(sql, taskLibraryMapper));
    }

    public TaskLibraryItem getTaskLibraryItem(Long id) {
        String sql = "SELECT * FROM tasks_library WHERE id = ?";
        return jdbc.queryForObject(sql, taskLibraryMapper, id);
    }

    public void insertTask(TaskLibraryItem taskLibraryItem) {
        String sql = "INSERT INTO tasks_library (title, description, type) VALUES (?, ?, ?)";
        jdbc.update(sql, taskLibraryItem.getTitle(), taskLibraryItem.getDescription(), taskLibraryItem.getType());
    }

    public void updateTask(TaskLibraryItem taskLibraryItem) {
        String sql = "UPDATE tasks_library SET title = ?, description = ?, type = ? WHERE id = ?";
        jdbc.update(sql, taskLibraryItem.getTitle(), taskLibraryItem.getDescription(), taskLibraryItem.getType(), taskLibraryItem.getId());
    }

    public void deleteTask(Long taskId) {
        String sql = "DELETE FROM tasks_library WHERE id = ?";
        jdbc.update(sql, taskId);
    }
}
