package com.dining.boyaki.model.form;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
import com.dining.boyaki.model.form.validation.ConfirmFileUpload;

public class FileUploadForm {
	
	@ConfirmFileUpload
	private MultipartFile multipartFile;
	
	private LocalDateTime createAt;

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

}
