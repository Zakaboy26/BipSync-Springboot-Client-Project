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


import java.time.LocalDate;
import java.util.List;
import java.util.Queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = "ROLE_ADMIN")
public class EditTaskTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testEditTaskFormFilledWithInitialDetails() throws Exception {

        // Given
        String url = "/task-list/edit/1";

        // When
        MvcResult result = mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        // Then
        assert content.contains("value=\"test task\"");
        assert content.contains("currently just test data for a task");
        assert content.contains("value=\"IT\"");
        assert content.contains("value=\"2023-11-01\"");
    }

    @Test
    public void testEditTaskEditsTaskSuccessfully() throws Exception{
        // Given
        String url = "/task-list/edit/1";
        String title = "Buy a laptop";
        String description = "MacBook Air";
        String type = "IT";
        LocalDate deadline = LocalDate.parse("2099-12-10");

        List<Task> storedTasksBefore = taskRepository.getTasks();

        // When
        mockMvc.perform(post(url)
                        .param("title", title)
                        .param("description", description)
                        .param("type", type)
                        .param("deadline", deadline.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then
        Task storedTask = taskRepository.getTask(1L);

        assert storedTask.getTitle().equals(title);
        assert storedTask.getDescription().equals(description);
        assert storedTask.getType().equals(type);
        assert storedTask.getDeadline().equals(deadline);
    }

    @Test
    public void testEditTaskWithoutTitle() throws Exception{
        // Given
        String url = "/task-list/edit/1";

        String initialTitle = "test task";
        String initialDescription = "currently just test data for a task";
        String initialType = "IT";
        LocalDate initialDeadline = LocalDate.parse("2023-11-01");

        String title = "";
        String description = "MacBook Air";
        String type = "IT";
        LocalDate deadline = LocalDate.parse("2023-12-10");

        // When
        MvcResult mvcResult = mockMvc.perform(post(url)
                        .param("title", title)
                        .param("description", description)
                        .param("type", type)
                        .param("deadline", deadline.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andReturn();

        // Then
        String content = mvcResult.getResponse().getContentAsString();

        Task storedTask = taskRepository.getTask(1L);
        assertThat(content, containsString("The title must be between 2 and 50 characters long"));
        assert storedTask.getTitle().equals(initialTitle);
        assert storedTask.getDescription().equals(initialDescription);
        assert storedTask.getType().equals(initialType);
        assert storedTask.getDeadline().equals(initialDeadline);
    }
}
