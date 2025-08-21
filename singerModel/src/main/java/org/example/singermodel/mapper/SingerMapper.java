package org.example.singermodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.common.pojo.Singer;

import java.util.List;

@Mapper
public interface SingerMapper extends BaseMapper<Singer> {

    @Select("select s.singer_id,s.name,s.region_id,r.name as region,s.type_id,t.name as type,s.gender,s.image " +
            "from singer s left join singer_type t on s.type_id=t.type_id " +
            "left join region r on s.region_id=r.region_id")
    List<Singer> getAll();
    @Select("select s.singer_id,s.name,s.region_id,r.name as region,s.type_id,t.name as type,s.gender,s.image " +
            "from singer s left join singer_type t on s.type_id=t.type_id " +
            "left join region r on s.region_id=r.region_id " +
            "where s.singer_id=#{id}")
    Singer getById(Integer id);


}
