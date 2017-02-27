package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by wangshuqiang on 2017/2/24.
 * 文件操作工具
 */
public class FileUtils {

    /**
     * read from file and convert to byte array
     */
    public static byte[] readFileIntoBytes(String fileName) {
        return readFileIntoBytes(new File(fileName));
    }


    /**
     * read from file and convert to byte array
     */
    public static byte[] readFileIntoBytes(File file) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        FileChannel channel = inputStream.getChannel();
        ByteBuffer buffer;
        try {
            buffer = ByteBuffer.allocate((int) channel.size());
            while (channel.read(buffer) > 0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (channel != null) channel.close();
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buffer.array();
    }
}
