package com.girigiri.dao.validators;

import com.girigiri.dao.constraints.StringDateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by JianGuo on 6/26/16.
 * Custom validator for validate a String is in date format
 */
public class DateValidator implements ConstraintValidator<StringDateFormat, String> {
    @Override
    public void initialize(StringDateFormat constraintAnnotation) {
        // We don't need it here, 'cause the value is in the generic parameter
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (containsLetter(value)) {
            return false;
        }
        DateFormat dateFormat = new SimpleDateFormat(value);
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean containsLetter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if ((value.charAt(i) <= 'z' && value.charAt(i) >= 'a') || (value.charAt(i) <= 'Z' && value.charAt(i) >= 'A'))
                return true;
        }
        return false;
    }
}
