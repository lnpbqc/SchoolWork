package xyz.lnpbqc.webchat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.lnpbqc.webchat.Utils.JsonUtils;
import xyz.lnpbqc.webchat.pojo.Message;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{user}")
@Component
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    //    存储用户名-ChatController（便于通过user名字来获取相应的session
    private static final ConcurrentHashMap<String,ChatController> onlineUsers;

    static {
        onlineUsers = new ConcurrentHashMap<>();
    }


    private Session session;

    private String user;



    @OnOpen
    public void OnOpen(Session session,@PathParam("user")String user){
        logger.info(user+"已经连接");
        //获取登录的用户
        this.session = session;
        this.user = user;
        onlineUsers.put(user,this);

        showOnlineUsers();
    }

    private void broadcastAllUsers(String msg) throws IOException {
        Set<String> users = onlineUsers.keySet();
        logger.info("正在给在线用户推送在线状态");
        logger.info("目前状态是："+msg);
        for (String user : users) {
            ChatController chatController = onlineUsers.get(user);
            Session session = chatController.getSession();
            session.getBasicRemote().sendText(msg);
        }
    }

    private void showOnlineUsers(){
        //构建登录的信息，发送给其他用户
        Message<Set<String>> setMessage = new Message<>("system", "all", onlineUsers.keySet());
        try {
            String s = JsonUtils.toJson(setMessage);
            broadcastAllUsers(s);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @OnClose
    public void OnClose(){
        logger.info(user+"下线了");
        onlineUsers.remove(user);

        showOnlineUsers();
    }

    @OnMessage
    public void OnMessage(String msg){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var message =  objectMapper.readValue(msg,HashMap.class);
            ChatController chatController = onlineUsers.get(message.get("to"));
            if(chatController==null){
                return;
            }
            message.remove("to");
            Session session = chatController.getSession();
            session.getBasicRemote().sendText(JsonUtils.toJson(message));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    @OnError
    public void OnError(Throwable e){
        logger.error(e.getMessage());
    }


    public static ConcurrentHashMap<String, ChatController> getOnlineUsers() {
        return onlineUsers;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
