package com.swiftfingers.makercheckersystem.utils.validations;

import com.swiftfingers.makercheckersystem.payload.response.EmailValidatorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailValidator {

    @Value("${zero.bounce.api-key}")
    private String apiKey;

    private static final String ZERO_BOUNCE_API_URL = "https://api.zerobounce.net/v2/validate";

    public EmailValidatorResponse validateEmail(String email) {
        String url = ZERO_BOUNCE_API_URL + "?api_key=" + apiKey + "&email=" + email;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(json);
                return parseResponse(jsonObject);
            }
        } catch (IOException e) {
            log.error("Error validating email ", e);
            return new EmailValidatorResponse(false, "Unable to validate email");
        }
    }

    private EmailValidatorResponse parseResponse(JSONObject response) {
        System.out.println("response " + response);
        String email = response.getString("address");
        String status = response.getString("status");
        String subStatus = response.optString("sub_status", "none");
        boolean isValid = "valid".equals(status);

        String message = switch (status) {
            case "valid" -> String.format("The email address %s is valid.", email);
            case "invalid" -> String.format("The email address %s is invalid.", email);
            case "catch-all" -> String.format("The email address %s is a catch-all address.", email);
            case "unknown" -> String.format("The email address %s could not be verified.", email);
            case "spamtrap" -> String.format("The email address %s is a spamtrap.", email);
            case "abuse" -> String.format("The email address %s is an abuse address.", email);
            case "do_not_mail" -> String.format("The email address %s should not be mailed.", email);
            default ->
                    String.format("The email address %s returned status %s with sub-status %s.", email, status, subStatus);
        };

        return new EmailValidatorResponse(isValid, message);
    }
}
