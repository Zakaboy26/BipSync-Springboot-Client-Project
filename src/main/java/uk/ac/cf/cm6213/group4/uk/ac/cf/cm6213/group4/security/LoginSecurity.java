package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.security;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class LoginSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/css/**", "/images/**", "/login", "/reset-password", "/reset-password-confirm", "/reset-password-confirm/**").permitAll()
                        // List page
                        .requestMatchers("/new/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/Onboardinglist/edit/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/OnboardingList/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/register").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/upload").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/list/**").authenticated()  // Allow all authenticated users
                        .requestMatchers("/list/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_IT", "ROLE_HR")

                        // Task list page
                        .requestMatchers("/task-list/edit/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/task-list/movetodo/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_IT", "ROLE_HR")
                        .requestMatchers("/task-list/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_IT", "ROLE_HR")
                        .requestMatchers("/on-boarding/*/add-task").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/tasks-library/**").hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/list", true)
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcUserDetailsManager userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        return jdbcUserDetailsManager;
    }
}
