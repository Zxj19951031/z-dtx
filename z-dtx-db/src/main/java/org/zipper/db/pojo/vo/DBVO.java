package org.zipper.db.pojo.vo;

import org.zipper.db.enums.DBType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DBVO {
    private Integer id;
    private String dbName;
    private Integer dbType;
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getDbTypeStr(){
        return DBType.get(dbType).name();
    }



}
