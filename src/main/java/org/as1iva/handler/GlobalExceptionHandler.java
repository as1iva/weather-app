package org.as1iva.handler;

import org.as1iva.exception.ApiRequestFailedException;
import org.as1iva.exception.DataNotFoundException;
import org.as1iva.exception.DuplicateLocationException;
import org.as1iva.exception.UserAuthenticationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String SIGN_IN = "sign-in";
    private static final String ERROR = "error";

    @ExceptionHandler(UserAuthenticationFailedException.class)
    public String handleUserAuthenticationFailedException(UserAuthenticationFailedException e, Model model) {

        model.addAttribute("error", e.getMessage());

        return SIGN_IN;
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDataNotFoundException(DataNotFoundException e, Model model) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());

        return ERROR;
    }

    @ExceptionHandler(ApiRequestFailedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleApiRequestFailedException(ApiRequestFailedException e, Model model) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.SERVICE_UNAVAILABLE.value());

        return ERROR;
    }

    @ExceptionHandler(DuplicateLocationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateLocationException(DuplicateLocationException e, Model model) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.CONFLICT.value());

        return ERROR;
    }
}
