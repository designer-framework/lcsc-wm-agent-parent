package com.designer.docker.TEST;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.InputStream;
import java.io.OutputStream;


@Slf4j
@RestController
@Api(tags = "用户管理01")
@RequestMapping("/agent/api/fedex")
public class FedexController {

    @Autowired
    private FedExDocRemoteFeign fedExDocRemoteFeign;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @PostMapping(value = "/agent/api/fedex", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void req(@RequestHeader("Authorization") String token, FedExUploadFileApiReqVO fedExUploadFileApiReqVO) {
        String uploadFedExFile_ = fedExDocRemoteFeign.uploadFedExFile_(fedExUploadFileApiReqVO, token);

        FedExUploadFileResVO fedExUploadFileResVO = objectMapper.readValue(uploadFedExFile_, FedExUploadFileResVO.class);
    }

    @SneakyThrows
    @PostMapping("/agent/api/fedex2")
    void req2(@RequestHeader("Authorization") String token, FedExUploadFileApiReqVO fedExUploadFileApiReqVO) {

        FileItem fileItem = new DiskFileItemFactory().createItem("attachment", MediaType.APPLICATION_PDF_VALUE, true, "attFileName.pdf");


        Resource resource = new ClassPathResource("/yqj_SMT.pdf");
        try (
                InputStream inputStream = resource.getInputStream();
                OutputStream outputStream = fileItem.getOutputStream()
        ) {
            IOUtils.copy(inputStream, outputStream);

            fedExUploadFileApiReqVO.setAttachment(new CommonsMultipartFile(fileItem));
            fedExUploadFileApiReqVO.setDocument("{\"workflowName\":\"ETDPreshipment\",\"carrierCode\":\"FDXE\",\"name\":\"yqj_SMT.pdf\",\"contentType\":\"application/pdf\",\"meta\":{\"shipDocumentType\":\"COMMERCIAL_INVOICE\",\"originCountryCode\":\"CN\",\"destinationCountryCode\":\"TH\"}}");
            fedExDocRemoteFeign.uploadFedExFile(fedExUploadFileApiReqVO, token);
        }
    }
}
