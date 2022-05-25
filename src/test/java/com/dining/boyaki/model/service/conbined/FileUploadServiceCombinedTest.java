package com.dining.boyaki.model.service.conbined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
	
	private static MockedStatic<UUID> uuid;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@BeforeEach
	void setUp() {
		uuid = Mockito.mockStatic(UUID.class,Mockito.CALLS_REAL_METHODS);
		UUID uuidName = UUID.fromString("f3241f8f-006e-4429-8438-f42adb1d1869");
		uuid.when(UUID::randomUUID).thenReturn(uuidName);
	}
	
	@AfterEach
    void tearDown() throws Exception {
		uuid.close();
	}
	
	@Test
	void fileUploadでファイルがアップロードされる() throws Exception{
		File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		FileUploadForm fileUploadForm = new FileUploadForm();
		fileUploadForm.setMultipartFile(file);
		fileUploadForm.setCreateAt(LocalDateTime.of(2022, 4, 20, 21, 04, 45));
		
		String fileName = fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads",null);
		assertEquals("2022-04-20 21-04-45 f3241f8f-006e-4429-8438-f42adb1d1869.jpg",fileName);
		
		fileName = fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads","hogehoge.jpg");
		assertEquals("hogehoge.jpg",fileName);
	}
	
	@Test
	void fileDownloadでファイルがダウンロードされる() throws Exception{
		String result = fileUploadService.fileDownload("spring-infra-wp-study/wp-content/uploads", "hogehoge.jpg");
		assertTrue(!result.isEmpty());
	}

}
