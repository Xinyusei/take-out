package com.sky.controller.admin;


import com.aliyuncs.exceptions.ClientException;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用接口
 */
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
@RestController
public class CommonController {

    @Autowired
    AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result<String> upload(MultipartFile file) throws Exception {
        //日志记录
        log.info("文件上传,文件名-{}", file.getOriginalFilename());
        //调用阿里云的上传方法
        String url = aliOssUtil.upload(file);
        log.info("文件访问url:{}",url);
        return Result.success(url);
    }
}
