package com.system.exception;

import com.system.controller.ResourceDoesNotExistException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mpereyma on 10/19/15.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    void handleIllegalArgumentException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(IllegalStateException.class)
    void handleIllegalStateException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    public void handleResourceDoesNotExistException(ResourceDoesNotExistException ex,
                                                    HttpServletRequest request, HttpServletResponse response)
        throws IOException {response.sendError(HttpStatus.NOT_FOUND.value(),
                                               "The resource '" + request.getRequestURI() + "' does not exist");
    }

}