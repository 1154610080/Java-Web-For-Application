package com.web.site;

import com.web.validation.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;

@Validated
public interface AuthenticationService {
    Principal authenticate(@NotBlank(message = "{validate.AuthenticationService.authenticate.username}") String username,
                           @NotBlank(message = "{validate.AuthenticationService.authenticate.password}") String password);
}
