package com.jtspringproject.JtSpringProject.controller;

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for all Spring MVC controllers.
 *
 * <p>Maps application exceptions to appropriate HTTP status codes and safe JSP
 * error views. No exception detail (message, class name, stack trace) is ever
 * passed to a view model — all rendered error pages are static and safe per
 * CWE-209.</p>
 *
 * <p>Spring Security exceptions (AccessDeniedException, AuthenticationException,
 * UsernameNotFoundException) are intentionally NOT handled here; the Security
 * filter chain retains ownership of 401/403 responses.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String VIEW_ERROR = "error";
    private static final String VIEW_404 = "404";

    /**
     * Handles not-found exceptions, returning HTTP 404 and the 404.jsp view.
     *
     * @param ex      the NoSuchElementException that was thrown
     * @param request the current HTTP request (used to obtain the URI for logging)
     * @return a ModelAndView with an empty model pointing to the 404 view
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
        logger.warn("Resource not found at URI [{}]", request.getRequestURI(), ex);
        ModelAndView mv = new ModelAndView(VIEW_404);
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }

    /**
     * Handles unmapped URL requests when DispatcherServlet throws NoHandlerFoundException.
     *
     * <p>Requires {@code spring.mvc.throw-exception-if-no-handler-found=true} and
     * {@code spring.web.resources.add-mappings=false} in application.properties.</p>
     *
     * @param ex      the NoHandlerFoundException thrown by DispatcherServlet
     * @param request the current HTTP request (used to obtain the method and URI for logging)
     * @return a ModelAndView with an empty model pointing to the 404 view
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        logger.warn("No handler found for [{}] [{}]", request.getMethod(), request.getRequestURI(), ex);
        ModelAndView mv = new ModelAndView(VIEW_404);
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }

    /**
     * Handles validation and binding exceptions, returning HTTP 400 and the error view
     * with field-level errors preserved in the model.
     *
     * <p>Per dec-validation-field-errors, FieldError messages are developer-defined
     * user-facing text and are NOT information leakage.</p>
     *
     * @param ex      the validation exception (BindException or MethodArgumentNotValidException)
     * @param request the current HTTP request (used to obtain the URI for logging)
     * @return a ModelAndView with BindingResult in model under key "errors"
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleValidation(Exception ex, HttpServletRequest request) {
        logger.warn("Validation failed: URI=[{}]", request.getRequestURI());

        BindingResult bindingResult;
        if (ex instanceof BindException) {
            bindingResult = ((BindException) ex).getBindingResult();
        } else {
            bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
        }

        ModelAndView mv = new ModelAndView(VIEW_ERROR);
        mv.setStatus(HttpStatus.BAD_REQUEST);
        mv.addObject("errors", bindingResult);
        return mv;
    }

    /**
     * Catch-all handler for any unhandled exception, returning HTTP 500 and the generic error view.
     *
     * <p>This handler deliberately does NOT pass exception details to the model to prevent
     * information leakage to the client.</p>
     *
     * @param ex      the exception that was thrown
     * @param request the current HTTP request (used to obtain the URI for logging)
     * @return a ModelAndView with an empty model pointing to the error view
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAll(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception at URI [{}]", request.getRequestURI(), ex);
        ModelAndView mv = new ModelAndView(VIEW_ERROR);
        mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mv;
    }
}
