package org.zipper.transport.pojo.dto;

import lombok.Data;
import org.zipper.transport.enums.DBType;

@Data
public class DBDeleteRow {

    private DBType type;
    private Integer id;

    public void setType(int val) {
        this.type = DBType.get(val);
    }

}
