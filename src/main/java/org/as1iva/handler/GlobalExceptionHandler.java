package org.as1iva.handler;

import org.as1iva.exception.UserAuthenticationFailedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String SIGN_IN = "sign-in";

    @ExceptionHandler(UserAuthenticationFailedException.class)
    public String handle(UserAuthenticationFailedException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return SIGN_IN;
    }
}
