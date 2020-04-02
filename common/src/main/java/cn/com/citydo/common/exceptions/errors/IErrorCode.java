package cn.com.citydo.common.exceptions.errors;


import cn.com.citydo.common.enums.IEnum;

public interface IErrorCode extends IEnum<Integer, String> {


    default Integer getCode() {
        return getKey();
    }

    default String getDescription() {
        return getValue();
    }

    String toString();
}
