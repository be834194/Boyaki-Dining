package com.dining.boyaki.model.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import javax.imageio.ImageIO;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.amazonaws.SdkClientException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.ImageReadException;
import org.springframework.stereotype.Service;

import com.dining.boyaki.model.form.FileUploadForm;

@Service
public class FileUploadService {
	
	private final AmazonS3 s3Client;
	
	private final ExifRewriter exifRewriter;
	
	public FileUploadService(AmazonS3 s3Client,ExifRewriter exifRewriter) {
		this.s3Client = s3Client;
		this.exifRewriter = exifRewriter;
	}
	
	public boolean fileValid(FileUploadForm fileUploadForm) throws IOException{
	    try(ByteArrayInputStream bis = new ByteArrayInputStream(fileUploadForm.getMultipartFile().getBytes())){
			BufferedImage bi = ImageIO.read(bis);
			if (bi != null) {
				return true;
			} else {
				return false;
			}
	    }catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
		
	}
	
	public String fileUpload(FileUploadForm fileUploadForm,String s3PathName,String fileName) 
			       throws IOException,ImageWriteException,ImageReadException{
		DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		String extension = FilenameUtils.getExtension(fileUploadForm.getMultipartFile().getOriginalFilename()).toLowerCase();
		//画像の新規アップロードはUUID,画像更新は何もしない
		if(fileName == null) {
        	fileName = fileUploadForm.getCreateAt().format(fm) + " " + UUID.randomUUID() +"." + extension;
        }
		
        try (ByteArrayOutputStream uploadFileStream = new ByteArrayOutputStream()){
        	//JpegイメージからEXIFメタデータを削除して、結果をストリームに書き込む
        	byte[] bytes = fileUploadForm.getMultipartFile().getBytes();
        	exifRewriter.removeExifMetadata(bytes, uploadFileStream);
        	
        	//メタデータ設定してS3へアップロード
        	try(ByteArrayInputStream bis = new ByteArrayInputStream(uploadFileStream.toByteArray())){
        		ObjectMetadata metaData = new ObjectMetadata();
        		byte[] size = uploadFileStream.toByteArray();
        		metaData.setContentLength(size.length);
        		
        		//S3の格納先オブジェクト名,ファイル名,inputStream,メタデータ
            	s3Client.putObject(s3PathName, fileName, bis, metaData);
        	}	
        	return fileName;
        } catch (AmazonServiceException e) {
        	e.printStackTrace();
        	throw e;
        } catch (SdkClientException e){
        	e.printStackTrace();
        	throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } 
	}
	
	public String fileDownload(String bucketName,String objectName) {
		S3Object s3Object = s3Client.getObject(bucketName, objectName);
		StringBuffer data = new StringBuffer();
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()){
            byte[] content = IOUtils.toByteArray(inputStream);
            String base64Data = Base64.getEncoder().encodeToString(content);
            
            data.append(base64Data);
            return data.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } 
	}

}
