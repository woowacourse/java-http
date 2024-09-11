package org.apache.coyote.http11.file;

import org.apache.coyote.http11.component.FileExtension;

public record FileDetails(String fileName, FileExtension extension) {

    public FileDetails(String filePath) {
        this(filePath, FileExtension.HTML);
    }

    public String getFilePath() {
        return fileName + extension.getExtension();
    }
}
