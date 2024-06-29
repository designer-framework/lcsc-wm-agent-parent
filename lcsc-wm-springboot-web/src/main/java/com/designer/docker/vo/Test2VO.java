package com.designer.docker.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-29 13:46
 */
@Data
@ApiModel(value = "用户登录表单对象", description = "用户登录表单对象")
public class Test2VO {

    @ApiModelProperty(value = "名字1", required = true, example = "123456")
    private String name1;
    @ApiModelProperty(value = "名字2", required = true, example = "123456")
    private BigDecimal name2;
    @ApiModelProperty(value = "名字3", required = true, example = "123456")
    private BigDecimal name3;
    @ApiModelProperty(value = "名字4", required = true, example = "123456")
    private String name4;
    @ApiModelProperty(value = "名字5", required = true, example = "123456")
    private Long name5;
    @ApiModelProperty(value = "名字6", required = true, example = "123456")
    private Integer name6;
    @ApiModelProperty(value = "名字7", required = true, example = "123456")
    private String name7;
    @ApiModelProperty(value = "名字8", required = true, example = "123456")
    private String name8;
    @ApiModelProperty(value = "名字9", required = true, example = "123456")
    private Integer name9;
    @ApiModelProperty(value = "名字10", required = true, example = "123456")
    private String name10;
    @ApiModelProperty(value = "名字11", required = true)
    private List<Test1VO> name11;
    @ApiModelProperty(value = "名字12", required = true, example = "123456")
    private List<String> name12;
    @ApiModelProperty(value = "名字13", required = true)
    private Map<String, Test1VO> name13;

}
