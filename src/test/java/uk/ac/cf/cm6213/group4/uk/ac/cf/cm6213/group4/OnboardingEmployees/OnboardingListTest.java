package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingEmployees;

import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployeesController;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployeesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OnboardingListTest {

    @Mock
    private OnboardingEmployeesService onboardingEmployeesService;

    @InjectMocks
    private OnboardingEmployeesController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testListOnboardingEmployees() throws Exception {
        // When:
        mockMvc.perform(get("/list"))
                .andExpect(status().isOk())
                // Then:
                .andExpect(view().name("OnboardingList/onboardingList"))
                .andExpect(model().attributeExists("OnboardingEmployees"));
    }

    @Test
    void testCreateOnboardingEmployeeForm() throws Exception {
        // When:
        mockMvc.perform(get("/new"))
                // Then:
                .andExpect(status().isOk())
                .andExpect(view().name("OnboardingList/newEmployee"))
                .andExpect(model().attributeExists("OnboardingEmployee"));
    }

    @Test
    void testSaveOnboardingEmployee_WithValidEmployee_ShouldRedirectToList() throws Exception {
        // Given:
        OnboardingEmployees employee = new OnboardingEmployees();
        employee.setId(1L);
        employee.setFirstName("Zakariya");
        employee.setLastName("Aden");
        employee.setEmailAddress("zaka_aap@hotmail.com");
        employee.setInductionComplete(true);

        // When:
        mockMvc.perform(post("/list")
                        .param("id", "1")
                        .param("firstName", "Zakariya")
                        .param("lastName", "Aden")
                        .param("emailAddress", "zaka_aap@hotmail.com")
                        .param("inductionComplete", "true"))
        //Then:
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));
        verify(onboardingEmployeesService, times(1)).saveOnboardingEmployee(any(OnboardingEmployees.class));
    }

    @Test
    void testSaveOnboardingEmployee_WithInvalidDetails_NoFirstName() throws Exception {
        // Given:
        OnboardingEmployees employee = new OnboardingEmployees();
        employee.setId(1L);
        employee.setLastName("Aden");
        employee.setEmailAddress("zaka_aap@hotmail.com");
        employee.setInductionComplete(true);

        // When:
        mockMvc.perform(post("/list")
                        .flashAttr("OnboardingEmployee", employee))
                // Then:
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("OnboardingEmployee"))
                .andExpect(model().hasErrors()) // Expecting validation errors
                .andExpect(view().name("OnboardingList/newEmployee"));
    }

    @Test
    void testUpdateEmployeeSuccessfully() throws Exception {
        // Given:
        OnboardingEmployees existingEmployee = new OnboardingEmployees();
        existingEmployee.setId(1L);
        existingEmployee.setFirstName("FirstName");
        existingEmployee.setLastName("LastName");
        existingEmployee.setEmailAddress("zaka_aap@hotmail.com");
        existingEmployee.setInductionComplete(true);
        when(onboardingEmployeesService.getOnboardingEmployeeById(1L)).thenReturn(existingEmployee);

        // When:
        existingEmployee.setFirstName("NEWFirstName");
        existingEmployee.setLastName("NEWLastName");
        mockMvc.perform(post("/list/1")
                        .flashAttr("OnboardingEmployee", existingEmployee))
        //Then:
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));
        verify(onboardingEmployeesService, times(1)).editOnboardingEmployee(any(OnboardingEmployees.class));
    }

    @Test
    void testEmployeeDeletionSuccessfully() throws Exception {
        // When:
        mockMvc.perform(get("/list/1"))
                // Then:
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));

        // Then:
        verify(onboardingEmployeesService, times(1)).deleteOnboardingEmployeeById(1L);
    }
}
