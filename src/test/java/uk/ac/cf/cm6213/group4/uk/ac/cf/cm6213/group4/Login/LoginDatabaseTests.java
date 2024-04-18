package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.UserDetails;
import uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.security.LoginSecurity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(LoginSecurity.class)
@ActiveProfiles("test")
public class LoginDatabaseTests {

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;


    @BeforeEach
    public void setup() {

    }

    @Test
    public void testUserCreation() {
        String username = "testUser";
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        userDetailsManager.createUser(
                org.springframework.security.core.userdetails.User.withUsername(username)
                        .password(encodedPassword)
                        .roles("USER")
                        .build()
        );

        assertTrue(userDetailsManager.userExists(username));
        assertEquals(encodedPassword, userDetailsManager.loadUserByUsername(username).getPassword());
    }

    @Test
    public void testUserAuthentiction() {
        String username = "adminuser";
        String rawPassword = "password";

        assertTrue(userDetailsManager.userExists(username));
        assertTrue(passwordEncoder.matches(rawPassword, userDetailsManager.loadUserByUsername(username).getPassword()));
    }

    @Test
    public void testInvalidUser() {
        String invalidUsername = "nonExistentUser";
        assertFalse(userDetailsManager.userExists(invalidUsername));
    }

    @Test
    public void testUserPasswordUpdate() throws Exception {
        String username = "adminuser";
        String newPassword = "newPassword";
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
            stmt.setString(1, encodedNewPassword);
            stmt.setString(2, username);
            int updatedRows = stmt.executeUpdate();
            assertTrue(updatedRows > 0, "Password update should affect at least one row.");
        }

        UserDetails updatedUser = userDetailsManager.loadUserByUsername(username);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()), "Password should be updated to the new value.");
    }





}
