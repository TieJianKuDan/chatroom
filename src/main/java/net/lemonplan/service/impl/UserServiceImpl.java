package net.lemonplan.service.impl;

import net.lemonplan.dao.FriendMapper;
import net.lemonplan.dao.RequestMapper;
import net.lemonplan.dao.UserMapper;
import net.lemonplan.pojo.Friend;
import net.lemonplan.pojo.Request;
import net.lemonplan.pojo.User;
import net.lemonplan.service.UserService;
import net.lemonplan.util.FdfsClient;
import net.lemonplan.util.FileUtils;
import net.lemonplan.util.QRCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private FdfsClient fdfsClient;

    @Override
    public User queryByUsername(String username) {
        return userMapper.findUserByName(username);
    }

    @Override
    public User addUser(User user) throws IOException {
        user.setId(UUID.randomUUID().toString());
        user.setFaceImage("");
        user.setFaceImageBig("");
        user.setNickname(user.getUsername());
        File file = new File("tmp/image/" + user.getUsername() + ".png");
        QRCodeUtils.makeQrcode(file, user.getUsername());
        String uploadPath = fdfsClient.uploadPng(FileUtils.png2MultipartFile(file));
        user.setQrcode(uploadPath);
        return userMapper.insertUser(user) != 0 ? user : null;
    }

    @Override
    public User setPortrait(String username, String path) {
        String[] subStr = path.split("\\.");
        String _path = subStr[0] + "_150x150." + subStr[1];
        int count = userMapper.updateFaceImage(username, _path, path);
        if (count > 0) {
            return userMapper.findUserByName(username);
        } else {
            return null;
        }
    }

    @Override
    public User updateNickname(String username, String nickname) {
        int count = userMapper.updateNickname(username, nickname);
        if (count > 0) {
            return userMapper.findUserByName(username);
        } else {
            return null;
        }
    }

    @Override
    public List<User> queryAllFriends(String userId) {
        List<String> list = friendMapper.queryAllByUserId(userId);
        List<User> users = userMapper.queryAllUserByIds(list);
        return users;
    }

    @Override
    public String queryFriend(String myId, String stranger) {
        return friendMapper.queryOneByUserIdAndFriendId(myId, stranger);
    }

    @Override
    public boolean addFriendRequest(Request request) {
        // 检查是否是重复请求
        if (requestMapper.queryRequest(request) != null) {
            return true;
        }
        request.setId(UUID.randomUUID().toString());
        request.setRequestTime(new Date());
        int count = requestMapper.insert(request);
        return count > 0;
    }

    @Override
    public List<String> getAllFriendRequest(String userId) {
        return requestMapper.queryByAcceptUserId(userId);
    }

    @Override
    public List<User> queryUsersByIds(List<String> list) {
        return userMapper.queryAllUserByIds(list);
    }

    @Override
    public boolean deleteFriendRequest(Request request) {
        int count = requestMapper.deleteRequest(request);
        return count > 0;
    }

    @Override
    public boolean addFriendRelation(Friend friend) {
        friend.setId(UUID.randomUUID().toString());
        int count = friendMapper.insertFriendRelation(friend);
        if (count == 0) {
            return false;
        }
        Friend friend_ = new Friend();
        friend_.setId(UUID.randomUUID().toString());
        friend_.setUserId(friend.getFriendId());
        friend_.setFriendId(friend.getUserId());
        count = friendMapper.insertFriendRelation(friend_);
        return count > 0;
    }

    @Override
    public boolean deleteFriend(Friend friend) {
        friendMapper.deleteFriendRelation(friend);
        Friend _friend = new Friend();
        _friend.setUserId(friend.getFriendId());
        _friend.setFriendId(friend.getUserId());
        friendMapper.deleteFriendRelation(_friend);
        return true;
    }

    @Override
    public User queryUserById(String friendId) {
        return userMapper.queryUserById(friendId);
    }
}
