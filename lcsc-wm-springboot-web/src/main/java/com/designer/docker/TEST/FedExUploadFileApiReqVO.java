package com.designer.docker.TEST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FedExUploadFileApiReqVO implements Serializable {

    private static final long serialVersionUID = 2133292190562933217L;

    private String document;

    private MultipartFile attachment;

}



