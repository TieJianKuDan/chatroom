package net.lemonplan.dao;

import net.lemonplan.pojo.Friend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FriendMapper {
    public List<String> queryAllByUserId(String userId);

    public String queryOneByUserIdAndFriendId(@Param("userId") String userId, @Param("friendId") String friendId);

    int insertFriendRelation(Friend friend);

    int deleteFriendRelation(Friend friend);
}
