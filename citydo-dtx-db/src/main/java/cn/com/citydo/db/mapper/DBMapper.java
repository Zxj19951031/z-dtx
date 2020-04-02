package cn.com.citydo.db.mapper;

import cn.com.citydo.db.pojo.dto.DBQueryParams;
import cn.com.citydo.db.pojo.entity.MySqlDB;
import cn.com.citydo.db.pojo.entity.OracleDB;
import cn.com.citydo.db.pojo.vo.DBVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface DBMapper {

    @Insert("insert into tb_db_mysql(db_name,host,port,user,password,create_time,update_time,status)" +
            "values(#{r.dbName},#{r.host},#{r.port},#{r.user},#{r.password},#{r.createTime},#{r.updateTime},#{r.status})")
    int insertOneMysql(@Param("r") MySqlDB record);

    @Insert("insert into tb_db_oracle(db_name,host,port,user,password,create_time,update_time,status,conn_type)" +
            "values(#{r.dbName},#{r.host},#{r.port},#{r.user},#{r.password},#{r.createTime},#{r.updateTime},#{r.status},#{r.connType)")
    int insertOneOracle(@Param("r") OracleDB dataBase);

    @Select({"<script>",
            "select id,db_name,db_type,host,create_time,update_time from(",
            "select id,db_name,1 as db_type,host,create_time,update_time from tb_db_mysql where status = 0",
            "union all",
            "select id,db_name,2 as db_type,host,create_time,update_time from tb_db_oracle where status = 0",
            ") as t",
            "<where>",
            "<if test=\"p.dbType!=null and p.dbType!=''\">",
            "t.db_type = #{p.dbType}",
            "</if>",
            "<if test=\"p.dbName!=null and p.dbName!=''\">",
            "t.db_name = #{p.dbName}",
            "</if>",
            "</where>",
            "</script>"})
    List<DBVO> selectUnionAll(@Param("p") DBQueryParams params);

    @Update({"<script>",
            "update tb_db_mysql set update_time = #{r.updateTime} ",
            "where id = #{r.id} ",
            "<if test=\"r.dbName!=null and r.dbName!=''\">",
            ",db_name = #{r.dbName}",
            "</if>",
            "<if test=\"r.host!=null and r.host!=''\">",
            ",host = #{r.host}",
            "</if>",
            "<if test=\"r.port!=null and r.port>0\">",
            ",port = #{r.port}",
            "</if>",
            "<if test=\"r.user!=null and r.user!=''\">",
            ",user = #{r.user}",
            "</if>",
            "<if test=\"r.password!=null and r.password!=''\">",
            ",password = #{r.password}",
            "</if>",
            "<,script>"})
    int updateOneMysql(@Param("r") MySqlDB record);

    @Update({"<script>",
            "update tb_db_oracle set update_time = #{r.updateTime} ",
            "where id = #{r.id} ",
            "<if test=\"r.dbName!=null and r.dbName!=''\">",
            ",db_name = #{r.dbName}",
            "</if>",
            "<if test=\"r.host!=null and r.host!=''\">",
            ",host = #{r.host}",
            "</if>",
            "<if test=\"r.port!=null and r.port>0\">",
            ",port = #{r.port}",
            "</if>",
            "<if test=\"r.user!=null and r.user!=''\">",
            ",user = #{r.user}",
            "</if>",
            "<if test=\"r.password!=null and r.password!=''\">",
            ",password = #{r.password}",
            "</if>",
            "<if test=\"r.connType!=null and r.connType!=''\">",
            ",conn_type = #{r.connType}",
            "</if>",
            "<,script>"})
    int updateOneOracle(@Param("r") OracleDB record);
}
