package com.girigiri.controller.utils;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * Created by JianGuo on 6/28/16.
 * Custom json for {@link ConstraintViolationException}, more details in {@link ErrorMessage}
 */
public class ViolationError {
    private List<ErrorMessage> errors;

    public ViolationError() {

    }
    public List<ErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorMessage> errors) {
        this.errors = errors;
    }





}
