package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;


import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployeesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = "ROLE_ADMIN")
public class EmailTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private OnboardingEmployeesService onboardingEmployeesService;

    @Autowired
    private TaskReminderScheduler taskReminderScheduler;

    @Autowired
    private TaskService taskService;



    // Test employee from the database
    Long employeeId = 1L;

    private void mockOnboardingEmployee(Long id, String firstName, String lastName, String email, boolean inductionComplete) {
        OnboardingEmployees onboardingEmployees = new OnboardingEmployees();
        onboardingEmployees.setId(id);
        onboardingEmployees.setFirstName(firstName);
        onboardingEmployees.setLastName(lastName);
        onboardingEmployees.setEmailAddress(email);
        when(onboardingEmployeesService.getOnboardingEmployeeById(id)).thenReturn(onboardingEmployees);
    }

    private void mockEmployeeForLastThreeTests_databaseinteraction(
            Long id,
            String firstName,
            String lastName,
            String email,
            boolean taskCreationEmail,
            boolean taskEditEmail,
            boolean taskReminderEmail) {

        OnboardingEmployees onboardingEmployees = new OnboardingEmployees();
        onboardingEmployees.setId(id);
        onboardingEmployees.setFirstName(firstName);
        onboardingEmployees.setLastName(lastName);
        onboardingEmployees.setEmailAddress(email);
        onboardingEmployees.setTaskCreationEmail(taskCreationEmail);
        onboardingEmployees.setTaskEditEmail(taskEditEmail);
        onboardingEmployees.setTaskReminderEmail(taskReminderEmail);

        when(onboardingEmployeesService.getOnboardingEmployeeById(id)).thenReturn(onboardingEmployees);
    }

    @Test
    public void testCreateTaskAndSendEmail() throws Exception {
        // Given:
        mockOnboardingEmployee(employeeId, "Brian", "Busch", "zaka_aap@hotmail.com", false);
        OnboardingEmployees updatedEmployeeTask = onboardingEmployeesService.getOnboardingEmployeeById(employeeId);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/on-boarding/1/add-task")
                        .param("title", "New Task")
                        .param("description", "Task description")
                        .param("type", "IT")
                        .param("deadline", "2099-01-01")
                        .param("employeeId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then:
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));

        // Capture the email sent and assert its content
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(emailCaptor.capture());

        SimpleMailMessage sentEmail = emailCaptor.getValue();
        assertEquals("Onboarding Task: New Task", sentEmail.getSubject());
        assertEquals("zaka_aap@hotmail.com", sentEmail.getTo()[0]);
        assertEquals("Dear Brian Busch,\n" +
                "\n" +
                "A new onboarding task has been assigned to you.\n" +
                "\n" +
                "Title: New Task\n" +
                "\n" +
                "Description: Task description\n" +
                "Department: IT\n" +
                "Deadline: 2099-01-01\n" +
                "\n" +
                "Please make sure to complete the task(s) before the deadline so that we can get you onboard as soon as possible.\n" +
                "\n" +
                "Regards,\n" +
                "BipSync", sentEmail.getText());
    }

    @Test
    public void testUpdateTaskAndSendEmail() throws Exception {
        // Given:
        mockOnboardingEmployee(employeeId, "Brian", "Busch", "zaka_aap@hotmail.com", false);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/task-list/edit/1") // Test task data used that's in the database
                        .param("title", "Updated Task")
                        .param("description", "Updated Task description")
                        .param("type", "IT")
                        .param("deadline", "2030-02-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()); // Should redirect to the task list page after the post method

        //Then:
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(emailCaptor.capture());

        SimpleMailMessage sentEmail = emailCaptor.getValue();
        assertEquals("Updated Onboarding Task: Updated Task", sentEmail.getSubject());
        assertEquals("zaka_aap@hotmail.com", sentEmail.getTo()[0]);
        assertEquals("Dear Brian Busch,\n" +
                "\n" +
                "An onboarding task assigned to you has been updated.\n" +
                "\n" +
                "Title: Updated Task\n" +
                "\n" +
                "Description: Updated Task description\n" +
                "Department: IT\n" +
                "Deadline: 2030-02-01\n" +
                "\n" +
                "Please make sure to complete the task(s) before the deadline so that we can get you onboard as soon as possible.\n" +
                "\n" +
                "Regards,\n" +
                "BipSync", sentEmail.getText());
    }

    @Test
    public void testCreateTaskWithPastDeadline_ShouldNotSendEmail() throws Exception {
        // Given:
        mockOnboardingEmployee(employeeId, "Brian", "Busch", "zaka_aap@hotmail.com", false);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/on-boarding/1/add-task")
                        .param("title", "New Task")
                        .param("description", "Task description")
                        .param("type", "IT")
                        .param("deadline", "2002-01-01") // Past deadline used
                        .param("employeeId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk()); // No redirection as validation would fail

        // Then:
        verifyEmailNotSent();
    }

    @Test
    public void testUpdateTaskWithPastDeadline_ShouldNotSendEmail() throws Exception {
        // Given:
        mockOnboardingEmployee(employeeId, "Brian", "Busch", "zaka_aap@hotmail.com", false);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/task-list/edit/1")
                        .param("title", "Updated Task")
                        .param("description", "Updated Task description")
                        .param("type", "IT")
                        .param("deadline", "2002-01-01") // Past deadline used
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk()); // No redirection as validation would fail

        // Then:
        verifyEmailNotSent();
    }

    // This verifies that no emails were sent for the two test scenarios above
    private void verifyEmailNotSent() {
        verify(javaMailSender, times(0)).send(any(SimpleMailMessage.class));
    }



    @Test
    public void testTaskReminderEmail_ShouldBeSentIfTheDeadlineIsLessThan7DaysAway() throws Exception {
        // Given:
        mockOnboardingEmployee(employeeId, "Brian", "Busch", "zaka_aap@hotmail.com", false);

        // Calculate the deadline dynamically as the current date plus 6 days
        LocalDate currentPlus6Days = LocalDate.now().plusDays(6);
        String dynamicDeadline = currentPlus6Days.toString();

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/on-boarding/1/add-task")
                        .param("title", "Task Reminder Test")
                        .param("description", "Task reminder test description")
                        .param("type", "IT")
                        .param("deadline", dynamicDeadline)
                        .param("employeeId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then:
        taskReminderScheduler.scheduleTaskWithCron();

        /*Capture the email sent and assert its content, the reason I set the number of invocations to 2 is
        Because whenever a task gets created successfully, an email will get sent my default(task creation email), and since the deadline is
        approaching, we expect another one (email reminder) which is the one we're testing for
         */
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(2)).send(emailCaptor.capture());

        SimpleMailMessage sentEmail = emailCaptor.getValue();
        assertEquals("Reminder: Onboarding Task Deadline Approaching - Task Reminder Test", sentEmail.getSubject());
        assertEquals("zaka_aap@hotmail.com", sentEmail.getTo()[0]);
        assertEquals("Dear Brian Busch,\n" +
                "\n" +
                "This is a reminder that the deadline for the following onboarding task is approaching.\n" +
                "\n" +
                "Title: Task Reminder Test\n" +
                "\n" +
                "Description: Task reminder test description\n" +
                "Department: IT\n" +
                "Deadline: " + dynamicDeadline + "\n" +
                "\n" +
                "Please make sure to complete the task(s) before the deadline so that we can get you onboard as soon as possible.\n" +
                "\n" +
                "Regards,\n" +
                "BipSync", sentEmail.getText());
    }

    @Test
    public void testTaskReminderEmail_DeadlineMoreThan7DaysAway_ShouldNotSendEmail() throws Exception {
        // Given:
        mockOnboardingEmployee(employeeId, "Brian", "Busch", "zaka_aap@hotmail.com", false);

        // Calculate the deadline dynamically as the current date plus 10 days (more than 7 days)
        Calendar currentPlus10Days = Calendar.getInstance();
        currentPlus10Days.add(Calendar.DAY_OF_MONTH, 10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dynamicDeadline = dateFormat.format(currentPlus10Days.getTime());

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/on-boarding/1/add-task")
                        .param("title", "Long Deadline Task")
                        .param("description", "Task with a long deadline description")
                        .param("type", "IT")
                        .param("deadline", dynamicDeadline)
                        .param("employeeId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then:
        verifyReminderEmailNotSent();
    }

    /* This verifies that no reminder emails were sent for the scenario above
     EXCEPT for the task creation email (which is why the number of invocations is set to 1 instead of 0)
     */
    private void verifyReminderEmailNotSent() {
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testTaskCreationEmailFlagAfterTaskCreation() throws Exception {
        // Given:
        mockEmployeeForLastThreeTests_databaseinteraction(
                employeeId,
                "Brian",
                "Busch",
                "zaka_aap@hotmail.com",
                false,
                false,
                false);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/on-boarding/1/add-task")
                        .param("title", "New Task")
                        .param("description", "Task description")
                        .param("type", "IT")
                        .param("deadline", "2099-01-01")
                        .param("employeeId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));

        // Then: check if the taskCreationEmail field in the employee entity has been updated to true
        OnboardingEmployees updatedEmployee = onboardingEmployeesService.getOnboardingEmployeeById(employeeId);
        Assertions.assertTrue(updatedEmployee.getTaskCreationEmail());
        Assertions.assertFalse(updatedEmployee.getTaskEditEmail());
        Assertions.assertFalse(updatedEmployee.getTaskReminderEmail());
    }

    @Test
    public void testTaskEditEmailFlagAfterTaskEdit() throws Exception {
        // Given:
        mockEmployeeForLastThreeTests_databaseinteraction(
                employeeId,
                "Brian",
                "Busch",
                "zaka_aap@hotmail.com",
                true,
                false,
                false);

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/task-list/edit/1")
                        .param("title", "Updated Task")
                        .param("description", "Updated Task description")
                        .param("type", "IT")
                        .param("deadline", "2099-02-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));

        // Then: check if the taskEditEmail field in the employee entity has been updated to true
        OnboardingEmployees updatedEmployee = onboardingEmployeesService.getOnboardingEmployeeById(employeeId);
        Assertions.assertTrue(updatedEmployee.getTaskCreationEmail());
        Assertions.assertTrue(updatedEmployee.getTaskEditEmail());
        Assertions.assertFalse(updatedEmployee.getTaskReminderEmail());
    }

    @Test
    public void testTaskReminderEmailFlagAfterReminder() throws Exception {
        // Given:
        mockEmployeeForLastThreeTests_databaseinteraction(
                employeeId,
                "Brian",
                "Busch",
                "zaka_aap@hotmail.com",
                true,
                false,
                false);

        // Calculate the deadline dynamically as the current date plus 6 days
        LocalDate currentPlus6Days = LocalDate.now().plusDays(6);
        String dynamicDeadline = currentPlus6Days.toString();

        // When:
        mockMvc.perform(MockMvcRequestBuilders.post("/on-boarding/1/add-task")
                        .param("title", "Task Reminder Test")
                        .param("description", "Task reminder test description")
                        .param("type", "IT")
                        .param("deadline", dynamicDeadline)
                        .param("employeeId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        taskReminderScheduler.scheduleTaskWithCron();

        // Then: check if the taskReminderEmail field in the employee entity has been updated to true
        OnboardingEmployees updatedEmployee = onboardingEmployeesService.getOnboardingEmployeeById(employeeId);
        Assertions.assertTrue(updatedEmployee.getTaskCreationEmail());
        Assertions.assertFalse(updatedEmployee.getTaskEditEmail());
        Assertions.assertTrue(updatedEmployee.getTaskReminderEmail());
    }



}
