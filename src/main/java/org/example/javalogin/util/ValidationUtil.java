package org.example.javalogin.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidationUtil {

    private static final ValidatorFactory factory =
            Validation.buildDefaultValidatorFactory();

    private static final Validator validator =
            factory.getValidator();

    private ValidationUtil() {
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }
}
