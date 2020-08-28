package org.zipper.job.pojo.entity;

import lombok.Builder;
import lombok.Data;
import org.zipper.helper.web.entity.BaseEntity;

@Data
@Builder
public class Rule extends BaseEntity {
    private String name;
    private String expression;
    private String var1;
}
