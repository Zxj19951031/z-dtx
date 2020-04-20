package org.zipper.db.pojo.dto;

import lombok.Data;
import org.zipper.enums.DBType;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
public class DBDeleteParams {
    private List<DBDeleteRow> rows;

}

