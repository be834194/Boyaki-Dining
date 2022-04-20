package com.dining.boyaki.model.service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.dining.boyaki.model.form.FileUploadForm;

@RunWith(SpringRunner.class)
@Transactional
public class FileUploadServiceTest {
	
	@Mock
	AmazonS3 s3Client;
	
	@InjectMocks
	FileUploadService fileUploadService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
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
		when(s3Client.putObject(any(), any(), any(File.class))).thenReturn(new PutObjectResult());
		
		String fileName =  fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads/");
		assertEquals("2022-04-20 21-04-45.jpg",fileName);
		verify(s3Client,times(1)).putObject(any(), any(), any(File.class));
	}
	
	@Test
	void fileUploadで例外が発生する() throws Exception{
		File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		FileUploadForm fileUploadForm = new FileUploadForm();
		fileUploadForm.setMultipartFile(file);
		fileUploadForm.setCreateAt(LocalDateTime.of(2022, 3, 4, 23, 55, 06));
		
		doThrow(new AmazonServiceException("S3との接続が拒否されました")).when(s3Client).putObject(any(), any(), any(File.class));		
		Throwable e = assertThrows(Exception.class, () -> {
			fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads/");
        });
		assertEquals(e.getClass(),AmazonServiceException.class);
		verify(s3Client,times(1)).putObject(any(), any(), any(File.class));
		
	}
	
	@Test
	void fileDownloadでファイルがダウンロードされる() throws Exception{
		FileInputStream stream = new FileInputStream("src/test/resources/image/3840_2160.jpg");
		S3Object s3Object = new S3Object();
		s3Object.setObjectContent(stream);
		when(s3Client.getObject(any(String.class), any(String.class))).thenReturn(s3Object);
		
		String result = fileUploadService.fileDownload("spring-infra-wp-study/wp-content/uploads/", "test.png");
		assertTrue(!result.isEmpty());
	}

}
