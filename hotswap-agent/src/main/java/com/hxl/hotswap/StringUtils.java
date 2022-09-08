package com.hxl.hotswap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StringUtils {
    public static Map<String, String> parseUrlArg(String arg) {
        if (arg == null) return Collections.emptyMap();
        String[] split = arg.split("&");
        if (split.length == 0) return Collections.emptyMap();
        Map<String, String> resultMap = new HashMap<>();
        for (String itemArg : split) {
            String[] item = itemArg.split("=");
            resultMap.put(item[0], item[1]);
        }
        return resultMap;
    }
}
