package org.zipper.common.exceptions.errors;


import org.zipper.common.enums.IEnum;

public interface IErrorCode extends IEnum<Integer, String> {


    default Integer getCode() {
        return getKey();
    }

    default String getDescription() {
        return getValue();
    }

    String toString();
}
