package org.fadhel.jisrnihongoplatform.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record PhoneUpdateRequest(

        @NotBlank(message = "Phone cannot be empty")
        @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Phone must be in E.164 format, for example +966512345678")
        String phone
) {

}
