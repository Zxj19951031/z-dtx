package org.zipper.db.pojo.dto;

import org.zipper.db.enums.DBType;
import lombok.Data;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
public class DBDeleteParams {
    private DBType type;
    private List<Integer> ids;

}
