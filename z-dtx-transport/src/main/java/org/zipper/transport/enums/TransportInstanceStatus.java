package org.zipper.transport.enums;

import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;

/**
 * 传输任务调度状态
 *
 * @author zhuxj
 * @since 2020/08/28
 */
public enum TransportInstanceStatus {

    /**
     * 运行中
     */
    RUNNING(0, "运行中"),
    /**
     * 成功
     */
    SUCCESS(1, "成功"),
    /**
     * 失败
     */
    FAIL(2, "失败"),
    ;
    private final int type;
    private final String desc;

    TransportInstanceStatus(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static TransportInstanceStatus get(int type) {
        for (TransportInstanceStatus value : TransportInstanceStatus.values()) {
            if (value.getType() == type) {
                return value;
            }
        }
        throw HelperException.newException(ErrorCode.PARAMETER_ERROR, "不支持的调度实例类型值：" + type);
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
