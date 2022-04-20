package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
	
	private static final LocalDateTime datetime = LocalDateTime.of(2022, 4, 20, 21, 04, 45);
	
	private static MockedStatic<LocalDateTime> mock;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		//CALLS_REAL_METHODSで、部分的なMock化を可能にできる
		mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
		mock.when(LocalDateTime::now).thenReturn(datetime);
	}
	
	@AfterEach //mockStaticのモック化の解除
    void tearDown() throws Exception {
        mock.close();
	}
	
	@Test
	void fileUploadでファイルがアップロードされる() throws Exception{
		File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		FileUploadForm fileUploadForm = new FileUploadForm();
		fileUploadForm.setMultipartFile(file);
		fileUploadForm.setCreateAt(LocalDateTime.now());
		
		String fileName = fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads");
		assertEquals("2022-04-20 21-04-45.jpg",fileName);
	}
	
	@Test
	void fileDownloadでファイルがダウンロードされる() throws Exception{
		String result = fileUploadService.fileDownload("spring-infra-wp-study/wp-content/uploads", "sample.jpeg");
		assertTrue(!result.isEmpty());
	}

}
