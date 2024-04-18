package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Register;// RegisterRepository.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RegisterRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RegisterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean checkIfEmailExists(String email) {
        String checkEmailQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkEmailQuery, Integer.class, email);
        return userCount != null && userCount > 0;
    }


    public void insertUserEmail(String username, String email) {
        String updateEmailQuery = "UPDATE users SET email = ? WHERE username = ?";
        jdbcTemplate.update(updateEmailQuery, email, username);
    }

    public void insertCaptchaVerification(String username, boolean captchaVerified) {
        String updateCaptchaSql = "UPDATE users SET captcha_verified = ? WHERE username = ?";
        jdbcTemplate.update(updateCaptchaSql, captchaVerified, username);
    }
}
