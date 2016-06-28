package com.girigiri.controller.utils;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * Created by JianGuo on 6/28/16.
 * Custom json for {@link ConstraintViolationException}
 */
public class ViolationError {
    private int errorSize;
    private List<ErrorMessage> errors;

    public ViolationError() {

    }

    public int getErrorSize() {
        return errorSize;
    }

    public void setErrorSize(int errorSize) {
        this.errorSize = errorSize;
    }

    public List<ErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorMessage> errors) {
        this.errors = errors;
    }





}
