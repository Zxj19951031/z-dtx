package org.zipper.db.pojo.dto;

import lombok.Data;
import org.zipper.db.enums.DBType;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
public class DBDeleteParams {
    private DBType type;
    private List<Integer> ids;

    public void setType(int val) {
        this.type = DBType.get(val);
    }
}
