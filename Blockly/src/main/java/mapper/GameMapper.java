package mapper;

import bean.HistoryEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface GameMapper {
    @Insert("insert into history(userId,gameId,history) values (#{userId},#{gameId},#{history}) on duplicate key update history = #{history}")
    public int insertHistory(HistoryEntity history);

    @Select("select * from history where userId = #{param1} and gameId = #{param2}")
    public HistoryEntity selectHistoryByUserId(int userId, int gameId);
}
