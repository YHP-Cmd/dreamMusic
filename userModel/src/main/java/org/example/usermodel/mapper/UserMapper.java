package org.example.usermodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.common.pojo.SingerType;
import org.example.common.pojo.User;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select type_id,type_name as name from user_type")
    List<SingerType> getAllType();
    @Update("update user set status='已冻结' where id=#{id}")
    int freeze(int id);
    @Update("update user set status='正常' where id=#{id}")
    int unfreeze(int id);
    @Select("select t.type_name from user u left join user_type t on t.type_id=u.type_id where u.username=#{username}")
    String getUserType(String username);
}
