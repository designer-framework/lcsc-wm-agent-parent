package com.designer.docker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class ServiceOne {

    @Autowired
    private ServiceTwo serviceTwo;

}
