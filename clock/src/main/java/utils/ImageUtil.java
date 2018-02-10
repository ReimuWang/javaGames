package utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 图片处理静态工具类
 */
public class ImageUtil {

    /**
     * 本类为静态工具类，不得创建实例
     */
    private ImageUtil() {}

    /**
     * 改变传入图片尺寸并返回改变后的新图
     * @param sourceImage BufferedImage, 源图片
     * @param newWidth int, 新的宽
     * @param newHeight int, 新的高
     * @return BufferedImage, 新图片
     */
    public static BufferedImage changeLength(BufferedImage sourceImage, int newWidth, int newHeight) {
        if (null == sourceImage) throw new NullPointerException("image is null");
        if (newWidth <= 0 || newHeight <= 0)
            throw new IllegalArgumentException("newWidth or newHeight is illegal");
        BufferedImage targetImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = targetImage.createGraphics();
        graphics.drawImage(sourceImage, 0, 0, newWidth, newHeight, null);
        return targetImage;
    }
}
