package xyz.lnpbqc.webchat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.lnpbqc.webchat.pojo.JsonResult;
import xyz.lnpbqc.webchat.pojo.User;
import xyz.lnpbqc.webchat.serivces.UserService;

@RestController()
@RequestMapping("/test")
public class Test {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public JsonResult startTest(){
        return new JsonResult("a","");
    }

    @PostMapping("/login")
    public JsonResult loginTest(String name,String password){
        User request = new User(name, password);
        String msg = userService.isExits(request)?"y":"n";
        return new JsonResult("msg",msg);
    }
}
