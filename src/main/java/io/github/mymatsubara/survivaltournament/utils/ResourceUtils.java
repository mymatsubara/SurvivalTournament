package io.github.mymatsubara.survivaltournament.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
    public String read(String resourcePath) throws IOException {
        InputStream in = getResourceAsStream(resourcePath);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = in.read(buffer) ) >= 0) {
            output.write(buffer, 0, length);
        }
        in.close();
        String result = output.toString("UTF-8");
        return result;
    }

    public InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
}
