package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;

@Service
public class    TaskEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendTaskAssignmentEmail(Task task, OnboardingEmployees onboardingEmployeeTask) {
        String emailContent = generateEmailContent("A new onboarding task has been assigned to you.", task, onboardingEmployeeTask);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(onboardingEmployeeTask.getEmailAddress());
        mailMessage.setSubject("Onboarding Task: " + task.getTitle());
        mailMessage.setText(emailContent);

        javaMailSender.send(mailMessage);
    }

    public void sendTaskUpdateEmail(Task task, OnboardingEmployees updatedEmployeeTask) {
        String emailContent = generateEmailContent("An onboarding task assigned to you has been updated.", task, updatedEmployeeTask);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(updatedEmployeeTask.getEmailAddress());
        mailMessage.setSubject("Updated Onboarding Task: " + task.getTitle());
        mailMessage.setText(emailContent);

        javaMailSender.send(mailMessage);
    }

    public void sendTaskReminderEmail(Task task, OnboardingEmployees onboardingEmployeeTask) {
        String emailContent = generateEmailContent("This is a reminder that the deadline for the following onboarding task is approaching.", task, onboardingEmployeeTask);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(onboardingEmployeeTask.getEmailAddress());
        mailMessage.setSubject("Reminder: Onboarding Task Deadline Approaching - " + task.getTitle());
        mailMessage.setText(emailContent);

        javaMailSender.send(mailMessage);
    }

    private String generateEmailContent(String introduction, Task task, OnboardingEmployees employee) {
        return "Dear " + employee.getFirstName() + " " + employee.getLastName() + ",\n\n"
                + introduction + "\n\n"
                + "Title: " + task.getTitle() + "\n\n"
                + "Description: " + task.getDescription() + "\n"
                + "Department: " + task.getType() + "\n"
                + "Deadline: " + task.getDeadline() + "\n\n"
                + "Please make sure to complete the task(s) before the deadline so that we can get you onboard as soon as possible.\n\n"
                + "Regards,\nBipSync";
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("Use the following link to reset your password: http://localhost:8080/reset-password-confirm/" + resetToken);

        javaMailSender.send(mailMessage);
    }
}
