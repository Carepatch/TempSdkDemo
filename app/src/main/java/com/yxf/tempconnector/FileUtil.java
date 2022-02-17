package com.yxf.tempconnector;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    /**
     *
     * @param filePath
     * @param bytes
     * @param append
     */
    public static void writeBytesToFile(String filePath, byte[] bytes, boolean append) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            fos = new FileOutputStream(filePath, append);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] getBytesByInputStream(InputStream inputStream) {
        try {
            byte[] buff = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length;
            while ((length = inputStream.read(buff)) != -1) {
                bos.write(buff, 0, length);
            }
            inputStream.close();
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
