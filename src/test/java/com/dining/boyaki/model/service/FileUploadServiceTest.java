package com.dining.boyaki.model.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.ImageReadException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.dining.boyaki.model.form.FileUploadForm;

@RunWith(SpringRunner.class)
@Transactional
public class FileUploadServiceTest {
	
	private static MockedStatic<UUID> uuid;
	
	@Mock
	AmazonS3 s3Client;
	
	@Mock
	ExifRewriter exifRewriter;
	
	@InjectMocks
	FileUploadService fileUploadService;
	
	File upFile;
	FileUploadForm fileUploadForm = new FileUploadForm();
	
	@BeforeEach
	void setUp() throws Exception{
		upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		fileUploadForm.setMultipartFile(file);
		fileUploadForm.setCreateAt(LocalDateTime.of(2022, 4, 20, 21, 04, 45));
		
		MockitoAnnotations.openMocks(this);
		uuid = Mockito.mockStatic(UUID.class,Mockito.CALLS_REAL_METHODS);
	}
	
	@AfterEach //mockStaticのモック化の解除
    void tearDown() throws Exception {
		uuid.close();
	}
	
	@Test
	void fileValidでtrueが返ってくる() throws Exception{
		File upFile = new File("src/test/resources/image/3840_2160.jpg");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","3840_2160.jpg","multipart/form-data",bytes);
		fileUploadForm.setMultipartFile(file);
		
		boolean result = fileUploadService.fileValid(fileUploadForm);
		assertEquals(true,result);
	}
	
	@Test
	void fileValidでfalseが返ってくる() throws Exception{
		File upFile = new File("src/test/resources/image/testApp.js");
		Path path = Paths.get(upFile.getCanonicalPath());
		byte[] bytes = Files.readAllBytes(path);
		MultipartFile file = new MockMultipartFile("file","testApp.js","multipart/form-data",bytes);
		fileUploadForm.setMultipartFile(file);
		
		boolean result = fileUploadService.fileValid(fileUploadForm);
		assertEquals(false,result);
	}
	
	@Test
	void fileUploadでファイルがアップロードされる() throws Exception{
		UUID uuidName = UUID.fromString("f3241f8f-006e-4429-8438-f42adb1d1869");
		uuid.when(UUID::randomUUID).thenReturn(uuidName);
		when(s3Client.putObject(any(), any(), any(File.class))).thenReturn(new PutObjectResult());
		doNothing().when(exifRewriter).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));
		
		String fileName =  fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads/");
		assertEquals("f3241f8f-006e-4429-8438-f42adb1d1869 2022-04-20 21-04-45.jpg",fileName);
		verify(exifRewriter,times(1)).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));
		verify(s3Client,times(1)).putObject(any(), any(), any(File.class));
	}
	
	@Test
	void fileUploadでAmazonServiceExceptionが発生する() throws Exception{
		doThrow(new AmazonServiceException("S3との接続が拒否されました")).when(s3Client).putObject(any(), any(), any(File.class));		
		doNothing().when(exifRewriter).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));
		
		            //発生するであろう例外のクラス、ラムダ式でテスト対象の処理
		Throwable e = assertThrows(Exception.class,
				() -> {fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads/");});
		assertEquals(AmazonServiceException.class,e.getClass());
		verify(exifRewriter,times(1)).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));
		verify(s3Client,times(1)).putObject(any(), any(), any(File.class));
		
	}
	
	@Test
	void fileUploadでImageWriteExceptionが発生する() throws Exception{
		when(s3Client.putObject(any(), any(), any(File.class))).thenReturn(new PutObjectResult());
		doThrow(new ImageWriteException("読み込み失敗")).when(exifRewriter).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));	
		
		Throwable e = assertThrows(Exception.class,
				() -> {fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads/");});
		assertEquals(ImageWriteException.class,e.getClass());
		verify(exifRewriter,times(1)).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));
		verify(s3Client,times(0)).putObject(any(), any(), any(File.class));
	}
	
	@Test
	void fileUploadでImageReadExceptionが発生する() throws Exception{
		when(s3Client.putObject(any(), any(), any(File.class))).thenReturn(new PutObjectResult());
		doThrow(new ImageReadException("読み込み失敗")).when(exifRewriter).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));	
		
		Throwable e = assertThrows(Exception.class,
				() -> {fileUploadService.fileUpload(fileUploadForm, "spring-infra-wp-study/wp-content/uploads/");});
		assertEquals(ImageReadException.class,e.getClass());
		verify(exifRewriter,times(1)).removeExifMetadata(any(byte[].class), any(FileOutputStream.class));
		verify(s3Client,times(0)).putObject(any(), any(), any(File.class));
	}

}
