package org.example.singermodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.common.pojo.Region;
import org.example.common.pojo.Singer;
import org.example.common.pojo.SingerType;
import org.example.common.pojo.TypeCountDTO;

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
    @Select("select st.name as typeName, " +
            "count(*) as count " +
            "from singer s " +
            "left join singer_type st on s.type_id = st.type_id " +
            "group by s.type_id, st.name")
    List<TypeCountDTO> getCountByType();
    List<Singer> selectSingerPage(
            Page<Singer> page,
            @Param("name") String name,
            @Param("gender") String gender,
            @Param("regionId") Integer regionId,
            @Param("typeId") Integer singerTypeId
    );
    // 获取所有歌手类型
    @Select("select * from singer_type order by type_id")
    List<SingerType> selectAllSingerTypes();

    // 获取所有地区
    @Select("select * from region order by region_id")
    List<Region> selectAllRegions();


}
