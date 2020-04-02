package cn.com.citydo.common.enums;

public enum Status implements IEnum<Integer, String> {

    VALID
            (0, "有效的"),
    INVALID
            (1, "无效的");

    Status(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private int key;
    private String value;

    @Override
    public Integer getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
