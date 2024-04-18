package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

public interface TaskLibraryRepository {
    TaskLibrary getTaskLibrary();
    TaskLibraryItem getTaskLibraryItem(Long id);
    void insertTask(TaskLibraryItem taskLibraryItem);
    void updateTask(TaskLibraryItem taskLibraryItem);
    void deleteTask(Long taskId);
}
