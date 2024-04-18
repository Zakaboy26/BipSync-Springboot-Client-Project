package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class TaskLibraryItem {
    private Long id;
    @Length(min = 2, max = 50, message = "The title must be between 2 and 50 characters long")
    private String title;
    @Length(max = 255, message = "The description must be less than 255 characters long")
    private String description;
    private String type;
    private Boolean isSelected;

    public TaskLibraryItem() {
        this(0L, "", "", "", false);
    }
}
