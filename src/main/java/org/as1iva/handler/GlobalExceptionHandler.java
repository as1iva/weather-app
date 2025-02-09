package org.as1iva.handler;

import org.as1iva.exception.api.ClientApiException;
import org.as1iva.exception.api.JsonParsingApiException;
import org.as1iva.exception.DataNotFoundException;
import org.as1iva.exception.DuplicateLocationException;
import org.as1iva.exception.UserAuthenticationFailedException;
import org.as1iva.exception.ExpiredSessionException;
import org.as1iva.exception.api.ServerApiException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String REDIRECT_TO_LOGIN = "redirect:/login";

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

    @ExceptionHandler(JsonParsingApiException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleJsonParsingApiException(JsonParsingApiException e, Model model) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.SERVICE_UNAVAILABLE.value());

        return ERROR;
    }

    @ExceptionHandler(ClientApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleClientApiException(ClientApiException e, Model model) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());

        return ERROR;
    }

    @ExceptionHandler(ServerApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerApiException(ServerApiException e, Model model) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ERROR;
    }

    @ExceptionHandler(DuplicateLocationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateLocationException(DuplicateLocationException e, Model model) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.CONFLICT.value());

        return ERROR;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(Model model) {

        model.addAttribute("error", "Page not found");
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());

        return ERROR;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleHttpRequestMethodNotSupportedException(Model model) {

        model.addAttribute("error", "The method is not supported");
        model.addAttribute("statusCode", HttpStatus.METHOD_NOT_ALLOWED.value());

        return ERROR;
    }

    @ExceptionHandler(ExpiredSessionException.class)
    public String handleExpiredSessionException() {
        return REDIRECT_TO_LOGIN;
    }
}
