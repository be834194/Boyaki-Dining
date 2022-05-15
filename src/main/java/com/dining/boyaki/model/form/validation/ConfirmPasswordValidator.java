package com.dining.boyaki.model.form.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;

public class ConfirmPasswordValidator implements ConstraintValidator<ConfirmPassword, Object> {
	
	private String password;
    private String confirmPassword;
    private String message;
    
    @Override
    public void initialize(ConfirmPassword annotation) {
    	this.password = annotation.password();
    	this.confirmPassword = annotation.confirmPassword();
    	this.message = annotation.message();
    }
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object passwordValue = beanWrapper.getPropertyValue(this.password);
        Object confirmPasswordValue = beanWrapper.getPropertyValue(this.confirmPassword);

        boolean matched = ObjectUtils.nullSafeEquals(passwordValue, confirmPasswordValue);

        if (matched) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addPropertyNode(confirmPassword).addConstraintViolation();
        return false;
	}

}
