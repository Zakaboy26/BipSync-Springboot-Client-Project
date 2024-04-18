package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = "ROLE_ADMIN")
public class NewTaskTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testAddTaskStoresExactInformationInDatabase() throws Exception {

        // Given
        String title = "Buy a laptop";
        String description = "MacBook Air";
        String type = "IT";
        LocalDate deadline = LocalDate.parse("2099-12-10");

        List<Task> storedTasksBefore = taskRepository.getTasks();

        // When
        mockMvc.perform(post("/on-boarding/1/add-task")
                        .param("title", title)
                        .param("description", description)
                        .param("type", type)
                        .param("deadline", deadline.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then
        List<Task> storedTasksAfter = taskRepository.getTasks();
        Task storedTask = storedTasksAfter.get(storedTasksAfter.size() - 1);

        assertEquals(storedTasksBefore.size() + 1, storedTasksAfter.size());
        assert storedTask.getTitle().equals(title);
        assert storedTask.getDescription().equals(description);
        assert storedTask.getType().equals(type);
        assert storedTask.getDeadline().equals(deadline);
    }

    @Test
    public void testAddTaskStoresDateAndTimeOfTaskCreation() throws Exception {

        // Given
        String title = "Buy a laptop";
        String description = "MacBook Air";
        String type = "IT";
        LocalDate deadline = LocalDate.parse("2099-12-10");

        // When
        mockMvc.perform(post("/on-boarding/1/add-task")
                        .param("title", title)
                        .param("description", description)
                        .param("type", type)
                        .param("deadline", deadline.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then
        List<Task> storedTasks = taskRepository.getTasks();
        Task storedTask = storedTasks.get(storedTasks.size() - 1);

        assertNotNull(storedTask.getCreatedAt());
        assertTrue(storedTask.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(5)));
        assertTrue(storedTask.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    public void testAddTaskWithoutTitle() throws Exception {

        // Given
        String description = "MacBook Air";
        String type = "IT";
        LocalDate deadline = LocalDate.parse("2023-12-10");

        List<Task> storedTasksBefore = taskRepository.getTasks();

        // When
        MvcResult mvcResult = mockMvc.perform(post("/on-boarding/1/add-task")
                        .param("description", description)
                        .param("type", type)
                        .param("deadline", deadline.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andReturn();

        // Then
        String content = mvcResult.getResponse().getContentAsString();

        List<Task> storedTasksAfter = taskRepository.getTasks();

        assertEquals(storedTasksBefore.size(), storedTasksAfter.size());
        assertThat(content, containsString("The title must be between 2 and 50 characters long"));
    }

    @Test
    public void testAddTaskWithDeadlineInThePast() throws Exception {

        // Given
        String title = "Buy a laptop";
        String description = "MacBook Air";
        String type = "IT";
        LocalDate deadline = LocalDate.parse("2023-01-01");

        List<Task> storedTasksBefore = taskRepository.getTasks();

        // When
        MvcResult mvcResult = mockMvc.perform(post("/on-boarding/1/add-task")
                        .param("title", title)
                        .param("description", description)
                        .param("type", type)
                        .param("deadline", deadline.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andReturn();

        // Then
        String content = mvcResult.getResponse().getContentAsString();

        List<Task> storedTasksAfter = taskRepository.getTasks();

        assertEquals(storedTasksBefore.size(), storedTasksAfter.size());
        assertThat(content, containsString("The deadline must be in the future"));
    }

    @Test
    public void testTaskEmployeeIdIsStoredAppropriately() throws Exception {

        // Given
        String employeeId = "1";
        String title = "Buy a laptop";

        // When
        mockMvc.perform(post("/on-boarding/{id}/add-task", employeeId)
                        .param("title", title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then
        List<Task> storedTasks = taskRepository.getTasks();
        Task storedTask = storedTasks.get(storedTasks.size() - 1);

        assertEquals(employeeId, storedTask.getEmployeeId().toString());
    }

    @Test
    public void testEmployeePageShowsAllTasksOfThatEmployee() throws Exception {
        // Given
        String employeeId = "1";
        String title = "test task";

        // When
        MvcResult result = mockMvc.perform(get("/on-boarding/{id}", employeeId))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains(title));
    }

}
