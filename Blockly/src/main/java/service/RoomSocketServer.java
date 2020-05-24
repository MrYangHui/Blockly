package service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import aspect.LogAspect;
import bean.RoomEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import config.WebsocketConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@ServerEndpoint(value = "/room/{connectParam}",configurator =  WebsocketConfiguration.class)
@Component
public class RoomSocketServer {
    private Logger log = LoggerFactory.getLogger(LogAspect.class);

    public static final int LENGTH_OF_ROOM_ID = 8;
    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的RoomSocketServer对象。*/
    private static ConcurrentHashMap<String, RoomSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**保存已有的房间实例对象*/
    private static ConcurrentHashMap<String, RoomEntity> roomMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId = "";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("connectParam") String connectParam) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if(httpSession == null){
            log.info("无法获取HTTPSession。");
            try{
                session.close();
            }catch (IOException e){
                log.info(e.getMessage());
            }
            return;
        }
        if(httpSession.getAttribute("userId") == null){
            log.info("用户未登录,无法建立websocket连接。");
            try{
                session.close();
            }catch (IOException e){
                log.info(e.getMessage());
            }
            return;
        }
        String httpSessionUserId = (String)httpSession.getAttribute("userId");
        this.session = session;
        this.userId=httpSessionUserId;
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            webSocketMap.put(userId,this);
        }else{
            webSocketMap.put(userId,this);
            addOnlineCount();
        }

        try{
            int uid = Integer.parseInt(userId);
            for(Map.Entry<String,RoomEntity> entry : roomMap.entrySet()){
                RoomEntity room = entry.getValue();
                if(room.getcUserId()==uid || room.getUserId()==uid){
                    sendMessage("你已在一个房间中,房间id="+room.getRoomId());
                    try{
                        session.close();
                    }catch (IOException e){
                        log.info(e.getMessage());
                    }
                    return;
                }
            }
            if(connectParam.length() != 8){
                log.info("创建房间");
                int gid = Integer.parseInt(connectParam);
                RoomEntity room = new RoomEntity();
                room.setUserId(uid);
                room.setGameId(gid);
                room.setRoomId(getNewRoomId());
                addNewRoom(room);
                sendMessage(room.getRoomId());
                log.info("房间号: "+room.getRoomId());
            }else {
                log.info("加入房间");
                RoomEntity room = roomMap.get(connectParam);
                if(room == null){
                    log.info("房间不存在。");
                    sendMessage("房间不存在。");
                }else {
                    if(room.getcUserId() == 0){
                        sendMessage("连接成功。");
                        room.setcUserId(uid);
                    }else {
                        log.info("房间已满。");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("用户:uid="+userId+",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        for(Map.Entry<String,RoomEntity> entry : roomMap.entrySet()){
            String roomId = entry.getKey();
            RoomEntity room = entry.getValue();
            if(userId.equals(room.getcUserId()+"")){
                room.setcUserId(0);
                try{
                    webSocketMap.get(room.getUserId()+"").sendMessage("对方已退出。");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(userId.equals(room.getUserId()+"") && room.getcUserId() != 0){
                try{
                    webSocketMap.get(room.getcUserId()+"").sendMessage("对方已退出。");
                }catch (Exception e){
                    e.printStackTrace();
                }
                room.setUserId(room.getcUserId());
                room.setcUserId(0);
            }else if(userId.equals(room.getUserId()+"") && room.getcUserId() != 0){
                roomMap.remove(roomId);
            }
        }
        log.info("用户退出:uid="+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:uid="+userId+",报文:"+message);
        if(StringUtils.isNotBlank(message)){
            try {
                for(Map.Entry<String,RoomEntity> entry : roomMap.entrySet()){
                    String roomId = entry.getKey();
                    RoomEntity room = entry.getValue();
                    String sendId = "";
                    if(userId.equals(room.getUserId()+"")){
                        sendId = room.getcUserId()+"";
                    }else if(userId.equals(room.getcUserId()+"")){
                        sendId = room.getUserId()+"";
                    }
                    if(webSocketMap.containsKey(sendId)){
                        webSocketMap.get(sendId).sendMessage(message);
                        return;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("用户错误:uid="+this.userId+",原因:"+error.getMessage());
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message,@PathParam("userId") String userId) throws IOException {
        System.out.println("发送消息到:uid="+userId+"，报文:"+message);
        if(StringUtils.isNotBlank(userId)&&webSocketMap.containsKey(userId)){
            webSocketMap.get(userId).sendMessage(message);
        }else{
            System.out.println("用户uid="+userId+",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        RoomSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        RoomSocketServer.onlineCount--;
    }

    public static synchronized void addNewRoom(RoomEntity room){
        roomMap.put(room.getRoomId(),room);
    }

    public static synchronized RoomEntity getRoomById(String roomId){
        return roomMap.get(roomId);
    }

    private static String getNewRoomId(){
        StringBuilder val;
        Random random = new Random();
        do{
            val = new StringBuilder();
            for(int i = 0; i < LENGTH_OF_ROOM_ID; i++) {
                String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
                //输出字母还是数字
                if("char".equalsIgnoreCase(charOrNum)){
                    //输出是大写字母还是小写字母
                    int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                    val.append((char)(random.nextInt(26) + temp));
                }else {
                    val.append(String.valueOf(random.nextInt(10)));
                }
            }
        }while (getRoomById(val.toString())!=null);
        return val.toString();
    }
}

