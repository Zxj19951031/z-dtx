package org.zipper.common.pojo;


import org.zipper.common.enums.Status;

import java.util.Date;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }
}
