package com.hanhu.teamupbackend.model.enums;

/**
 * 队伍状态枚举
 *
 * @author hanhu
 */
public enum TeamStatusEnum {

    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    SECRET(2, "加密");

    private int value;

    private String text;


    /**
     * 构造函数  alt + insert  / alt + shift + 0
     * @param value
     * @param text
     */
    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    //get set 方法，enm不能用 @Data 注解
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }



}
