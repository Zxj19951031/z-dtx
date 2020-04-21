package org.zipper.rule.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.zipper.rule.pojo.dto.RuleDTO;
import org.zipper.rule.pojo.dto.RuleQueryParams;
import org.zipper.rule.pojo.entity.Rule;
import org.zipper.rule.pojo.vo.RuleVO;

import java.util.List;

public interface RuleMapper {

    @Insert("insert into tb_rule(name,expression,create_time,update_time,status,var1)" +
            "values(#{r.name},#{r.expression},#{r.createTime},#{r.updateTime},#{r.status},#{r.var1})")
    int insertOne(@Param("r") Rule rule);

    @Select({
            "<script>",
            "select id,name,expression,create_time,update_time from tb_rule",
            "<where>",
            "status = 0",
            "<if test=\"p.name!=null and p.name!=''\">",
            "and name like concat('%',#{p.name},'%')",
            "</if>",
            "</where>",
            "</script>"
    })
    List<RuleVO> query(@Param("p") RuleQueryParams params);

    @Select("select id,name,expression,create_time,update_time,var1 from tb_rule where status = 0 and id = #{value}")
    Rule selectOne(Integer id);

    @Update({
            "<script>",
            "update tb_rule set update_time = now()",
            "<if test=\"d.name!=null and d.name!=''\">",
            ", name = #{d.name}",
            "</if>",
            "<if test=\"d.expression!=null and d.expression!=''\">",
            ", expression = #{d.expression}",
            "</if>",
            "<if test=\"d.var1!=null and d.var1!=''\">",
            ", var1 = #{d.var1}",
            "</if>",
            "where id = #{d.id}",
            "</script>",
    })
    int updateOne(@Param("d") RuleDTO dto);

    @Update({
            "<script>",
            "update tb_rule set update_time = now(),status = 1 ",
            "<where> id in",
            " <foreach item=\"item\" collection=\"list\" separator=\",\" open=\"(\" close=\")\">",
            " #{item}",
            " </foreach>",
            "</where>",
            "</script>"
    })
    int deleteBatch(@Param("list") List<Integer> ids);
}
