package org.zipper.transport.enums;

import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;

/**
 * 传输任务调度状态
 *
 * @author zhuxj
 * @since 2020/08/28
 */
public enum TransportScheduleStatus {

    /**
     * 未注册/未调度
     */
    CANCELED(0, "已停止"),
    /**
     * 已注册/调度中
     */
    REGISTERED(1, "调度中"),

    ;
    private final int type;
    private final String desc;

    TransportScheduleStatus(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static TransportScheduleStatus get(int type) {
        for (TransportScheduleStatus value : TransportScheduleStatus.values()) {
            if (value.getType() == type) {
                return value;
            }
        }
        throw HelperException.newException(ErrorCode.PARAMETER_ERROR, "不支持的调度类型值：" + type);
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
