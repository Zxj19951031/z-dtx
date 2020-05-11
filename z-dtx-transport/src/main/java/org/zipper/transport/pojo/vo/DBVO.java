package org.zipper.transport.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.zipper.transport.enums.DBType;

import java.util.Date;

@Data
public class DBVO {
    private Integer id;
    private String dbName;
    private Integer dbType;
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public String getDbTypeStr() {
        return DBType.get(dbType).name();
    }


}