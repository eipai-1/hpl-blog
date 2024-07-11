package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/6 12:50
 */
@Getter
public enum OperateArticleEnum {

    EMPTY(0, "") {
        @Override
        public int getDbStatCode() {
            return 0;
        }
    },
    OFFICAL(1, "官方") {
        @Override
        public int getDbStatCode() {
            return OfficalStateEnum.OFFICAL.getCode();
        }
    },
    CANCEL_OFFICAL(2, "非官方"){
        @Override
        public int getDbStatCode() {
            return OfficalStateEnum.NOT_OFFICAL.getCode();
        }
    },
    TOPPING(3, "置顶"){
        @Override
        public int getDbStatCode() {
            return ToppingStateEnum.TOPPING.getCode();
        }
    },
    CANCEL_TOPPING(4, "不置顶"){
        @Override
        public int getDbStatCode() {
            return ToppingStateEnum.NOT_TOPPING.getCode();
        }
    },
    CREAM(5, "加精"){
        @Override
        public int getDbStatCode() {
            return CreamStateEnum.CREAM.getCode();
        }
    },
    CANCEL_CREAM(6, "不加精"){
        @Override
        public int getDbStatCode() {
            return CreamStateEnum.NOT_CREAM.getCode();
        }
    };

    OperateArticleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static OperateArticleEnum fromCode(Integer code) {
        for (OperateArticleEnum value : OperateArticleEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return OperateArticleEnum.OFFICAL;
    }

    public abstract int getDbStatCode();
}
