package com.dining.boyaki.model.form.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {ConfirmPasswordValidator.class}) //バリデーションの実装クラス
@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE}) //注釈型が適用可能なプログラム要素
@Retention(RetentionPolicy.RUNTIME) //アノテーションの読み込みタイミング
public @interface ConfirmPassword {
	
	String message() default "パスワードが一致していません";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	String password();
	String confirmPassword();
	
	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
		ConfirmPassword[] values();
    }

}
