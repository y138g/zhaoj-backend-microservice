package com.itgr.zhaojbackendfileservice.service;


import com.itgr.zhaojbackendmodel.model.dto.file.UploadFileRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
public interface FileService {
    /**
     * 文件上传
     * @param multipartFile 文件
     * @return 文件路径
     */
    String uploadFile(MultipartFile multipartFile, UploadFileRequest uploadFileRequest);

}
