package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingEmployees;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployees;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList.OnboardingEmployeesRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = {"ROLE_ADMIN"})
public class NewEmployeeTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OnboardingEmployeesRepository onboardingEmployeesRepository;

    @Test
    public void testAddEmployeeStoresExactInformationInDatabase() throws Exception {

        // Given
        String firstName = "Test First Name";
        String lastName = "test Last Name";
        String emailAddress = "email@email.com";
        String address = "Test Address";
        String phoneNumber = "123456789";
        String emergencyContactName = "Test Emergency Contact Name";
        String emergencyContactPhoneNumber = "987654321";
        LocalDate dateOfBirth = LocalDate.parse("2021-01-01");
        long bankSortCode = 123456L;
        long bankAccountNo = 12345678L;
        String nationalInsuranceNo = "AB123456C";
        String role = "Test Role";
        String employmentStatus = "Full Time";
        LocalDate startDate = LocalDate.parse("2021-01-01");
        String notes = "Test Notes";
        boolean inductionComplete = true;

        List<OnboardingEmployees> storedEmployeesBefore = onboardingEmployeesRepository.findAll();

        // When
        mockMvc.perform(post("/list")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("emailAddress", emailAddress)
                        .param("address", address)
                        .param("phoneNumber", phoneNumber)
                        .param("emergencyContactName", emergencyContactName)
                        .param("emergencyContactPhoneNumber", emergencyContactPhoneNumber)
                        .param("dateOfBirth", dateOfBirth.toString())
                        .param("bankSortCode", String.valueOf(bankSortCode))
                        .param("bankAccountNo", String.valueOf(bankAccountNo))
                        .param("nationalInsuranceNo", nationalInsuranceNo)
                        .param("role", role)
                        .param("employmentStatus", employmentStatus)
                        .param("startDate", startDate.toString())
                        .param("notes", notes)
                        .param("inductionComplete", String.valueOf(inductionComplete))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Then
        List<OnboardingEmployees> storedEmployeesAfter = onboardingEmployeesRepository.findAll();
        OnboardingEmployees newEmployee = storedEmployeesAfter.get(storedEmployeesAfter.size() - 1);

        assertEquals(storedEmployeesBefore.size() + 1, storedEmployeesAfter.size());
        assertEquals(firstName, newEmployee.getFirstName());
        assertEquals(lastName, newEmployee.getLastName());
        assertEquals(emailAddress, newEmployee.getEmailAddress());
        assertEquals(address, newEmployee.getAddress());
        assertEquals(phoneNumber, newEmployee.getPhoneNumber());
        assertEquals(emergencyContactName, newEmployee.getEmergencyContactName());
        assertEquals(emergencyContactPhoneNumber, newEmployee.getEmergencyContactPhoneNumber());
        assertEquals(dateOfBirth, newEmployee.getDateOfBirth());
        assertEquals(bankSortCode, newEmployee.getBankSortCode());
        assertEquals(bankAccountNo, newEmployee.getBankAccountNo());
        assertEquals(nationalInsuranceNo, newEmployee.getNationalInsuranceNo());
        assertEquals(role, newEmployee.getRole());
        assertEquals(employmentStatus, newEmployee.getEmploymentStatus());
        assertEquals(startDate, newEmployee.getStartDate());
        assertEquals(notes, newEmployee.getNotes());
        assertEquals(inductionComplete, newEmployee.getInductionComplete());
    }

    @Test
    public void testAddEmployeeWithEmptyCrucialData() throws Exception {
        // Given
        String firstName = ""; // empty first name
        String lastName = ""; // empty last name
        String emailAddress = ""; // empty email address
        String address = "Test Address";
        String phoneNumber = "123456789";
        String emergencyContactName = "Test Emergency Contact Name";
        String emergencyContactPhoneNumber = "987654321";
        LocalDate dateOfBirth = LocalDate.parse("2021-01-01");
        long bankSortCode = 123456L;
        long bankAccountNo = 12345678L;
        String nationalInsuranceNo = "AB123456C";
        String role = "Test Role";
        String employmentStatus = "Full Time";
        LocalDate startDate = LocalDate.parse("2021-01-01");
        String notes = "Test Notes";
        boolean inductionComplete = true;

        List<OnboardingEmployees> storedEmployeesBefore = onboardingEmployeesRepository.findAll();

        // When
        MvcResult mvcResult = mockMvc.perform(post("/list")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("emailAddress", emailAddress)
                        .param("address", address)
                        .param("phoneNumber", phoneNumber)
                        .param("emergencyContactName", emergencyContactName)
                        .param("emergencyContactPhoneNumber", emergencyContactPhoneNumber)
                        .param("dateOfBirth", dateOfBirth.toString())
                        .param("bankSortCode", String.valueOf(bankSortCode))
                        .param("bankAccountNo", String.valueOf(bankAccountNo))
                        .param("nationalInsuranceNo", nationalInsuranceNo)
                        .param("role", role)
                        .param("employmentStatus", employmentStatus)
                        .param("startDate", startDate.toString())
                        .param("notes", notes)
                        .param("inductionComplete", String.valueOf(inductionComplete))
                        .with(csrf()))
                .andReturn();

        // Then
        List<OnboardingEmployees> storedEmployeesAfter = onboardingEmployeesRepository.findAll();
        String content = mvcResult.getResponse().getContentAsString();

        assertEquals(storedEmployeesBefore.size(), storedEmployeesAfter.size());
        assertTrue(content.contains("First name cannot be empty"));
        assertTrue(content.contains("Last name cannot be empty"));
        assertTrue(content.contains("Email cannot be empty"));
    }

//    Given employee email is not in the correct format, when I proceed to submit the form, then submission should be prevented and a clear error message should be shown.
    @Test
    public void testAddEmployeeWithInvalidData() throws Exception {
        // Given
        String firstName = "Test First Name";
        String lastName = "test Last Name";
        String emailAddress = "email"; // invalid email address
        String address = "Test Address";
        String phoneNumber = "123456789";
        String emergencyContactName = "Test Emergency Contact Name";
        String emergencyContactPhoneNumber = "987654321";
        LocalDate dateOfBirth = LocalDate.parse("2021-01-01");
        long bankSortCode = 123456L;
        long bankAccountNo = 12345678L;
        String nationalInsuranceNo = "National Insurance No"; // invalid email address
        String role = "Test Role";
        String employmentStatus = "Employment Status"; // invalid employment status
        LocalDate startDate = LocalDate.parse("2021-01-01");
        String notes = "Test Notes";
        boolean inductionComplete = true;

        List<OnboardingEmployees> storedEmployeesBefore = onboardingEmployeesRepository.findAll();

        // When
        MvcResult mvcResult = mockMvc.perform(post("/list")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("emailAddress", emailAddress)
                        .param("address", address)
                        .param("phoneNumber", phoneNumber)
                        .param("emergencyContactName", emergencyContactName)
                        .param("emergencyContactPhoneNumber", emergencyContactPhoneNumber)
                        .param("dateOfBirth", dateOfBirth.toString())
                        .param("bankSortCode", String.valueOf(bankSortCode))
                        .param("bankAccountNo", String.valueOf(bankAccountNo))
                        .param("nationalInsuranceNo", nationalInsuranceNo)
                        .param("role", role)
                        .param("employmentStatus", employmentStatus)
                        .param("startDate", startDate.toString())
                        .param("notes", notes)
                        .param("inductionComplete", String.valueOf(inductionComplete))
                        .with(csrf()))
                .andReturn();

        // Then
        List<OnboardingEmployees> storedEmployeesAfter = onboardingEmployeesRepository.findAll();
        String content = mvcResult.getResponse().getContentAsString();

        assertEquals(storedEmployeesBefore.size(), storedEmployeesAfter.size());
        assertTrue(content.contains("Email invalid"));
        assertTrue(content.contains("National insurance number must be in the format XX000000X"));
        assertTrue(content.contains("Employment status must be Full Time, Part Time, Contractor or Intern"));
    }
}
