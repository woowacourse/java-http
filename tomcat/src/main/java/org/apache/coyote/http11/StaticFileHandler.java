package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler {
    
    public String readFile(String filePath) throws IOException {
        try {
            var resourceUrl = getClass().getResource("/static" + filePath);
            if (resourceUrl == null) {
                return null;
            }
            
            Path path = Paths.get(resourceUrl.getPath());
            return String.join("\n", Files.readAllLines(path));
        } catch (IOException e) {
            throw new IOException("파일을 읽을 수 없습니다: " + filePath, e);
        }
    }
    
    public boolean exists(String filePath) {
        return getClass().getResource("/static" + filePath) != null;
    }
    
    public String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html;charset=utf-8";
        } else if (filePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        } else if (filePath.endsWith(".json")) {
            return "application/json;charset=utf-8";
        }
        return "text/plain;charset=utf-8";
    }
}
