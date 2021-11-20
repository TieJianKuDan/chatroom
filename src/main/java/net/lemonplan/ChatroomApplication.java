package net.lemonplan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Author TieJianKuDan
 * @Date 2021/11/8 11:22
 * @Description springboot 启动类
 * @Since version-1.0
 */
@SpringBootApplication
@MapperScan("net/lemonplan/dao")
public class ChatroomApplication extends SpringBootServletInitializer{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ChatroomApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatroomApplication.class, args);
    }
}
