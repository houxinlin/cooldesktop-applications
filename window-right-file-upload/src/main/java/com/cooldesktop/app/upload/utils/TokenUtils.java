package com.cooldesktop.app.upload.utils;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TokenUtils {
    public static String getToken() {
        try {
            Path path = Paths.get(FileUtils.getConfigWorkPath(), "right-upload-token.cfg");
            if (Files.exists(path)) {
                String token = new String(Files.readAllBytes(path));
                if (StringUtils.hasText(token)) return token;
            }
            String token = generatorToken();
            Files.write(path, token.getBytes(StandardCharsets.UTF_8));
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String generatorToken() {
        String str = "qwertyuiopasdfghjklzxcvbnm7894561230WERTYUIOPASDFGHJKLZXCVBNM";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            stringBuilder.append(str.charAt(random.nextInt(str.length() - 1)));
        }
        return stringBuilder.toString();
    }


}
