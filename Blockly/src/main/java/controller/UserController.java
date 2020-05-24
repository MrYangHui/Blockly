package controller;

import bean.RecordEntity;
import bean.UserEntity;
import bean.messageBean.InformationMessage;
import bean.messageBean.LoginMessage;
import bean.messageBean.Message;
import bean.messageBean.RecordMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/data/user/register",method = RequestMethod.POST)
    public Message register(@RequestParam(value = "userName",required = false) String name,
                         @RequestParam(value = "password",required = false) String pwd){
        Message message = new Message();
        if(name == null || pwd == null || "".equals(name) || "".equals(pwd)){
            message.setResult(false);
            message.setMessage("用户名和密码不能为空。");
            return message;
        }
        if(userService.selectUserByName(name)!=null){
            message.setResult(false);
            message.setMessage("用户名已被使用。");
            return message;
        }
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setPwd(pwd);
        if(userService.insertUser(user)!=0){
            message.setResult(true);
            return message;
        }
        message.setResult(false);
        return message;
    }

    @RequestMapping(value = "/data/user/login",method = RequestMethod.GET)
    public Message login(@RequestParam(value = "userName",required = false) String name,
                         @RequestParam(value = "password",required = false) String pwd,
                        HttpSession session){
        Message message = new Message();
        if(name == null && pwd == null){
            message.setResult(false);
            message.setMessage("用户名和密码不能为空。");
            return message;
        }
        if(userService.selectUserByName(name)==null){
            message.setResult(false);
            message.setMessage("用户不存在。");
            return message;
        }
        UserEntity user = userService.selectUserByName(name);
        if(user.getPwd().equals(pwd)){
            session.setAttribute("userId",user.getId());  //用session保存了登录的用户
            LoginMessage loginMessage = new LoginMessage();
            loginMessage.setResult(true);
            loginMessage.setUserId(user.getId());
            return loginMessage;
        }
        message.setResult(false);
        message.setMessage("用户名或密码错误。");
        return message;
    }

    @RequestMapping(value = "/data/user/logout",method = RequestMethod.GET)
    public Message logout(HttpSession session){
        Message message = new Message();
        if(session.getAttribute("userId")!=null){
            session.removeAttribute("userId");
            message.setResult(true);
            return message;
        }
        message.setResult(false);
        message.setMessage("用户未登录。");
        return message;
    }

    @RequestMapping(value = "/data/user/information",method = RequestMethod.GET)
    public Message information(HttpSession session){
        if(session.getAttribute("userId")!=null){
            int userId = (int)session.getAttribute("userId");
            UserEntity userEntity = userService.selectUserById(userId);
            InformationMessage informationMessage = new InformationMessage();
            informationMessage.setResult(true);
            informationMessage.setUserName(userEntity.getName());
            informationMessage.setAvatar(userEntity.getAvatar());
            informationMessage.setEmail(userEntity.getEmail());
            return informationMessage;
        }
        Message message = new Message();
        message.setResult(false);
        message.setMessage("用户未登录。");
        return message;
    }

    @RequestMapping(value = "/data/user/record",method = RequestMethod.POST)
    public Message record(HttpSession session,
                          @RequestParam(value = "gameId") int gameId,
                          @RequestParam(value = "date") String date,
                          @RequestParam(value = "time") String time,
                          @RequestParam(value = "status") boolean status){
        if(session.getAttribute("userId") == null){
            Message message = new Message();
            message.setResult(false);
            message.setMessage("用户未登录。");
            return message;
        }else {
            int userId = (int)session.getAttribute("userId");
            RecordEntity record = new RecordEntity();
            record.setUserId(userId);
            record.setGameId(gameId);
            record.setDate(date);
            record.setTime(time);
            record.setStatus(status);

            Message message = new Message();
            if(userService.insertRecord(record) != 0){
                message.setResult(true);
                return message;
            }
            message.setResult(false);
            return message;
        }
    }

    @RequestMapping(value = "/data/user/record",method = RequestMethod.GET)
    public Message record(HttpSession session){
        if(session.getAttribute("userId") == null){
            Message message = new Message();
            message.setResult(false);
            message.setMessage("用户未登录。");
            return message;
        }else {
            int userId = (int)session.getAttribute("userId");
            List<RecordEntity> records = userService.selectRecordsByUserId(userId);
            RecordMessage recordMessage = new RecordMessage();
            recordMessage.setRecords(records);
            recordMessage.setResult(true);
            return recordMessage;
        }
    }
}
