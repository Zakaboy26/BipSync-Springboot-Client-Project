package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Captcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CAPTCHAVerificationService {

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    public boolean verifyRecaptcha(String recaptchaResponse) {
        // Verify reCAPTCHA with Google reCaptcha V2
        String url = "https://www.google.com/recaptcha/api/siteverify";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecretKey);
        params.add("response", recaptchaResponse);

        ResponseEntity<Map> response = new RestTemplate().postForEntity(url, params, Map.class);

        if (response != null && response.getBody() != null) {
            Map responseBody = response.getBody();
            Boolean success = (Boolean) responseBody.get("success");
            return success != null && success;
        }

        return false;
    }
}