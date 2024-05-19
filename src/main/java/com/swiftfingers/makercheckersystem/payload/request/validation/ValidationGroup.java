package com.swiftfingers.makercheckersystem.payload.request.validation;

import lombok.Data;

/*
* Provides a means of using Validation Groups for Create and Update Scenarios using a common DTO
*
* */
@Data
public class ValidationGroup {
    //Validation Groups for All Entities
    public interface CreateEntity {}
    public interface UpdateEntity {}
}
