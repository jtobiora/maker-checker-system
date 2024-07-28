package com.swiftfingers.makercheckersystem.controller.email_switch;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Obiora on 28-Jul-2024 at 11:29
 */
@Service
@RequiredArgsConstructor
public class EmailSettingsService {

    private final EmailSettingsRepository emailSettingsRepository;

    public void saveEmailSettings(EmailSettingsDto emailSettingsDto) {
        EmailSettings entity = new EmailSettings();
        entity.setHost(emailSettingsDto.getHost());
        entity.setPort(emailSettingsDto.getPort());
        entity.setUsername(emailSettingsDto.getUsername());
        entity.setPassword(emailSettingsDto.getPassword());
        entity.setProtocol(emailSettingsDto.getProtocol());
        entity.setAuth(emailSettingsDto.isAuth());
        entity.setFromAddress(emailSettingsDto.getFromAddress());
        entity.setStarttls(emailSettingsDto.isStarttls());
        entity.setSslTrust(emailSettingsDto.getSslTrust());

        emailSettingsRepository.save(entity);
    }

    public EmailSettingsDto getEmailSettings() {
        EmailSettings entity = emailSettingsRepository.findTopByOrderByIdDesc();
        EmailSettingsDto dto = new EmailSettingsDto();
        if (entity != null) {
            dto.setHost(entity.getHost());
            dto.setPort(entity.getPort());
            dto.setUsername(entity.getUsername());
            dto.setPassword(entity.getPassword());
            dto.setProtocol(entity.getProtocol());
            dto.setAuth(entity.isAuth());
            dto.setStarttls(entity.isStarttls());
            dto.setSslTrust(entity.getSslTrust());
            dto.setFromAddress(entity.getFromAddress());
        }
        return dto;
    }
}
