package cn.yiheng.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.*;
import java.util.Iterator;

/**
 * @author Yiheng
 * @create 4/15/2019
 * @since 1.0.0
 */
public class ImgUtils {

    static Logger logger = LoggerFactory.getLogger(ImgUtils.class);

    public static Dimension getResolution(File imgFile) {
        logger.info("start parse img: "+imgFile.getAbsolutePath());
        try (ImageInputStream iis = ImageIO.createImageInputStream(imgFile)) {
            ImageReader imageReader = ImageIO.getImageReadersBySuffix("jpg").next();
            imageReader.setInput(iis);
            return new Dimension(imageReader.getWidth(0), imageReader.getHeight(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否JPG文件
     *
     * @param file
     * @return
     */
    public static boolean isJpgFile(File file) {
        if (file.isDirectory()) {
            return false;
        }

        byte[] bytes = new byte[4];
        try (FileInputStream in = new FileInputStream(file)) {
            int len = readAvailable(in, bytes, 0, 4);
            return 4 == len && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF && (bytes[3] & 0xFF) == 0xE0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否可读取指定长度信息
     *
     * @param in
     * @param b   要读取的字节数组
     * @param off 开始位置偏移量
     * @param len 读取最大长度
     * @return 读取到长度
     * @throws IOException
     */
    public static int readAvailable(InputStream in, byte b[], int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        int wpos = off;
        while (len > 0) {
            int count = in.read(b, wpos, len);
            if (count < 0) {
                break;
            }
            wpos += count;
            len -= count;
        }
        return wpos - off;
    }
}
