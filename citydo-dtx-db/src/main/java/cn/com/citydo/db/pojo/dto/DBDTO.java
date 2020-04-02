package cn.com.citydo.db.pojo.dto;

import cn.com.citydo.db.enums.DBType;
import lombok.Data;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
public class DBDTO {
    private Integer id;
    private String dbName;
    private DBType dbType;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String connType;

    public void setDbType(int val) {
        this.dbType = DBType.get(val);
    }
}
