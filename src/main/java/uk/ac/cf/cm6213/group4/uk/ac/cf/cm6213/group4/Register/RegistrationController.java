package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Captcha.CAPTCHAVerificationService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private CAPTCHAVerificationService captchaVerificationService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationForm registrationForm, Model model,
                               @RequestParam(name = "g-recaptcha-response") String recaptchaResponse) {
        // Verify the CAPTCHA
        if (!captchaVerificationService.verifyRecaptcha(recaptchaResponse)) {
            model.addAttribute("error", "CAPTCHA verification failed. Please try again.");
            return "register";
        }

        String username = registrationForm.getUsername();
        String password = registrationForm.getPassword();
        String confirmPassword = registrationForm.getConfirmPassword();
        String email = registrationForm.getEmail();

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            model.addAttribute("error", "All essential fields must be filled out");
            return "register";
        }

        // Password validation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        // Check if the email already exists using the instance of RegisterRepository
        if (email != null && !email.isEmpty() && registerRepository.checkIfEmailExists(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        // Username existence check
        if (userDetailsManager.userExists(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        // Encode the password before storing it in the database
        String encodedPassword = passwordEncoder.encode(password);

        // Create a new user and assign the "ROLE_IT" role
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_IT"));

        userDetailsManager.createUser(new org.springframework.security.core.userdetails.User(
                username,
                encodedPassword,
                authorities));

        //Insert the captcha verification status
        registerRepository.insertCaptchaVerification(username, true);

        // If the email is provided and not empty, insert it for the user
        if (email != null && !email.isEmpty()) {
            registerRepository.insertUserEmail(username, email);
        }

        // Redirect to the login page
        return "redirect:login";
    }
}
