package org.zipper.transport.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
public class DBDeleteParams {
    private List<DBDeleteRow> rows;

}

