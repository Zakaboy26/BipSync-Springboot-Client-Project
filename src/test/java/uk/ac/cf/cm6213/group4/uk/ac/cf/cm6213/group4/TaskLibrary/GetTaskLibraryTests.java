package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = "ROLE_ADMIN")
public class GetTaskLibraryTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskLibraryService taskLibraryService;

    @Test
    public void testGetTasksLibraryReturnsAllTasks() throws Exception {
        // Given
        String uri = "/tasks-library/1";

        // When
        MvcResult mvcResult = mockMvc.perform(get(uri, 1))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Employee Documentation"));
        assertTrue(content.contains("Collect and verify all necessary documents from the new employee"));
        assertTrue(content.contains("Benefits Enrollment"));
        assertTrue(content.contains("Guide the new employee through the benefits enrollment process"));
        assertTrue(content.contains("Policy Briefing"));
        assertTrue(content.contains("Brief the new employee about company policies"));
        assertTrue(content.contains("Payroll Setup"));
        assertTrue(content.contains("Setup the new employee in the payroll system"));
        assertTrue(content.contains("Employee Onboarding"));
        assertTrue(content.contains("Conduct a comprehensive onboarding process for the new employee"));
        assertTrue(content.contains("Setup Workstation"));
        assertTrue(content.contains("Setup the new employee workstation with necessary equipment and software"));
        assertTrue(content.contains("Work email"));
        assertTrue(content.contains("setup up work email address for the new employee"));
        assertTrue(content.contains("Security Training"));
        assertTrue(content.contains("Allow the new employee to complete security training"));
        assertTrue(content.contains("Access Fob"));
        assertTrue(content.contains("Setup access fob for the new employee"));
    }

    @Test
    public void testGetTasksLibraryFromServiceReturnsAllTasks() throws Exception {
        // Given
        TaskLibrary taskLibrary = taskLibraryService.getTaskLibrary();

        // When
        List<TaskLibraryItem> taskLibraryItems = taskLibrary.getTaskLibraryItems();

        // Then
        assertEquals(9, taskLibraryItems.size());
    }

    @Test
    public void testSelectAllTasksReturnsAllTasksSelected() throws Exception {
        // Given
        TaskLibrary taskLibrary = taskLibraryService.getTaskLibrary();

        // When
        taskLibrary = taskLibraryService.selectAllTasks(taskLibrary);
        List<TaskLibraryItem> taskLibraryItems = taskLibrary.getTaskLibraryItems();

        // Then
        for (TaskLibraryItem item : taskLibraryItems) {
            assertTrue(item.getIsSelected());
        }
    }

    @Test
    public void testUnselectAllTasksReturnsAllTasksUnselected() throws Exception {
        // Given
        TaskLibrary taskLibrary = taskLibraryService.getTaskLibrary();

        // When
        taskLibrary = taskLibraryService.unselectAllTasks(taskLibrary);
        List<TaskLibraryItem> taskLibraryItems = taskLibrary.getTaskLibraryItems();

        // Then
        for (TaskLibraryItem item : taskLibraryItems) {
            assertFalse(item.getIsSelected());
        }
    }
}
