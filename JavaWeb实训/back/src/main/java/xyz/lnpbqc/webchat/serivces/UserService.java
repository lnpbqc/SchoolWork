package xyz.lnpbqc.webchat.serivces;

import xyz.lnpbqc.webchat.pojo.User;

public interface UserService {
    boolean isExits(User u);

    boolean addUser(User u);

    boolean changePassword(User u);

    User[] allUsers();
}
