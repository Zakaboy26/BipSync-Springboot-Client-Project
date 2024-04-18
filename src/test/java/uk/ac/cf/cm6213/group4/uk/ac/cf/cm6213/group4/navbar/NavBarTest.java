package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.navbar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = {"ROLE_ADMIN"})
public class NavBarTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testBipsyncLogoIsDisplayed() throws Exception {

        // Given
        String url = "/list";

        // When
        MvcResult result = mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        // Then
        assert content.contains("src=\"/images/bipsync-logo.svg\"");
    }

    @Test
    public void testLogoutButtonIsDisplayed() throws Exception {

        // Given
        String url = "/list";

        // When
        MvcResult result = mockMvc
                .perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        // Then
        assert content.contains("Logout");
    }

    @Test
    public void testNavBarIsDisplayedOnEveryPage() throws Exception {
        // Given
        String[] urls = {"/list", "/on-boarding/1", "/on-boarding/1/add-task", "/task-list/edit/1", "/list/edit/1", "/new", "/tasks-library/1", "/tasks-library/1/add-task"};

        // When
        for (String url : urls) {
            MvcResult result = mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();

            // Then
            assertTrue(content.contains("</nav>"));
        }
    }
}