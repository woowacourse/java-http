package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.response.MimeType;

public class StaticFileHandler {
    
    public String readFile(String filePath) throws IOException {
        try {
            var resourceUrl = getClass().getResource("/static" + filePath);
            if (resourceUrl == null) {
                return null;
            }
            
            Path path = Paths.get(resourceUrl.getPath());
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new IOException("파일을 읽을 수 없습니다: " + filePath, e);
        }
    }
    
    public boolean exists(String filePath) {
        return getClass().getResource("/static" + filePath) != null;
    }
    
    public MimeType getContentType(String filePath) {
        final String extension = extractExtension(filePath);
        return MimeType.from(extension);
    }

    private String extractExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다: " + filePath);
        }
        return filePath.substring(lastDotIndex + 1);
    }
}
