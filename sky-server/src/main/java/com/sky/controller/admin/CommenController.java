package com.sky.controller.admin;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@Slf4j
@RestController
@RequestMapping ("/admin/common")
@Api(tags="通用接口")
public class CommenController {
    //注入aliOssUtil
    @Autowired
    private AliOssUtil aliOssUtil;


    @PostMapping("/upload")
    @ApiOperation("文件上传")
     public Result<String>upload(MultipartFile file){
        //非查询可以不用泛型
        log.info("文件上传: {}",file);
        try{
            String originalFilename = file.getOriginalFilename();
            int index=originalFilename.lastIndexOf(".");
            String extension = originalFilename.substring(index);
            String ObjectName= UUID.randomUUID().toString()+extension;
            String filePath= aliOssUtil.upload(file.getBytes(), ObjectName);
            return Result.success(filePath);
        }catch (IOException e){
            log.info("文件上传失败了: {}",e.getMessage());
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
