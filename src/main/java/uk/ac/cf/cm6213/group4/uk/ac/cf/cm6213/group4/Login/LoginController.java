package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Login;

import com.structurizr.annotation.UsedByPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Captcha.CAPTCHAVerificationService;

@Controller
@UsedByPerson(name = "HR", description = "Login into their account", technology = "http(s)")
@UsedByPerson(name = "New Employee", description = "Login into their account", technology = "http(s)")
@UsedByPerson(name = "Department Member", description = "Login into their account", technology = "http(s)")
public class LoginController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoginController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private CAPTCHAVerificationService captchaVerificationService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("g-recaptcha-response") String recaptchaResponse,
            Model model) {

        boolean isRecaptchaValid = captchaVerificationService.verifyRecaptcha(recaptchaResponse);

        if (!isRecaptchaValid) {
            model.addAttribute("error", "CAPTCHA verification failed. Please try again.");
            return "login";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (auth.isAuthenticated()) {
            String username = auth.getName();
            String updateSql = "UPDATE users SET captcha_verified = 1 WHERE username = ?";
            jdbcTemplate.update(updateSql, username);
            return "redirect:list";
        } else {
            // Handle unsuccessful login
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }
}
