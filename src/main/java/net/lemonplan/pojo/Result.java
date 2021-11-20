package net.lemonplan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author TieJianKuDan
 * @Date 2021/11/8 11:20
 * @Description 响应结果类
 * @Since version-1.0
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private int status;
    private boolean isSuccess;
    private String info;
    private T data;

    public class StandardStatus{
        public static final int OK = 200;
        public static final int PASSWORDERROR = 201;
        public static final int ADDFAIL = 202;
        public static final int UPDATAFAIL = 203;
        public static final int QUERYFAIL = 204;
        public static final int REQUESTERROR = 205;
        public static final int DELFAIL = 206;
    }
}
