package service;

import bean.RecordEntity;
import bean.UserEntity;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserEntity selectUserByName(String name) {
        return userMapper.selectUserByName(name);
    }

    public UserEntity selectUserById(int id) {
        return userMapper.selectUserById(id);
    }

    public int insertUser(UserEntity user){
        return userMapper.insertUser(user);
    }

    public int insertRecord(RecordEntity record){
        return userMapper.insertRecord(record);
    }

    public List<RecordEntity> selectRecordsByUserId(int userId){
        return userMapper.selectRecordsByUserId(userId);
    }
}
