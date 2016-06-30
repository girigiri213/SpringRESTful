package com.girigiri.dao.constraints;

import com.girigiri.dao.validators.CustomerIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by JianGuo on 6/30/16.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface ValidCustomerId {
    String message() default "Invalid customer Id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
