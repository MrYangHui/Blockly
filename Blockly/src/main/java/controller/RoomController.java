package controller;

import bean.RoomEntity;
import bean.messageBean.Message;
import bean.messageBean.RoomMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.RoomSocketServer;

import java.util.Random;

@RestController
public class RoomController {

    @RequestMapping(value = "/data/room/create",method = RequestMethod.GET)
    public Message create(@RequestParam("userId") int userId,
                          @RequestParam("gameId") int gameId){
        RoomEntity room = new RoomEntity();
        room.setUserId(userId);
        room.setGameId(gameId);
        //room.setRoomId(getNewRoomId());
        RoomSocketServer.addNewRoom(room);
        RoomMessage message = new RoomMessage();
        message.setRoomId(room.getRoomId());
        message.setResult(true);
        return message;
    }

    @RequestMapping(value = "/data/room/connect",method = RequestMethod.POST)
    public Message connect(@RequestParam("userId") int userId,
                           @RequestParam("roomId") String roomId){
        return new Message();
    }
}
