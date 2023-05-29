package xyz.lnpbqc.webchat.serivces.impl;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.lnpbqc.webchat.dao.UserMapper;
import xyz.lnpbqc.webchat.pojo.User;
import xyz.lnpbqc.webchat.serivces.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean isExits(User u) {
        return userMapper.isExits(u)!=null;
    }

    @Override
    public boolean addUser(User u) {
        int s = userMapper.findUserByAccount(u.getAccount())==null?0:1;
        userMapper.addUser(u);
        int e = userMapper.findUserByAccount(u.getAccount())==null?0:1;
        return s!=e;
    }

    @Override
    public boolean changePassword(User u) {
        User userByAccount = userMapper.findUserByAccount(u.getAccount());
        userMapper.changePassword(u);
        User userByAccount1 = userMapper.findUserByAccount(u.getAccount());
        return !userByAccount1.getPassword().equals(userByAccount.getPassword());
    }

    public User[] allUsers(){
        return userMapper.allUsers();
    }
}
