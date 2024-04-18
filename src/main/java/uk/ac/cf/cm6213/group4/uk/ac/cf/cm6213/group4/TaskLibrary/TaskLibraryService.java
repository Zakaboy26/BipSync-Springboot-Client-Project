package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

public interface TaskLibraryService {
    TaskLibrary getTaskLibrary();
    TaskLibraryItem getTaskLibraryItem(Long id);
    void addTasks(TaskLibrary taskLibrary, Long employeeId);
    void insertTask(TaskLibraryItem taskLibraryItem);
    void updateTask(TaskLibraryItem taskLibraryItem);
    void deleteTask(Long taskId);
    TaskLibrary selectAllTasks(TaskLibrary taskLibrary);
    TaskLibrary unselectAllTasks(TaskLibrary taskLibrary);
}
