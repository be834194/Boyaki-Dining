package com.dining.boyaki.model.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
public class FileUploadFormTest {
	
	@Autowired
	Validator validator;
	
	FileUploadForm form = new FileUploadForm();
                                                //ターゲット,ターゲットオブジェクトの名前
    BindingResult bindingResult = new BindException(form,"FileUploadForm");
    
    @Test
    void バリデーション問題なし() throws Exception{
    	//ファイルが選択されていない場合
    	validator.validate(form, bindingResult);
    	assertEquals(0,bindingResult.getFieldErrorCount());
    	
    	//形式が問題ない場合
    	File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
    	form.setMultipartFile(file);
    	
    	validator.validate(form, bindingResult);
    	assertEquals(0,bindingResult.getFieldErrorCount());
    }
    
    @Test
    void メディアタイプやでバリデーションエラー発生() throws Exception{
    	File upFile = new File("src/test/resources/image/testApp.js");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","testApp.js","text/javascript",bytes);
    	form.setMultipartFile(file);
    	validator.validate(form, bindingResult);
    	assertEquals(1,bindingResult.getFieldErrorCount());
    	assertTrue(bindingResult.getFieldError("multipartFile")
    			                .toString().contains("ファイル拡張子がjpg,jpegであることを確認してください"));
    	
    	file = new MockMultipartFile("file","testApp.png","multipart/form-data",bytes);
    	form.setMultipartFile(file);
    	validator.validate(form, bindingResult);
    	assertEquals(2,bindingResult.getFieldErrorCount());
    	assertTrue(bindingResult.getFieldError("multipartFile")
    			                .toString().contains("ファイル拡張子がjpg,jpegであることを確認してください"));
    }

}
