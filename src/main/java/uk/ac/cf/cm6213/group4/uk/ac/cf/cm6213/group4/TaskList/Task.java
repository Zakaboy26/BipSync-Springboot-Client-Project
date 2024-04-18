package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Task {
    private Long id;
    @Length(min = 2, max = 50, message = "The title must be between 2 and 50 characters long")
    private String title;
    @Length(max = 255, message = "The description must be less than 255 characters long")
    private String description;
    private String type;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Future(message = "The deadline must be in the future")
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long employeeId;

    public Task() {
        this(0L, "", "", "", null, null, null, null, null);
    }
}
