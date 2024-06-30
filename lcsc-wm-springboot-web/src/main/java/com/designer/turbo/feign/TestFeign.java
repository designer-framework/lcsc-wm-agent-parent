package com.designer.turbo.feign;

import com.designer.turbo.vo.Test1VO;
import com.designer.turbo.vo.Test2VO;
import com.designer.turbo.vo.Test3VO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-26 00:26
 */
public interface TestFeign {

    @PostMapping("/01")
    Test1VO api_1(@RequestBody Test1VO test1VO);

    @GetMapping("/02")
    Test2VO api_2(Test2VO test2VO);

    @PostMapping("/03")
    Test3VO api_3(Test3VO test3VO);

}
