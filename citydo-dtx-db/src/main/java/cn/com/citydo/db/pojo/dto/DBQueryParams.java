package cn.com.citydo.db.pojo.dto;

import cn.com.citydo.db.enums.DBType;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhuxj
 * @since 2020/4/2
 */
@Data
@Builder
public class DBQueryParams {
    private String dbName;
    private Integer dbType;
}
