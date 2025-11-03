package com.vetc.rule.core.utils;

import java.nio.charset.StandardCharsets;

public class StringUtils {
    public static String getStringFitNumberByte(String input, int numberByte) {
        String subStr = input;
        byte[] byteArray = input.getBytes(StandardCharsets.UTF_8);

        if (byteArray.length > numberByte) {
            // Điều chỉnh chuỗi con để phù hợp với giới hạn byte
            subStr = new String(byteArray, 0, numberByte, StandardCharsets.UTF_8);
        }

        return subStr;
    }
}
