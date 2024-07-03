package com.designer.turbo.web;

import com.designer.turbo.service.impl.Service1Impl;
import com.designer.turbo.vo.Test1VO;
import com.designer.turbo.vo.Test2VO;
import com.designer.turbo.vo.Test3VO;
import com.designer.turbo.vo.Test4VO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: Designer
 * @date : 2023-10-18 23:40
 */
@Slf4j
@RestController
@Api(tags = "用户管理01")
@RequestMapping("/agent/api/v1")
public class Test1Controller {

    @Autowired
    private Service1Impl service1;

    @PostMapping("/01")
    @ApiOperation(value = "用户测试", notes = "用户测试notes")
    public Test1VO api_1(@RequestBody Test1VO test1VO) {
        service1.test();
        return test1VO;
    }

    @GetMapping("/02")
    @ApiOperation(value = "用户测试", notes = "用户测试notes")
    public Test2VO api_2(Test2VO test2VO) {
        service1.test2();
        return test2VO;
    }

    @PostMapping("/03")
    @ApiOperation(value = "用户测试", notes = "用户测试notes")
    public Test3VO api_3(Test3VO test3VO) {
        return test3VO;
    }

    @PostMapping("/04")
    @ApiOperation(value = "用户测试", notes = "用户测试notes")
    public Test4VO api_4(Test4VO test4VO) {
        return test4VO;
    }

}
