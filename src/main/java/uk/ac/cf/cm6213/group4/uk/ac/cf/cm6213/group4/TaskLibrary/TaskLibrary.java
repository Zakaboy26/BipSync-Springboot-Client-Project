package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TaskLibrary {
    List<TaskLibraryItem> taskLibraryItems;

    public TaskLibrary() {
        this(null);
    }
}
