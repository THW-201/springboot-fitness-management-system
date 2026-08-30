package com.fitness.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileUtil {


    /**
     * 将网络链接加载为 Base64
     */
    public static String loadMediaAsBase64(String imageUrl) {
        try {
            if (!imageUrl.startsWith("http")) {
                throw new RuntimeException("仅支持网络链接 URL");
            }
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int code = conn.getResponseCode();
            if (code != 200) {
                throw new IOException("HTTP 响应码: " + code);
            }

            try (InputStream inputStream = conn.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return Base64.getEncoder().encodeToString(outputStream.toByteArray());
            }
        } catch (Exception e) {
            throw new RuntimeException("链接转换 Base64 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 对 URL 中的中文部分进行编码
     */
    public static String encodeChineseInUrl(String url) {
        try {
            StringBuilder sb = new StringBuilder();
            for (char c : url.toCharArray()) {
                if (c > 127) { // 中文或特殊字符
                    sb.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("URL 编码失败", e);
        }
    }
}
