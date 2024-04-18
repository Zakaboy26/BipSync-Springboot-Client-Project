package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.ResetPassword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Captcha.CAPTCHAVerificationService;

@Controller
public class ResetPasswordController {

    @Autowired
    private ResetPasswordService resetPasswordService;


    @Autowired
    private CAPTCHAVerificationService captchaVerificationService;

    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String requestPasswordReset(@RequestParam("email") String email,
                                       @RequestParam("g-recaptcha-response") String recaptchaResponse,
                                       Model model) {
        // Verify reCAPTCHA response using the CAPTCHAVerificationService
        boolean isRecaptchaValid = captchaVerificationService.verifyRecaptcha(recaptchaResponse);

        if (!isRecaptchaValid) {
            model.addAttribute("error", "CAPTCHA verification failed. Please try again.");
            return "reset-password";
        }

        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Please enter your email address");
            return "reset-password";
        }

        if (resetPasswordService.requestPasswordReset(email)) {
            model.addAttribute("message", "Password reset link sent to your email. Please check your inbox. The link will be valid for 5 minutes.");
        } else {
            model.addAttribute("message", "No user found with the provided email address.");
        }
        return "reset-password";
    }


    @GetMapping("/reset-password-confirm/{resetToken}")
    public String showResetPasswordConfirmForm(@PathVariable String resetToken, Model model) {
        if (resetPasswordService.isResetTokenValid(resetToken)) {
            model.addAttribute("resetToken", resetToken);
            return "reset-password-confirm";
        } else {
            model.addAttribute("message", "Invalid or expired token. Please request a new reset link.");
            return "reset-password";
        }
    }

    @PostMapping("/reset-password-confirm")
    public String resetPassword(@RequestParam("resetToken") String resetToken,
                                @RequestParam("password") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        if (newPassword.isEmpty()) {
            model.addAttribute("message", "Please enter a new password.");
            model.addAttribute("resetToken", resetToken);
            return "reset-password-confirm";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match. Please try again.");
            model.addAttribute("resetToken", resetToken);
            return "reset-password-confirm";
        }

        String username = resetPasswordService.getUsernameByResetToken(resetToken);

        if (username != null) {
            resetPasswordService.updatePassword(username, newPassword);
            model.addAttribute("message", "Password reset successfully. You can now login with your new password.");
        } else {
            model.addAttribute("message", "Invalid or expired token. Please request a new reset link.");
        }

        return "reset-password-confirm";
    }

}
