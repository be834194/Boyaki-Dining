package com.dining.boyaki.model.form.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class ConfirmFileUploadValidator implements ConstraintValidator<ConfirmFileUpload, MultipartFile> {

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
		if(Objects.isNull(multipartFile) ||multipartFile.isEmpty()) {
			return true;
		}
		
		//ファイルサイズの確認、10MB以上ならエラー
		if(10485760 <= multipartFile.getSize()) {
			return false;
		}
		
		//メディアタイプ、拡張子の確認
		MediaType mediaType = MediaType.parseMediaType(multipartFile.getContentType());
		String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
		
		List<MediaType> mediaTypeList = Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG , MediaType.MULTIPART_FORM_DATA);
	    List<String> extList = Arrays.asList("jpg", "jpeg", "png");
	    
	    return  mediaTypeList.stream().anyMatch((mType) -> mediaType.includes(mType))
	                        && extList.stream().anyMatch((v) -> extension.toLowerCase().equals(v));
	}

}
