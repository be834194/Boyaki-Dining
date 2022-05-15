package com.dining.boyaki.model.form.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {ConfirmDiaryRecordValidator.class}) //バリデーションの実装クラス
@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE}) //注釈型が適用可能なプログラム要素
@Retention(RetentionPolicy.RUNTIME) //アノテーションの読み込みタイミング
public @interface ConfirmDiaryRecord {
	
	String message() default "いずれかの入力が必須です";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	String[] records();
	
	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
		ConfirmDiaryRecord[] values();
    }

}
