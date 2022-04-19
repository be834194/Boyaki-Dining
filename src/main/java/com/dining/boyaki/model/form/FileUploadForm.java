package com.dining.boyaki.model.form;

import org.springframework.web.multipart.MultipartFile;
import com.dining.boyaki.model.form.validation.ConfirmFileUpload;

public class FileUploadForm {
	
	@ConfirmFileUpload
	private MultipartFile multipartFile;

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

}
