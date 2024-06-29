package com.designer.docker.TEST;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class FedExUploadFileResVO implements Serializable {

    private static final long serialVersionUID = -2808434166527512076L;
    @JsonProperty("output")
    private OutputResVO output;

    @JsonProperty("customerTransactionId")
    private String customerTransactionId;

    @NoArgsConstructor
    @Data
    public static class OutputResVO implements Serializable {

        private static final long serialVersionUID = 5278733894212432038L;
        @JsonProperty("meta")
        private MetaResVO meta;

        @NoArgsConstructor
        @Data
        public static class MetaResVO implements Serializable {
            private static final long serialVersionUID = 7256513236508366537L;
            @JsonProperty("documentType")
            private String documentType;
            @JsonProperty("docId")
            private String docId;
            @JsonProperty("folderId")
            private String folderId;
        }

    }

}


