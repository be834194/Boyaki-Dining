package com.dining.boyaki.model.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import javax.imageio.ImageIO;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
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
		File uploadFile = new File(fileUploadForm.getMultipartFile().getOriginalFilename());

	    try(FileOutputStream fos = new FileOutputStream(uploadFile)){
	    	fos.write(fileUploadForm.getMultipartFile().getBytes());
			BufferedImage bi = ImageIO.read(uploadFile);
			if (bi != null) {
				uploadFile.delete();
				return true;
			} else {
				uploadFile.delete();
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
        	fileName = UUID.randomUUID() + " " + fileUploadForm.getCreateAt().format(fm) +"." + extension;
        }
		
        File uploadFile = new File(fileName);
    
        //try-with-resources
        try (FileOutputStream uploadFileStream = new FileOutputStream(uploadFile)){
        	byte[] bytes = fileUploadForm.getMultipartFile().getBytes();
        	//JpegイメージからEXIFメタデータを削除して、結果をストリームに書き込む
        	exifRewriter.removeExifMetadata(bytes, uploadFileStream);
        	
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
        } finally {
        	uploadFile.delete();
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
