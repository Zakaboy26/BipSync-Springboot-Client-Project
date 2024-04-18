package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = "ROLE_ADMIN")
public class NewLibraryTaskTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskLibraryRepository taskLibraryRepository;

    @Test
    public void testAddTaskStoresExactInformationInDatabase() throws Exception {

            // Given
            String title = "Test Task";
            String description = "Test Description";
            String type = "Test Type";

            List<TaskLibraryItem> storedTasksBefore = taskLibraryRepository.getTaskLibrary().taskLibraryItems;

            // When
            mockMvc.perform(post("/tasks-library/1/add-task")
                            .param("title", title)
                            .param("description", description)
                            .param("type", type)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection());

            // Then
            List<TaskLibraryItem> storedTasksAfter = taskLibraryRepository.getTaskLibrary().taskLibraryItems;
            TaskLibraryItem newTask = storedTasksAfter.get(storedTasksAfter.size() - 1);

            assertEquals(storedTasksBefore.size() + 1, storedTasksAfter.size());
            assertEquals(title, newTask.getTitle());
            assertEquals(description, newTask.getDescription());
            assertEquals(type, newTask.getType());
    }

    @Test
    public void testAddTaskWithoutATitle() throws Exception {

            // Given
            String title = "";
            String description = "Test Description";
            String type = "Test Type";

            List<TaskLibraryItem> storedTasksBefore = taskLibraryRepository.getTaskLibrary().taskLibraryItems;

            // When
            MvcResult mvcResult = mockMvc.perform(post("/tasks-library/1/add-task")
                            .param("title", title)
                            .param("description", description)
                            .param("type", type)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andReturn();

            // Then
            String content = mvcResult.getResponse().getContentAsString();

            List<TaskLibraryItem> storedTasksAfter = taskLibraryRepository.getTaskLibrary().taskLibraryItems;

            assertEquals(storedTasksBefore.size(), storedTasksAfter.size());
            assertThat(content, containsString("The title must be between 2 and 50 characters long"));
    }
}
