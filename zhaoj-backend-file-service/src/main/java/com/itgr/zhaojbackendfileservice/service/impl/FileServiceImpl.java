package com.itgr.zhaojbackendfileservice.service.impl;

import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.itgr.zhaojbackendcommon.common.ErrorCode;
import com.itgr.zhaojbackendcommon.exception.BusinessException;
import com.itgr.zhaojbackendcommon.utils.ConstantPropertiesUtil;
import com.itgr.zhaojbackendcommon.utils.ExcelUtils;
import com.itgr.zhaojbackendfileservice.service.FileService;
import com.itgr.zhaojbackendmodel.model.dto.file.UploadFileRequest;
import com.itgr.zhaojbackendmodel.model.enums.FileUploadBizEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;


/**
 * 用户服务实现
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(MultipartFile multipartFile, UploadFileRequest uploadFileRequest) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验文件
        validFile(multipartFile, fileUploadBizEnum);
        if (biz.equals(FileUploadBizEnum.USER_AVATAR.getValue())) {
            try {
                // 创建OSSClient实例
                OSS ossClient = new OSSClientBuilder().build(
                        ConstantPropertiesUtil.END_POINT,
                        ConstantPropertiesUtil.ACCESS_KEY_ID,
                        ConstantPropertiesUtil.ACCESS_KEY_SECRET
                );

                // 上传文件
                String fileName = multipartFile.getOriginalFilename();
                ossClient.putObject(ConstantPropertiesUtil.BUCKET_NAME, fileName, multipartFile.getInputStream());

                // 生成预签名URL
                GeneratePresignedUrlRequest generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(ConstantPropertiesUtil.BUCKET_NAME, fileName);
                // 设置URL的过期时间为10小时，36000秒
                java.util.Date expiration = new java.util.Date(System.currentTimeMillis() + 36000 * 1000);
                generatePresignedUrlRequest.setExpiration(expiration);
                // 生成URL
                String imageUrl = ossClient.generatePresignedUrl(generatePresignedUrlRequest).toString();

                // 关闭OSSClient
                ossClient.shutdown();
                log.info("上传成功");

                return imageUrl;
            } catch (Exception e) {
                e.printStackTrace();
                return "上传失败：" + e.getMessage();
            }
        }
        if (biz.equals(FileUploadBizEnum.ANALYZE_EXCEL.getValue())) {
            return ExcelUtils.excelToCsv(multipartFile);
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "不是有效文件");
    }


    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 100 * 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 100M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp", "xlsx").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
