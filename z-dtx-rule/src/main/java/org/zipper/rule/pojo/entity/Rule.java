package org.zipper.rule.pojo.entity;

import lombok.Builder;
import lombok.Data;
import org.zipper.common.pojo.BaseEntity;

import java.util.Date;

@Data
@Builder
public class Rule extends BaseEntity {
    private String name;
    private String expression;
    private String var1;
}
