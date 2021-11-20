package net.lemonplan.dao;

import net.lemonplan.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User findUserByName(String username);

    int insertUser(User user);

    int updateFaceImage(@Param("username") String username, @Param("_path") String _path, @Param("path") String path);

    int updateNickname(@Param("username") String username, @Param("nickname") String nickname);

    List<User> queryAllUserByIds(@Param("ids") List<String> ids);

    User queryUserById(String friendId);
}
