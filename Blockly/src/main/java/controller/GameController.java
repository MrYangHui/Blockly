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

@RestController
public class GameController {
    private GameService gameService;
    @Autowired
    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @RequestMapping(value = "/data/game/save",method = RequestMethod.POST)
    public Message save(@RequestParam(value = "userId") int userId,
                        @RequestParam(value = "gameId") int gameId,
                        @RequestParam(value = "history") String history){
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

    @RequestMapping(value = "/data/game/load",method = RequestMethod.GET)
    public Message load(@RequestParam(value = "userId") int userId,
                        @RequestParam(value = "gameId") int gameId){
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
