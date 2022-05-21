package com.dining.boyaki.model.form.validation;

import java.util.Arrays;
import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class ConfirmDiaryRecordValidator implements ConstraintValidator<ConfirmDiaryRecord, Object> {
	
	private String[] records;
	private String message;
    
    @Override
    public void initialize(ConfirmDiaryRecord annotation) {
    	this.records = annotation.records();
    	this.message = annotation.message();
    }
    
    @Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		Object[] objects = new Object[3];
		for(int i = 0; i < 3; i++) {
			objects[i] = beanWrapper.getPropertyValue(this.records[i]);
		}
		
		boolean matched = Arrays.stream(objects).allMatch(Objects::isNull);
		if (!matched) { //全てnullでない場合
			return true;
        }
		context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addPropertyNode(records[0]).addConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addPropertyNode(records[1]).addConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addPropertyNode(records[2]).addConstraintViolation();
        return false;
    }

}
