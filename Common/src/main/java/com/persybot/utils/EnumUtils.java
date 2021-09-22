package com.persybot.utils;

import java.util.Arrays;

public class EnumUtils {
    public static boolean isInEnumIgnoreCase(Class<? extends Enum<?>> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants()).anyMatch(e -> e.name().equalsIgnoreCase(value));
    }

    public static boolean isInEnum(Class<? extends Enum<?>> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants()).anyMatch(e -> e.name().equals(value));
    }

    public static <T extends Enum<?>> T getEnumIgnoreCase(Class<T> anEnum, String identifier) {
        return Arrays.stream(anEnum.getEnumConstants())
                .filter(each -> each.name().equalsIgnoreCase(identifier))
                .findFirst()
                .orElse(null);
    }

}
