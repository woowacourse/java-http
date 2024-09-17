package org.apache.util;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileUtils {


    private FileUtils() {
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1);
    }

    public static String readFile(String fileName) {
        try {
            URL resource = FileUtils.class.getClassLoader().getResource(fileName);
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new UncheckedServletException("파일을 읽는 것에 실패했습니다");
        } catch (NullPointerException e) {
            throw new UncheckedServletException("존재하지 않는 파일입니다");
        }
    }
}
