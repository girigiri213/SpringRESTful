package com.girigiri.dao.constraints;

import com.girigiri.dao.validators.DateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by JianGuo on 6/26/16.
 * Custom {@link Constraint} Annotation to verify the string
 * whether it is in string format or not.
 */
@Documented
@Constraint(validatedBy = DateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringDateFormat {
    String message() default "String is not in date format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
