// File: FileBackup.java
package common;

import java.io.Serializable;

public class FileBackup implements Serializable {
    private String fileName;
    private String fileContent;

    public FileBackup(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }
}

