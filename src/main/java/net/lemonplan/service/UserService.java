package net.lemonplan.service;

import net.lemonplan.pojo.Friend;
import net.lemonplan.pojo.Request;
import net.lemonplan.pojo.User;

import java.io.IOException;
import java.util.List;

/**
 * @Author TieJianKuDan
 * @Date 2021/11/8 11:21
 * @Description 用户操作服务类
 * @Since version-1.0
 */
public interface UserService {
    User queryByUsername(String username);

    User addUser(User user) throws IOException;

    User setPortrait(String username, String path);

    User updateNickname(String username, String nickname);

    List<User> queryAllFriends(String userId);

    String queryFriend(String me, String stranger);

    boolean addFriendRequest(Request request);

    List<String> getAllFriendRequest(String userId);

    List<User> queryUsersByIds(List<String> list);

    boolean deleteFriendRequest(Request request);

    boolean addFriendRelation(Friend friend);

    boolean deleteFriend(Friend friend);

    User queryUserById(String friendId);
}
