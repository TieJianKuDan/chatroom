package net.lemonplan.controller;

import lombok.extern.slf4j.Slf4j;
import net.lemonplan.pojo.*;
import net.lemonplan.service.UserService;
import net.lemonplan.util.FdfsClient;
import net.lemonplan.util.FileUtils;
import net.lemonplan.util.Md5Utils;
import net.lemonplan.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FdfsClient fdfsClient;

    /**
     * 登录和注册一体化方法
     * @param user
     * @return
     */
    @PostMapping("/loginOrRegister")
    @CrossOrigin
    public Result<User> loginOrRegister(@RequestBody User user, HttpServletResponse response) throws IOException {
        log.debug("登录信息：{}", user);
        User registUser = userService.queryByUsername(user.getUsername());
        String subPwd = Md5Utils.encode(user.getPassword());
        // 已经注册过但是密码错误
        if (registUser != null && !registUser.getPassword().equals(subPwd)) {
            return new Result<>(Result.StandardStatus.PASSWORDERROR, false, "密码错误", null);
        }
        // 未注册
        if (registUser == null) {
            user.setPassword(subPwd);
            registUser = userService.addUser(user);
        }
        if (registUser != null) {
            UserUtils.toSafeUser(registUser);
            return new Result<>(Result.StandardStatus.OK, true, "登录成功", registUser);
        } else {
            return new Result<>(Result.StandardStatus.ADDFAIL, false, "注册失败", null);
        }
    }

    /**
     * 保存上传的头像（规定png格式）
     * @param portrait
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadPortrait")
    public Result uploadPortrait(@RequestBody Portrait portrait) throws IOException {
        String username = portrait.getUsername();
        String base64Image = portrait.getBase64Image();
        String localpath = "tmp/image/" + username + ".png";
        File file = new File(localpath);
        boolean flag = FileUtils.base64ToImage(base64Image, file);
        if (!flag) {
            return new Result(Result.StandardStatus.UPDATAFAIL, false, "base64转换失败", null);
        }
        MultipartFile multipartFile = FileUtils.png2MultipartFile(file);
        String uploadPath = fdfsClient.uploadPng(multipartFile);
        log.debug("上传至：" + uploadPath);
        User user = userService.setPortrait(username, uploadPath);
        if (user != null) {
            return new Result(Result.StandardStatus.OK, true, "上传成功", user);
        } else {
            UserUtils.toSafeUser(user);
            return new Result(Result.StandardStatus.UPDATAFAIL, false, "上传失败", null);
        }
    }

    /**
     * 更新昵称
     * @param user
     * @return
     */
    @PostMapping("/updateNickname")
    public Result updateNickname(@RequestBody User user) {
        User latestUser = userService.updateNickname(user.getUsername(), user.getNickname());
        UserUtils.toSafeUser(latestUser);
        if (latestUser == null) {
            return new Result(Result.StandardStatus.UPDATAFAIL, false, "修改失败", null);
        } else {
            return new Result(Result.StandardStatus.OK, true, "修改成功", latestUser);
        }
    }

    /**
     * 查询好友列表
     * @param userId
     * @return
     */
    @GetMapping("/queryAllFriends")
    public Result<List<User>> queryAllFriends(String userId) {
        List<User> users = userService.queryAllFriends(userId);
        if (users != null) {
            return new Result<>(Result.StandardStatus.OK, true, "查询成功", users);
        } else {
            return new Result<>(Result.StandardStatus.OK, true, "查询失败", users);
        }
    }

    /**
     * 根据 id 查询好友信息
     * @param friendId
     * @return
     */
    @GetMapping("/queryUserById")
    public Result<User> queryUserById(String friendId) {
        User user = userService.queryUserById(friendId);
        if (user == null) {
            return new Result<>(Result.StandardStatus.QUERYFAIL, false, "查询失败", null);
        } else {
            UserUtils.toSafeUser(user);
            return new Result<>(Result.StandardStatus.OK, true, "查询成功", user);
        }
    }

    /**
     * 根据用户名搜索用户
     * @param stranger
     * @param myId
     * @return
     */
    @GetMapping("/queryUser")
    public Result<User> queryUser(String stranger, String myId) {
        User user = userService.queryByUsername(stranger);
        if (user == null) {
            return new Result<>(Result.StandardStatus.QUERYFAIL, false, "没有此用户", null);
        }
        UserUtils.toSafeUser(user);
        String friendId = userService.queryFriend(myId, user.getId());
        if (friendId == null) {
            return new Result<>(Result.StandardStatus.OK, true, "陌生人", user);
        } else {
            return new Result<>(Result.StandardStatus.OK, true, "好友", user);
        }
    }

    /**
     * 发送好友申请
     * @param request
     * @return
     */
    @PostMapping("/addFriendRequest")
    public Result addFriendRequest(@RequestBody Request request) {
        if (request == null) {
            return new Result<>(Result.StandardStatus.REQUESTERROR, false, "请求格式错误", null);
        }
        boolean flag = userService.addFriendRequest(request);
        if (flag) {
            return new Result<>(Result.StandardStatus.OK, true, "已发送好友申请", null);
        } else {
            return new Result<>(Result.StandardStatus.ADDFAIL, false, "发送好友请求失败", null);
        }
    }

    /**
     * 获取用户所有的好友申请
     * @param userId
     * @return
     */
    @GetMapping("/getAllFriendRequest")
    public Result<List> getAllFriendRequest(String userId) {
        List<String> list = userService.getAllFriendRequest(userId);
        List<User> users = userService.queryUsersByIds(list);
        if (users == null) {
            new Result<List>(Result.StandardStatus.QUERYFAIL, false, "获取好友申请失败", null);
        }
        for (User user : users) {
            UserUtils.toSafeUser(user);
        }
        return new Result<List>(Result.StandardStatus.OK, true, "获取好友申请成功", users);
    }

    /**
     * 拒绝好友请求
     * @param request
     * @return
     */
    @PostMapping("/dismissFriendRequest")
    public Result dismissFriendRequest(@RequestBody Request request) {
        boolean flag = userService.deleteFriendRequest(request);
        if (flag) {
            return new Result<>(Result.StandardStatus.OK, true, "删除请求成功", null);
        } else {
            return new Result<>(Result.StandardStatus.DELFAIL, true, "删除请求失败", null);
        }
    }

    /**
     * 同意好友请求
     * @param request
     * @return
     */
    @PostMapping("/agreeFriendRequest")
    public Result<User> agreeFriendRequest(@RequestBody Request request) {
        Friend friend = new Friend();
        friend.setUserId(request.getAcceptUserId());
        friend.setFriendId(request.getSendUserId());
        boolean flag = userService.addFriendRelation(friend);
        if (!flag) {
            return new Result<>(Result.StandardStatus.ADDFAIL, true, "添加好友失败", null);
        }
        flag = userService.deleteFriendRequest(request);
        if (!flag) {
            return new Result<>(Result.StandardStatus.ADDFAIL, false, "删除请求失败", null);
        }
        // 清除重复的好友请求
        Request repeatReq = new Request();
        repeatReq.setAcceptUserId(repeatReq.getSendUserId());
        repeatReq.setSendUserId(repeatReq.getAcceptUserId());
        userService.deleteFriendRequest(repeatReq);
        return new Result<>(Result.StandardStatus.OK, true, "已添加好友", null);
    }

    /**
     * 删除好友
     * @param friend
     * @return
     */
    @PostMapping("/deleteFriend")
    public Result dismissFriendRequest(@RequestBody Friend friend) {
        boolean flag = userService.deleteFriend(friend);
        if (flag) {
            return new Result<>(Result.StandardStatus.OK, true, "删除好友成功", null);
        } else {
            return new Result<>(Result.StandardStatus.DELFAIL, false, "删除好友失败", null);
        }
    }
}
