package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.ResetPassword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.TaskList.TaskEmailService;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ResetPasswordService {

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaskEmailService taskEmailService;

    // Map to store reset tokens and their timestamps
    private final ConcurrentMap<String, LocalDateTime> resetTokenMap = new ConcurrentHashMap<>();

    public boolean requestPasswordReset(String email) {
        if (resetPasswordRepository.checkIfEmailExists(email)) {
            String resetToken = generateUniqueResetToken();
            resetPasswordRepository.updateResetToken(email, resetToken);
            resetTokenMap.put(resetToken, LocalDateTime.now()); // Store timestamp
            sendPasswordResetEmail(email, resetToken);
            return true;
        }
        return false;
    }

    public boolean isResetTokenValid(String resetToken) {
        LocalDateTime timestamp = resetTokenMap.get(resetToken);
        if (timestamp != null) {
            LocalDateTime now = LocalDateTime.now();
            // Check if 5 minutes have passed since the token was generated
            if (now.minusMinutes(5).isBefore(timestamp)) {
                return true;
            } else {
                resetTokenMap.remove(resetToken); // Remove expired token from map
            }
        }
        return false;
    }

    public String getUsernameByResetToken(String resetToken) {
        return resetPasswordRepository.getUsernameByResetToken(resetToken);
    }

    public void updatePassword(String username, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword); // Use password encoder
        resetPasswordRepository.updatePasswordAndResetToken(username, encodedPassword);
    }

    private String generateUniqueResetToken() {
        return UUID.randomUUID().toString();
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        taskEmailService.sendPasswordResetEmail(email, resetToken);
    }
}
