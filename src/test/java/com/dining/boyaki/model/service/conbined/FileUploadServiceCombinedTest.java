package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dining.boyaki.model.form.FileUploadForm;
import com.dining.boyaki.model.service.FileUploadService;

@SpringBootTest
@Transactional
public class FileUploadServiceCombinedTest {
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Test
	void fileUploadでファイルがアップロードされる() throws Exception{
		File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		FileUploadForm fileUploadForm = new FileUploadForm();
		fileUploadForm.setMultipartFile(file);
		fileUploadForm.setCreateAt(LocalDateTime.of(2022, 4, 20, 21, 04, 45));
		
		String fileName = fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads");
		assertEquals("2022-04-20 21-04-45.jpg",fileName);
	}
	
	@Test
	void fileDownloadでファイルがダウンロードされる() throws Exception{
		String result = fileUploadService.fileDownload("spring-infra-wp-study/wp-content/uploads", "sample.jpeg");
		assertTrue(!result.isEmpty());
	}

}
