package org.zipper.transport.enums;

import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;

/**
 * oracle的连接类型
 */
public enum OracleConnType {
    SID, TNS, SERVICE_NAME;

    public static OracleConnType get(String connType){
        for (OracleConnType value : OracleConnType.values()) {
            if(value.name().equals(connType.toUpperCase())){
                return value;
            }
        }
        throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                String.format("不支持的OracleConnType枚举值:%s，请联系管理员", connType));
    }
}
