package controller;

import bean.HistoryEntity;
import bean.messageBean.HistoryMessage;
import bean.messageBean.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.GameService;

import javax.servlet.http.HttpSession;

@RestController
public class GameController {
    private GameService gameService;
    @Autowired
    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @RequestMapping(value = "/data/game/save",method = RequestMethod.POST)
    public Message save(HttpSession session,
                        @RequestParam(value = "gameId") int gameId,
                        @RequestParam(value = "history") String history){
        if(session.getAttribute("userId") == null){
            Message message = new Message();
            message.setResult(false);
            message.setMessage("用户未登录。");
            return message;
        }else {
            int userId = (int)session.getAttribute("userId");
            HistoryEntity historyEntity = new HistoryEntity();
            historyEntity.setUserId(userId);
            historyEntity.setGameId(gameId);
            historyEntity.setHistory(history);
            Message message = new Message();
            if(gameService.insertHistory(historyEntity) != 0){
                message.setResult(true);
                return message;
            }
            message.setResult(false);
            return message;
        }
    }

    @RequestMapping(value = "/data/game/load",method = RequestMethod.GET)
    public Message load(HttpSession session,
                        @RequestParam(value = "gameId") int gameId){
        if(session.getAttribute("userId") == null){
            Message message = new Message();
            message.setResult(false);
            message.setMessage("用户未登录。");
            return message;
        }else{
            int userId = (int)session.getAttribute("userId");
            HistoryMessage message = new HistoryMessage();
            HistoryEntity historyEntity = gameService.selectHistoryByUserId(userId,gameId);
            if(historyEntity == null){
                message.setResult(true);
                message.setHistory("");
            }else {
                message.setResult(true);
                message.setHistory(historyEntity.getHistory());
            }
            return  message;
        }
    }
}
