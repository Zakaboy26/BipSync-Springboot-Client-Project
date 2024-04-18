package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

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
public class EditLibraryTaskTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskLibraryRepository taskLibraryRepository;

    @Test
    public void testEditTaskFormFilledWithInitialDetails() throws Exception {

            // Given
            String url = "/tasks-library/1/edit-task/1";

            // When
            MvcResult result = mockMvc
                    .perform(get(url))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();

            // Then
            assert content.contains("value=\"Employee Documentation\"");
            assert content.contains("\"Collect and verify all necessary documents from the new employee\"");
            assert content.contains("value=\"HR\"");
    }

    @Test
    public void testEditTaskEditsSuccessfully() throws Exception {

        //Given
        Long taskId = 1L;
        String url = "/tasks-library/1/edit-task/" + taskId;
        String title = "Test Task";
        String description = "Test Description";
        String type = "Test Type";

        List<TaskLibraryItem> storedTasksBefore = taskLibraryRepository.getTaskLibrary().taskLibraryItems;

        //When
        mockMvc.perform(post(url)
                        .param("title", title)
                        .param("description", description)
                        .param("type", type)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        List<TaskLibraryItem> storedTasksAfter = taskLibraryRepository.getTaskLibrary().taskLibraryItems;
        TaskLibraryItem storedTask = taskLibraryRepository.getTaskLibraryItem(taskId);

        //Then
        assertEquals(storedTasksBefore.size(), storedTasksAfter.size());
        assert storedTask.getTitle().equals(title);
        assert storedTask.getDescription().equals(description);
        assert storedTask.getType().equals(type);
    }

    @Test
    public void testEditTaskWithoutTitle() throws Exception{

        // Given
        String initialTitle = "Employee Documentation";
        String initialDescription = "Collect and verify all necessary documents from the new employee";
        String initialType = "HR";

        Long taskId = 1L;
        String url = "/tasks-library/1/edit-task/" + taskId;
        String title = "";
        String description = "Test Description";
        String type = "Test Type";

        List<TaskLibraryItem> storedTasksBefore = taskLibraryRepository.getTaskLibrary().taskLibraryItems;

        // When
        MvcResult mvcResult = mockMvc.perform(post(url)
                        .param("title", title)
                        .param("description", description)
                        .param("type", type)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andReturn();

        List<TaskLibraryItem> storedTasksAfter = taskLibraryRepository.getTaskLibrary().taskLibraryItems;
        TaskLibraryItem storedTask = taskLibraryRepository.getTaskLibraryItem(taskId);
        String content = mvcResult.getResponse().getContentAsString();

        // Then
        assertThat(content, containsString("The title must be between 2 and 50 characters long"));
        assert storedTask.getTitle().equals(initialTitle);
        assert storedTask.getDescription().equals(initialDescription);
        assert storedTask.getType().equals(initialType);
    }
}
