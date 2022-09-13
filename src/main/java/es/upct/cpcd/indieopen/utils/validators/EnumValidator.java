package es.upct.cpcd.indieopen.utils.validators;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.ReflectionUtils;

public class EnumValidator implements ConstraintValidator<ValidateEnum, String> {
    Class<? extends Enum<?>> enumClass;
    String enumMethod;

    @Override
    public void initialize(ValidateEnum annotation) {
        this.enumClass = annotation.value();
        this.enumMethod = annotation.enumMethod();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Enum<?>[] enums = enumClass.getEnumConstants();
        Method method = ReflectionUtils.findMethod(enumClass, enumMethod);

        return Objects.nonNull(enums)
                && Arrays.stream(enums).map(en -> {
            assert method != null;
            return ReflectionUtils.invokeMethod(method, en);
        }).anyMatch(en -> Objects.equals(value, en));
    }
}