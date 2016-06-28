package com.girigiri.controller.utils;

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
}
