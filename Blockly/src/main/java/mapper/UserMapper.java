package mapper;

import bean.RecordEntity;
import bean.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {

    @Select("select * from user where name = #{name}")
    public UserEntity selectUserByName(String name);

    @Select("select * from user where id = #{id}")
    public UserEntity selectUserById(int id);

    @Insert("insert into user(name,pwd,email,avatar) values (#{name},#{pwd},#{email},#{avatar})")
    public int insertUser(UserEntity user);

    @Insert("insert into record(userId,gameId,date,time,status) values (#{userId},#{gameId},#{date},#{time},#{status})")
    public int insertRecord(RecordEntity record);

    @Select("select * from record where userId = #{userId}")
    public List<RecordEntity> selectRecordsByUserId(int userId);
}
