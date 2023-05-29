package xyz.lnpbqc.webchat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.lnpbqc.webchat.Utils.JsonUtils;
import xyz.lnpbqc.webchat.config.ServerInfo;
import xyz.lnpbqc.webchat.pojo.JsonResult;
import xyz.lnpbqc.webchat.pojo.User;
import xyz.lnpbqc.webchat.serivces.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public JsonResult login(User request){
        logger.info(request.getAccount()+"以"+request.getPassword()+"登录了");
        ConcurrentHashMap<String, ChatController> onlineUsers = ChatController.getOnlineUsers();
//        防止重复登录
        ChatController legal = onlineUsers.get(request.getAccount());
        if(legal!=null){
            return new JsonResult("msg","123");
        }

//        后续聊天通过此验证
        String key = UUID.randomUUID().toString();
        ServerInfo.addLegal(request.getAccount(),key);
        String msg = userService.isExits(request)?key:"n";


        return new JsonResult("msg",msg);
    }

    @PostMapping("reLogin")
    public JsonResult reLogin(String user,String key){
        logger.info(user+"以密钥:"+key+"尝试重新登录");
        ConcurrentHashMap<String, ChatController> onlines = ChatController.getOnlineUsers();
//        防止重复登录
        ChatController legal = onlines.get(user);
        if(legal!=null){
            return new JsonResult("msg","123");
        }

        String msg = ServerInfo.getLegal(user).equals(key)?"y":"n";

        return new JsonResult("msg",msg);
    }


    @PostMapping("register")
    public JsonResult register(User request){
        logger.info(request.getAccount()+"正在注册，密码是:"+request.getPassword());
        String msg = userService.addUser(request)?"y":"n";
        return new JsonResult("msg",msg);
    }

    @PostMapping("changePassword")
    public JsonResult changerPassword(String user,String key,String password){
        logger.info(user+"正在尝试修改密码为："+password);
        if(!ServerInfo.getLegal(user).equals(key)){
            logger.warn(user+"的密钥为："+key+",密钥错误，修改失败");
            return new JsonResult("msg","n");
        }
        User newUser = new User(user, password);
        String msg = userService.changePassword(newUser)?"y":"n";
        return new JsonResult("msg",msg);
    }


    @PostMapping("/users")
    public String getUsers(String user,String key) throws JsonProcessingException {
        logger.info(user+"以密钥:"+key+"尝试获取所有用户");
        String legal = ServerInfo.getLegal(user);
        if (legal==null||!legal.equals(key)){
            logger.info(user+"获取失败，密钥非法或不存在");
            return JsonUtils.toJson("非法获取用户");
        }else {
            User[] users = userService.allUsers();
            ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();
            ConcurrentHashMap<String, ChatController> onlineUsers = ChatController.getOnlineUsers();
            for(User u:users){
                ChatController chatController = onlineUsers.get(u.getAccount());
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("name",u.getAccount());
                if (chatController!=null){
                    stringStringHashMap.put("online","true");
                }else{
                    stringStringHashMap.put("online","false");
                }
                hashMaps.add(stringStringHashMap);
            }
            return JsonUtils.toJson(hashMaps);
        }
    }
}
