package cn.yiheng.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author Yiheng
 * @create 4/15/2019
 * @since 1.0.0
 */

public class PathUtils {

    static Logger logger = LoggerFactory.getLogger(PathUtils.class);
    /**
     * 将本地路径转为URL对象
     *
     * @param url
     * @return
     */
    public static String toExternalForm(String url) {
        logger.info("url is: " + url);
        URL resource = PathUtils.class.getResource(url);

        return null == resource ? null : resource.toExternalForm();
    }
}
