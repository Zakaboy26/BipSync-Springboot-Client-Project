package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Captcha;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CaptchaVerificationRepository extends User {
    private boolean captchaVerified;

    public CaptchaVerificationRepository(String username, String password, boolean enabled, boolean accountNonExpired,
                                         boolean credentialsNonExpired, boolean accountNonLocked,
                                         Collection<? extends GrantedAuthority> authorities, boolean captchaVerified) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.captchaVerified = captchaVerified;
    }

    public void setCaptchaVerified(boolean captchaVerified) {
        this.captchaVerified = captchaVerified;
    }
}
