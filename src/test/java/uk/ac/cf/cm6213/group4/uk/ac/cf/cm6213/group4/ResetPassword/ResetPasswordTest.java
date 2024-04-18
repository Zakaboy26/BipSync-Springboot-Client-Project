    package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.ResetPassword;

    import org.hamcrest.Matchers;
    import org.junit.jupiter.api.Test;
    import org.mockito.Mockito;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
    import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
    import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Captcha.CAPTCHAVerificationService;

    import static org.mockito.ArgumentMatchers.anyString;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;
    import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


    @SpringBootTest
    @AutoConfigureMockMvc
    public class ResetPasswordTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ResetPasswordService resetPasswordService;

        @MockBean
        private CAPTCHAVerificationService captchaVerificationService;

        @Test
        public void testShowResetPasswordForm() throws Exception {
            //When:
            mockMvc.perform(MockMvcRequestBuilders.get("/reset-password"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
        }

        @Test
        public void testRequestPasswordReset_ValidEmail() throws Exception {
            //Given:
            Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);
            Mockito.when(resetPasswordService.requestPasswordReset("zaka_aap@hotmail.com")).thenReturn(true);
            //When:
            mockMvc.perform(MockMvcRequestBuilders.post("/reset-password")
                            .param("email", "zaka_aap@hotmail.com")
                            .param("g-recaptcha-response", "valid-recaptcha-response")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("message", Matchers.is("Password reset link sent to your email. Please check your inbox. The link will be valid for 5 minutes.")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password"));
        }

        @Test
        public void testRequestPasswordReset_ValidEmail_NoCaptchaVerification() throws Exception {
            //Given:
            Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(false);
            Mockito.when(resetPasswordService.requestPasswordReset("zaka_aap@hotmail.com")).thenReturn(false);
            //When:
            mockMvc.perform(MockMvcRequestBuilders.post("/reset-password")
                            .param("email", "zaka_aap@hotmail.com")
                            .param("g-recaptcha-response", "invalid-recaptcha-response")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("error", Matchers.is("CAPTCHA verification failed. Please try again.")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password"));
        }

        @Test
        public void testRequestPasswordReset_InvalidEmail() throws Exception {
            //Given:
            Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);
            Mockito.when(resetPasswordService.requestPasswordReset("zaka_aap@hotmail.com")).thenReturn(true);
            //When:
            mockMvc.perform(MockMvcRequestBuilders.post("/reset-password")
                            .param("email", "emailthatsnotvalid@gmail.com")
                            .param("g-recaptcha-response", "valid-recaptcha-response")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("message", Matchers.is("No user found with the provided email address.")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password"));
        }

        @Test
        public void testRequestPasswordReset_EmptyEmail() throws Exception {
            //Given:
            Mockito.when(captchaVerificationService.verifyRecaptcha(Mockito.anyString())).thenReturn(true);
            //When:
            mockMvc.perform(MockMvcRequestBuilders.post("/reset-password")
                            .param("email", "")
                            .param("g-recaptcha-response", "valid-recaptcha-response")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("error", Matchers.is("Please enter your email address")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password"));
        }

        @Test
        public void testShowResetPasswordConfirmForm_ValidToken() throws Exception {
            //Given:
            Mockito.when(resetPasswordService.isResetTokenValid(Mockito.anyString())).thenReturn(true);
            //When:
            mockMvc.perform(MockMvcRequestBuilders.get("/reset-password-confirm/valid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attributeExists("resetToken"))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password-confirm"));
        }

        @Test
        public void testShowResetPasswordConfirmForm_ButInvalidToken() throws Exception {
            //Given:
            Mockito.when(resetPasswordService.isResetTokenValid(Mockito.anyString())).thenReturn(false);
            //When:
            mockMvc.perform(MockMvcRequestBuilders.get("/reset-password-confirm/invalid-token"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    //Then:
                    .andExpect(MockMvcResultMatchers.model().attribute("message", Matchers.is("Invalid or expired token. Please request a new reset link.")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password"));
        }

        @Test
        public void testResetPassword_Successful() throws Exception {
            //Given:
            String validResetToken = "valid-token";
            String newPassword = "newPassword123";
            when(resetPasswordService.isResetTokenValid(validResetToken)).thenReturn(true);
            when(resetPasswordService.getUsernameByResetToken(validResetToken)).thenReturn("testUser");
            //When:
            mockMvc.perform(MockMvcRequestBuilders.post("/reset-password-confirm")
                            .param("resetToken", validResetToken)
                            .param("password", newPassword)
                            .param("confirmPassword", newPassword)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("message", Matchers.is("Password reset successfully. You can now login with your new password.")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password-confirm"));
            //Then:
            // Verify that the password update method was called with the correct parameters
            verify(resetPasswordService).updatePassword("testUser", newPassword);
        }

        @Test
        public void testResetPassword_ConfirmPasswordMismatch() throws Exception {
            //Given:
            String validResetToken = "valid-token";
            String newPassword = "newPassword123";
            String confirmPassword = "password456";
            when(resetPasswordService.isResetTokenValid(validResetToken)).thenReturn(true);
            when(resetPasswordService.getUsernameByResetToken(validResetToken)).thenReturn("testUser");
            //When:
            mockMvc.perform(MockMvcRequestBuilders.post("/reset-password-confirm")
                            .param("resetToken", validResetToken)
                            .param("password", newPassword)
                            .param("confirmPassword", confirmPassword)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("message", Matchers.is("Passwords do not match. Please try again.")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password-confirm"));
            //Then:
            // Verify that the password update method was not called
            verify(resetPasswordService, Mockito.never()).updatePassword(anyString(), anyString());
        }

        @Test
        public void testResetPassword_EmptyPasswordFields() throws Exception {
            //Given:
            String validResetToken = "valid-token";
            String newPassword = "";
            String confirmPassword = "";
            when(resetPasswordService.isResetTokenValid(validResetToken)).thenReturn(true);
            when(resetPasswordService.getUsernameByResetToken(validResetToken)).thenReturn("testUser");
            //When:
            mockMvc.perform(MockMvcRequestBuilders.post("/reset-password-confirm")
                            .param("resetToken", validResetToken)
                            .param("password", newPassword)
                            .param("confirmPassword", confirmPassword)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    //Then:
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("message", Matchers.is("Please enter a new password.")))
                    .andExpect(MockMvcResultMatchers.view().name("reset-password-confirm"));
            //Then:
            // Verify that the password update method was not called
            verify(resetPasswordService, Mockito.never()).updatePassword(anyString(), anyString());
        }

        @Test
        public void testShowResetPasswordForm_NoRecoveryEmail() throws Exception {
            // Given that: the user doesn't have a recovery email setup when registering
            //When:
            mockMvc.perform(MockMvcRequestBuilders.get("/reset-password"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("reset-password"))
                    .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"))
                    .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("message"))
                    //Then:
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("No recovery mail? <a href=\"mailto:haroonmubasher11@gmail.com\">Contact Support</a>")));
        }


    }
