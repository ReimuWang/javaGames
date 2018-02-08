package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

/**
 * 静态工具类，将各种素材数据加载入内存
 */
public class SourceDataGetUtil {

    public static File loadFile(String path) throws URISyntaxException
                                                  , FileNotFoundException {
        SourceDataGetUtil.check(path);
        URL url = SourceDataGetUtil.loadURL(path);
        return new File(url.toURI());
    }

    public static BufferedImage loadBufferedImage(String path) throws IOException {
        SourceDataGetUtil.check(path);
        URL url = SourceDataGetUtil.loadURL(path);
        return ImageIO.read(url);
    }

    public static URL loadURL(String path) throws FileNotFoundException {
        SourceDataGetUtil.check(path);
        URL url = SourceDataGetUtil.class.getClassLoader()
                                   .getResource(path);
        if (null == url)
            throw new FileNotFoundException("get none url by path=" + path); 
        return url;
    }

    private static void check(String path) {
        if (StringUtils.isBlank(path))
            throw new NullPointerException("path is blank");
    }

    private SourceDataGetUtil() {}
}
