package com.girigiri.controller;

import com.girigiri.controller.utils.RestUtils;
import com.girigiri.controller.utils.ViolationError;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Created by JianGuo on 7/2/16.
 * Base Rest Controller
 */
public class BaseController {

    protected static final int DEFAULT_PAGE_SIZE = 10;
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ResponseEntity<?> processFieldErrors(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> set = exception.getConstraintViolations();
        Resource<ViolationError> resource = new Resource<>(RestUtils.generateError(set));
        return new ResponseEntity<>(resource, HttpStatus.BAD_REQUEST);
    }
}
