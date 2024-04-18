package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import org.springframework.stereotype.Service;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList.Task;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList.TaskService;

@Service
public class TaskLibraryServiceImpl implements TaskLibraryService {
    private final TaskLibraryRepository taskLibraryRepository;

    private final TaskService TaskService;

    public TaskLibraryServiceImpl(TaskLibraryRepository taskLibraryRepository, TaskService taskService) {
        this.taskLibraryRepository = taskLibraryRepository;
        this.TaskService = taskService;
    }

    public TaskLibrary getTaskLibrary() {
        return taskLibraryRepository.getTaskLibrary();
    }

    public TaskLibraryItem getTaskLibraryItem(Long id) {
        return taskLibraryRepository.getTaskLibraryItem(id);
    }

    public void addTasks(TaskLibrary taskLibrary, Long employeeId) {
        for (TaskLibraryItem taskLibraryItem : taskLibrary.getTaskLibraryItems()) {
            if (taskLibraryItem.getIsSelected()) {
                addTask(taskLibraryItem, employeeId);
            }
        }
    }

    public void addTask(TaskLibraryItem taskLibraryItem, Long employeeId) {
        Task task = new Task();
        task.setTitle(taskLibraryItem.getTitle());
        task.setDescription(taskLibraryItem.getDescription());
        task.setType(taskLibraryItem.getType());
        task.setEmployeeId(employeeId);
        TaskService.addNewTask(task);
    }

    public void insertTask(TaskLibraryItem taskLibraryItem) {
        taskLibraryRepository.insertTask(taskLibraryItem);
    }

    public void updateTask(TaskLibraryItem taskLibraryItem) {
        taskLibraryRepository.updateTask(taskLibraryItem);
    }

    public void deleteTask(Long taskId) {
        taskLibraryRepository.deleteTask(taskId);
    }

    public TaskLibrary selectAllTasks(TaskLibrary taskLibrary) {
        for (TaskLibraryItem item : taskLibrary.getTaskLibraryItems()) {
            item.setIsSelected(true);
        }
        return taskLibrary;
    }

    public TaskLibrary unselectAllTasks(TaskLibrary taskLibrary) {
        for (TaskLibraryItem item : taskLibrary.getTaskLibraryItems()) {
            item.setIsSelected(false);
        }
        return taskLibrary;
    }
}
