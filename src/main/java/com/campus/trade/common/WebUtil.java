package com.campus.trade.common;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * URL 构造工具：自动跳过空参数并进行 UTF-8 编码。
 */
public final class WebUtil {

    private WebUtil() {
    }

    public static String url(String base, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(base);
        char separator = base.contains("?") ? '&' : '?';
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isBlank()) {
                continue;
            }
            builder.append(separator)
                    .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            separator = '&';
        }
        return builder.toString();
    }

    public static Map<String, String> params(String... keyValues) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
}
