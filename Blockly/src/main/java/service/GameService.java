package service;

import bean.HistoryEntity;
import mapper.GameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private GameMapper gameMapper;

    @Autowired
    public GameService(GameMapper gameMapper){
        this.gameMapper = gameMapper;
    }

    public int insertHistory(HistoryEntity history){
        return gameMapper.insertHistory(history);
    }

    public HistoryEntity selectHistoryByUserId(int userId, int gameId){
        return gameMapper.selectHistoryByUserId(userId,gameId);
    }
}
