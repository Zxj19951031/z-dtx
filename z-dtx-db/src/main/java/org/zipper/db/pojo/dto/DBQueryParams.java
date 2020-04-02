package org.zipper.db.pojo.dto;

import org.zipper.db.enums.DBType;
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