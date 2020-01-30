package com.intcore.snapcar.util;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter
{
    File file;
    String path;
    private final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};

    /**
     *
     */
    public ImageFileFilter(File newfile)
    {
        this.file=newfile;
    }

    public ImageFileFilter(String newfile)
    {
        this.path=newfile;
    }

    public boolean accept(String file)
    {
        for (String extension : okFileExtensions)
        {
            if (file.toLowerCase().endsWith(extension))
            {
                return true ;
            }
        }
        return false;
    }


    public boolean accept(File file)
    {
        for (String extension : okFileExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }

}
