package com.designer.docker.TEST;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ftdips-FedExDocRemoteFeign", url = "https://documentapi.prod.fedex.com")
public interface FedExDocRemoteFeign {

    /**
     * 电子运单无纸化[交易文档上传]
     */
    @PostMapping(value = {"/documents/v1/etds/upload"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FedExUploadFileResVO uploadFedExFile(FedExUploadFileApiReqVO document, @RequestHeader("Authorization") String tokenValue);

    /**
     * 电子运单无纸化[交易文档上传]
     */
    @PostMapping(value = {"/documents/v1/etds/upload"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFedExFile_(FedExUploadFileApiReqVO document, @RequestHeader("Authorization") String tokenValue);

}


