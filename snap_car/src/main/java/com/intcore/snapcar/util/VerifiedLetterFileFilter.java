package com.intcore.snapcar.util;

import java.io.File;
import java.io.FileFilter;

public class VerifiedLetterFileFilter implements FileFilter {

    private final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg", "pdf"};

    public VerifiedLetterFileFilter() {

    }

    public boolean accept(File file) {
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}