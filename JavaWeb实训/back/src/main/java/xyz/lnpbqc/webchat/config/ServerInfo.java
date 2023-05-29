package xyz.lnpbqc.webchat.config;

import java.util.HashMap;
import java.util.HashSet;

public class ServerInfo {

//    存储用户名-密钥（类似，用于保证合法性
    private static HashMap<String,String> legals = new HashMap<>();
    public static void addLegal(String legalUser,String legalKey){
        legals.put(legalUser,legalKey);
    }
    @Deprecated
    public static void removeLegal(String legal){
        legals.remove(legal);
    }

    public static String getLegal(String user){
        return legals.get(user);
    }
}
