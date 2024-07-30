package com.swiftfingers.makercheckersystem.demos.upload_configurations.classes;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Obiora on 30-Jul-2024 at 10:27
 */
@Data
@ToString
public class SocialAuthenticationSettings {
    private boolean googleAuthEnabled;
    private boolean facebookAuthEnabled;
    private String googleClientId;
    private String googleClientSecret;
    private String facebookClientId;
    private String facebookClientSecret;

}
