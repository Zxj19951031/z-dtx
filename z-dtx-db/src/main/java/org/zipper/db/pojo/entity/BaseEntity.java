package org.zipper.db.pojo.entity;


import org.zipper.common.enums.Status;
import lombok.Data;

import java.util.Date;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
public abstract class BaseEntity {
    private Integer id;
    private Date createTime;
    private Date updateTime;
    private Integer status;

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatus(Status status){
        this.status = status.getKey();
    }

}
