package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Register;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Captcha.CAPTCHAVerificationService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WithMockUser(authorities = "ROLE_ADMIN")
public class RegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegisterRepository registerRepository;

    @MockBean
    private CAPTCHAVerificationService captchaVerificationService;

    @Test
    public void testShowRegistrationForm() throws Exception {
        // Given:

        // When:
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))

                // Then:
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        // Given:
        String username = "newuser";
        String password = "password123";
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);

        // When:
        mockMvc.perform(post("/register")
                        .param("username", username)
                        .param("password", password)
                        .param("confirmPassword", password)
                        .param("email", "test@gmail.com")
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))

                // Then:
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:login"));
    }

    @Test
    public void testRegisterUserPasswordMismatch() throws Exception {
        //Given:
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);

        // When:
        mockMvc.perform(post("/register")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password456")
                        .param("email", "test@hotmail.com")
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))

                // Then:
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Passwords do not match"));
    }

    @Test
    public void testRegisterUser_UsernameFieldsNotEntered() throws Exception {
        //Given:
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);

        // When:
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .param("email", "test@gmail.com")
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))

                // Then:
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "All essential fields must be filled out"));
    }
    @Test
    public void testRegisterUser_PasswordFieldsNotEntered() throws Exception {
        //Given:
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);

        // When:
        mockMvc.perform(post("/register")
                        .param("username", "newuser")
                        .param("password", "")
                        .param("confirmPassword", "")
                        .param("email", "test@hotmail.com")
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))

                // Then:
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "All essential fields must be filled out"));
    }

    @Test
    public void testRegisterUser_UsernameAlreadyExists() throws Exception {
        //Given:
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);
        String existingUsername = "existinguser";
        String password = "password123";

        // Ensure that the user with the existing username already exists
        userDetailsManager.createUser(new org.springframework.security.core.userdetails.User(existingUsername, passwordEncoder.encode(password), new ArrayList<>()));

        // When:
        mockMvc.perform(post("/register")
                        .param("username", existingUsername)
                        .param("password", password)
                        .param("confirmPassword", password)
                        .param("email", "test@gmail.com")
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))

                // Then:
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Username already exists"));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setUsername("newuser");
        registrationForm.setPassword("password123");
        registrationForm.setConfirmPassword("password123");
        registrationForm.setEmail("zaka_aap@hotmail.com");

        mockMvc.perform(post("/register")
                        .flashAttr("registrationForm", registrationForm)
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Email already exists"));
    }

    @Test
    public void testRegisterUser_EmailNotEntered_ShouldStillRegister() throws Exception {
        // Given:
        String username = "newuser";
        String password = "password123";
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);

        // When:
        mockMvc.perform(post("/register")
                        .param("username", username)
                        .param("password", password)
                        .param("confirmPassword", password)
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))

                // Then:
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:login"));
    }

    @Test
    public void testRegisterUser_CorrectDetails_But_CaptchaNotVerified_ShouldNotCreateUser() throws Exception {
        // Given:
        String username = "newuser";
        String password = "password123";
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(false);

        // When:
        mockMvc.perform(post("/register")
                        .param("username", username)
                        .param("password", password)
                        .param("confirmPassword", password)
                        .param("g-recaptcha-response", "invalid-recaptcha-response")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"))

                //Then:
                .andExpect(model().attribute("error", "CAPTCHA verification failed. Please try again."));

    }
    @Test
    public void testNewRegisteredUser_SuccessfulLogin() throws Exception {
        // Given:
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);
        String username = "newuser";
        String password = "password123";

        mockMvc.perform(post("/register")
                .param("username", username)
                .param("password", password)
                .param("confirmPassword", password)
                .param("email", "test@gmail.com")
                .param("g-recaptcha-response", "valid-recaptcha-response")
                .with(csrf()));

        // When:
        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password)
                        .param("g-recaptcha-response", "valid-recaptcha-response")
                        .with(csrf()))

                // Then:
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/list"));
    }

    @Test
    @WithMockUser // Blank annotation means it excludes the "ROLE_ADMIN" authority for this test
    public void testNewUserHasAccessToAuthorizedPage() throws Exception {
        //Given:
        Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);
        String username = "newuser";
        String password = "password123";

        mockMvc.perform(post("/register")
                .param("username", username)
                .param("password", password)
                .param("confirmPassword", password)
                .param("email", "test@gmail.com")
                .param("g-recaptcha-response", "valid-recaptcha-response")
                .with(csrf()));

        mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password)
                .param("g-recaptcha-response", "valid-recaptcha-response")
                .with(csrf()));

        // When: Accessing an authorized page (e.g., /list, list can be accessed by any authorized user)
        mockMvc.perform(MockMvcRequestBuilders.get("/list"))

                // Then:
                .andExpect(status().isOk())
                .andExpect(view().name("OnboardingList/onboardingList"));
    }

    @Test
    @WithMockUser // Blank annotation means it excludes the "ROLE_ADMIN" authority for this test
    public void testNewUserIsDeniedAccessToRestrictedPage() throws Exception {
        // Given:
        String username = "newuser";
        String password = "password123";

        mockMvc.perform(post("/register")
                .param("username", username)
                .param("password", password)
                .param("confirmPassword", password)
                .param("email", "test@gmail.com")
                .param("g-recaptcha-response", "valid-recaptcha-response")
                .with(csrf()));

        mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password)
                .param("g-recaptcha-response", "valid-recaptcha-response")
                .with(csrf()));

        // When: Attempt to access a restricted page
        // (e.g. "/new", can only be accessed by the admin as specified in the LoginSecurity class)
        mockMvc.perform(MockMvcRequestBuilders.get("/new"))

                // Then:
                .andExpect(status().isForbidden());
    }
}
