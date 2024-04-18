package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskLibrary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = "ROLE_ADMIN")
public class DeleteLibraryTaskTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskLibraryRepository taskLibraryRepository;

    @Test
    public void testDeleteTaskDeletesSuccessfully() throws Exception {

        //Given
        long taskId = 1L;
        String url = "/tasks-library/1/delete-task/" + taskId;

        List<TaskLibraryItem> storedTasksBefore = taskLibraryRepository.getTaskLibrary().taskLibraryItems;

        //When
        mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection());

        List<TaskLibraryItem> storedTasksAfter = taskLibraryRepository.getTaskLibrary().taskLibraryItems;

        //Then
        assertEquals(storedTasksBefore.size() - 1, storedTasksAfter.size());
    }
}
