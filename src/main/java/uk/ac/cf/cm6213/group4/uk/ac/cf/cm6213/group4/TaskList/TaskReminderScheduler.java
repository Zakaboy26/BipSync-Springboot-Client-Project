package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskReminderScheduler {

    private final TaskService taskService;

    @Autowired
    public TaskReminderScheduler(TaskService taskService) {
        this.taskService = taskService;
    }
    //Reminders are scheduled for 12pm (as long as a task deadline falls within 7 days)
    @Scheduled(cron = "0 0 12 * * ?")
    public void scheduleTaskWithCron() {
        taskService.sendReminderEmails();
    }
}
