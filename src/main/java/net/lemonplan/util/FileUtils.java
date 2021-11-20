package net.lemonplan.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @Author TieJianKuDan
 * @Date 2021/11/8 11:21
 * @Description 文件工具类
 * @Since version-1.0
 */
public class FileUtils {
    public static boolean base64ToImage(String base64Image, File file) {
        try {
            String data = base64Image.split(";base64,")[1];
            byte[] image = Base64Utils.decodeFromString(data);
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, image);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static MultipartFile png2MultipartFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/png", fis);
        return multipartFile;
    }
}
