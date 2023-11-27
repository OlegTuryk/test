package oleg.turyk.test.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.firstFieldName();
        secondFieldName = constraintAnnotation.secondFieldName();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final Object firstObj = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        final Object secondObj = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);
        return Objects.equals(firstObj, secondObj);
    }
}
