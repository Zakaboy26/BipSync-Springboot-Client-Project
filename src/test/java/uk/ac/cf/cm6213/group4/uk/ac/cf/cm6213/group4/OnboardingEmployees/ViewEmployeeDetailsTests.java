package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingEmployees;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployeesService;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(authorities = "ROLE_ADMIN")
@SpringBootTest
@AutoConfigureMockMvc
public class ViewEmployeeDetailsTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OnboardingEmployeesService onboardingEmployeesService;

    @Test
    public void shouldGetEmployeeDetails() throws Exception {
        OnboardingEmployees onboardingEmployees = new OnboardingEmployees();
        onboardingEmployees.setId(1L);
        onboardingEmployees.setFirstName("John");
        onboardingEmployees.setLastName("Smith");
        onboardingEmployees.setEmailAddress("john@gmail.com");
        onboardingEmployees.setAddress("1 Test Street");
        onboardingEmployees.setPhoneNumber("01234567890");
        onboardingEmployees.setBankSortCode(123456L);
        onboardingEmployees.setBankAccountNo(12345678L);
        onboardingEmployees.setEmergencyContactName("Jane Smith");
        onboardingEmployees.setEmergencyContactPhoneNumber("01234567890");
        onboardingEmployees.setRole("Developer");
        onboardingEmployees.setStartDate(LocalDate.of(2021, 1, 1));
        onboardingEmployees.setNotes("Test notes");
        onboardingEmployees.setEmploymentStatus("Full Time");
        onboardingEmployees.setNationalInsuranceNo("AB123456C");
        onboardingEmployees.setDateOfBirth(LocalDate.of(1990, 1, 1));

        given(onboardingEmployeesService.getOnboardingEmployeeById(1L)).willReturn(onboardingEmployees);

        MvcResult mvcResult = mockMvc
                .perform(get("/on-boarding/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String content = mvcResult.getResponse().getContentAsString();

        assert content.contains(onboardingEmployees.getFirstName());
        assert content.contains(onboardingEmployees.getLastName());
        assert content.contains(onboardingEmployees.getEmailAddress());
        assert content.contains(onboardingEmployees.getAddress());
        assert content.contains(onboardingEmployees.getPhoneNumber());
        assert content.contains(onboardingEmployees.getBankSortCode().toString().substring(0, 2)
                +"-"+onboardingEmployees.getBankSortCode().toString().substring(2, 4)
                +"-"+onboardingEmployees.getBankSortCode().toString().substring(4, 6));
        assert content.contains(onboardingEmployees.getBankAccountNo().toString().substring(0, 4)
                +" "+onboardingEmployees.getBankAccountNo().toString().substring(4, 8));
        assert content.contains(onboardingEmployees.getEmergencyContactName());
        assert content.contains(onboardingEmployees.getEmergencyContactPhoneNumber());
        assert content.contains(onboardingEmployees.getRole());
        assert content.contains(onboardingEmployees.getStartDate().toString());
        assert content.contains(onboardingEmployees.getNotes());
        assert content.contains(onboardingEmployees.getEmploymentStatus());
        assert content.contains(onboardingEmployees.getNationalInsuranceNo());
        assert content.contains(onboardingEmployees.getDateOfBirth().toString());
    }
}
