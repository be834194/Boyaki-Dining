package com.dining.boyaki.model.service;

import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.ImageReadException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.stereotype.Service;

import com.dining.boyaki.model.form.FileUploadForm;

@Service
public class FileUploadService {
	
	private final AmazonS3 s3Client;
	
	public FileUploadService(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}
	
	public String fileUpload(FileUploadForm fileUploadForm,String s3PathName) 
			       throws IOException,ImageWriteException,ImageReadException{
		DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		String extension = FilenameUtils.getExtension(fileUploadForm.getMultipartFile().getOriginalFilename());
        String fileName = fileUploadForm.getCreateAt().format(fm) +"." + extension;
        
        File uploadFile = new File(fileName);
    
        //try-with-resources
        try (FileOutputStream uploadFileStream = new FileOutputStream(uploadFile)){
        	byte[] bytes = fileUploadForm.getMultipartFile().getBytes();
        	//JpegイメージからEXIFメタデータを削除して、結果をストリームに書き込む
        	new ExifRewriter().removeExifMetadata(bytes, uploadFileStream);
        	
        	//S3の格納先オブジェクト名,ファイル名,ファイル
        	s3Client.putObject(s3PathName, fileName, uploadFile);
        	return fileName;
        } catch (AmazonServiceException e) {
        	e.printStackTrace();
        	throw e;
        } catch (ImageWriteException e) {
            e.printStackTrace();
            throw e;
        } catch (ImageReadException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        
	}
	
	public String fileDownload(String bucketName,String objectName) {
		S3Object s3Object = s3Client.getObject(bucketName, objectName);
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()){
            byte[] content = IOUtils.toByteArray(inputStream);
            String base64Data = Base64.getEncoder().encodeToString(content);
            return base64Data;
        } catch (IOException e) {
            e.printStackTrace();
            return "error!";
        }
	}

}
