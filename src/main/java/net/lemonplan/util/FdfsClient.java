package net.lemonplan.util;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author TieJianKuDan
 * @Date 2021/11/8 11:34
 * @Description 本地化 fdfs 客户端
 * @Since version-1.0
 */
@Component
public class FdfsClient {
    @Autowired
    private FastFileStorageClient client;

    public String uploadPng(MultipartFile file) throws IOException {
        StorePath storePath = client.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), "png", null);
        return storePath.getFullPath();
    }
}
