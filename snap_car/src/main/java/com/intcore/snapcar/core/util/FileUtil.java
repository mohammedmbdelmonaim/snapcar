package com.intcore.snapcar.core.util;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import android.webkit.URLUtil;

import java.io.File;
import java.lang.annotation.Retention;

import static com.intcore.snapcar.core.util.FileUtil.FileType.AUDIO;
import static com.intcore.snapcar.core.util.FileUtil.FileType.DOCUMENT;
import static com.intcore.snapcar.core.util.FileUtil.FileType.IMAGE;
import static com.intcore.snapcar.core.util.FileUtil.FileType.UNKNOWN;
import static com.intcore.snapcar.core.util.FileUtil.FileType.VIDEO;
import static com.intcore.snapcar.core.util.FileUtil.FileType.ZIP;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class FileUtil {

    private static final int NOT_FOUND = -1;
    private static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';

    @Retention(SOURCE)
    @IntDef({UNKNOWN, AUDIO, VIDEO, DOCUMENT, ZIP, IMAGE})
    public @interface FileType {
        int UNKNOWN = -1;
        int AUDIO = 0;
        int VIDEO = 1;
        int DOCUMENT = 2;
        int ZIP = 3;
        int IMAGE = 4;
    }

    @FileType
    public static int getFileType(@NonNull String filename) {
        Preconditions.checkNonNull(filename);

        int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return UNKNOWN;
        } else {
            return getTypeFromExtension(filename);
        }
    }

    public static Intent getFileViewerIntent(@NonNull String path) {
        if (URLUtil.isNetworkUrl(path)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        } else {
            String extension = getExtension(path);
            Uri uri = Uri.fromFile(new File(path));
            return appropriateIntent(uri, extension);
        }
    }

    private static Intent appropriateIntent(Uri uri, String extension) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (extension.equalsIgnoreCase("pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (extension.equalsIgnoreCase("zip") || extension.equalsIgnoreCase("rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (extension.equalsIgnoreCase("rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (extension.equalsIgnoreCase("wav") || extension.equalsIgnoreCase("mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (extension.equalsIgnoreCase("gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (extension.equalsIgnoreCase("txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (extension.equalsIgnoreCase("3gp") || extension.equalsIgnoreCase("mpg") || extension.equalsIgnoreCase("mpeg") ||
                extension.equalsIgnoreCase("mpe") || extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private static int indexOfExtension(String filename) {
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = filename.lastIndexOf(UNIX_SEPARATOR);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    public static String getExtension(final String filename) {
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    @FileType
    private static int getTypeFromExtension(String fileName) {
        String extension = getExtension(fileName);

        if (isAudioExtension(extension)) {
            return FileType.AUDIO;
        } else if (isDocumentExtension(extension)) {
            return FileType.DOCUMENT;
        } else if (isCompressedFileExtension(extension)) {
            return FileType.ZIP;
        } else if (isVideoExtension(extension)) {
            return FileType.VIDEO;
        } else if (isImageExtension(extension)) {
            return FileType.IMAGE;
        } else {
            return FileType.UNKNOWN;
        }
    }

    public static boolean isAudioExtension(String extension) {
        return extension.equalsIgnoreCase("mp3")
                || extension.equalsIgnoreCase("amr")
                || extension.equalsIgnoreCase("aac")
                || extension.equalsIgnoreCase("ogg")
                || extension.equalsIgnoreCase("wav")
                || extension.equalsIgnoreCase("m4a")
                || extension.equalsIgnoreCase("flac")
                || extension.equalsIgnoreCase("mkv")
                || extension.equalsIgnoreCase("mid")
                || extension.equalsIgnoreCase("3gpp")
                || extension.equalsIgnoreCase("3gp")
                || extension.equalsIgnoreCase("xmf")
                || extension.equalsIgnoreCase("mxmf")
                || extension.equalsIgnoreCase("rtttl")
                || extension.equalsIgnoreCase("rtx")
                || extension.equalsIgnoreCase("bin")
                || extension.equalsIgnoreCase("ota")
                || extension.equalsIgnoreCase("imy");
    }

    public static boolean isVideoExtension(String extension) {
        return extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("avi")
                || extension.equalsIgnoreCase("mpg")
                || extension.equalsIgnoreCase("3gp")
                || extension.equalsIgnoreCase("3gpp")
                || extension.equalsIgnoreCase("ts")
                || extension.equalsIgnoreCase("AAC")
                || extension.equalsIgnoreCase("webm")
                || extension.equalsIgnoreCase("mkv");
    }

    public static boolean isCompressedFileExtension(String extension) {
        return extension.equalsIgnoreCase("rar")
                || extension.equalsIgnoreCase("zip")
                || extension.equalsIgnoreCase("gz")
                || extension.equalsIgnoreCase("gzip");
    }

    public static boolean isDocumentExtension(String extension) {
        return extension.equalsIgnoreCase("txt")
                || extension.equalsIgnoreCase("doc")
                || extension.equalsIgnoreCase("ods")
                || extension.equalsIgnoreCase("rtf")
                || extension.equalsIgnoreCase("odt")
                || extension.equalsIgnoreCase("pptx")
                || extension.equalsIgnoreCase("xlsx")
                || extension.equalsIgnoreCase("docx")
                || extension.equalsIgnoreCase("pdf");
    }

    public static boolean isImageExtension(String extension) {
        return extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("gif")
                || extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("bmp")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("webp");
    }

}
