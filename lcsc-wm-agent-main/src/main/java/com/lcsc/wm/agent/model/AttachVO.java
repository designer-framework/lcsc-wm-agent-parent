package com.lcsc.wm.agent.model;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 03:15
 */
@Data
@Builder
public class AttachVO {
    private String mainClassName;
    private String agentJarPath;
    private String pid;
}
