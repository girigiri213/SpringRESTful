package com.girigiri.controller.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by JianGuo on 6/28/16.
 * Utils for my custom rest controller
 */
public class RestUtils {
    public static ViolationError generateError(Set<ConstraintViolation<?>> set) {
        ViolationError violationError = new ViolationError();
        Iterator<ConstraintViolation<?>> iterator = set.iterator();
        List<ErrorMessage> errorMessages = new ArrayList<>();
        ErrorMessage errorMessage = new ErrorMessage();
        while (iterator.hasNext()) {
            ConstraintViolation constraintViolation = iterator.next();
            errorMessage.setProperty(constraintViolation.getPropertyPath().toString());
            errorMessage.setEntity(constraintViolation.getRootBeanClass().getSimpleName());
            errorMessage.setInvalidValue(constraintViolation.getInvalidValue().toString());
            errorMessage.setMessage(constraintViolation.getMessage());
            errorMessages.add(errorMessage);
        }
        violationError.setErrors(errorMessages);
        return violationError;
    }



    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ComponentRequestNotFoundException extends RuntimeException {
        public ComponentRequestNotFoundException(Long id) {
            super("Could not found component request " + id);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidHistoryException extends RuntimeException {
        public InvalidHistoryException(long history) {
            super("Invalid with history id " + history);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(long customerId) {
            super("Could not find customer '" + customerId + "'.");
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidManagerException extends RuntimeException {
        public InvalidManagerException(long managerId) {
            super("Invalid manager id" + managerId);
        }
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class HistoryNotFoundException extends RuntimeException {
        public HistoryNotFoundException(long historyId) {
            super("Could not find history '" + historyId + "'.");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class RequestNotFoundException extends RuntimeException {
        public RequestNotFoundException(long customerId) {
            super("Could not find request '" + customerId + "'.");
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidCustomerException extends RuntimeException {
        public InvalidCustomerException(long customerId) {
            super("Invalid customerId " + customerId);
        }
    }

}
