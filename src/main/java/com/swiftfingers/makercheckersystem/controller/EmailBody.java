package com.swiftfingers.makercheckersystem.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailBody {
    private String subject;
    private List<String> receiversList;
    private List<String> cc;
    private List<String> bcc;
    private String msg;
    private String htmlMessage;
    private String from;
    private String organizationId;
    private String partnerName;
}
