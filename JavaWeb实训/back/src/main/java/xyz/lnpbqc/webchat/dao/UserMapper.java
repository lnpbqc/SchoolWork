package xyz.lnpbqc.webchat.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import xyz.lnpbqc.webchat.pojo.User;

@Mapper
public interface UserMapper {
    User isExits(User u);
    void changePassword(User u);
    User findUserByAccount(String account);
    void addUser(User u);

    User[] allUsers();
}
