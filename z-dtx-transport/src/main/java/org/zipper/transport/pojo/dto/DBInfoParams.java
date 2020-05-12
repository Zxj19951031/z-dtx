package org.zipper.transport.pojo.dto;

import lombok.Data;

/**
 * @author zhuxj
 * @since 2020/5/12
 */
@Data
public class DBInfoParams {
    private String schema;
    private String catalog;
    private String table;
}
