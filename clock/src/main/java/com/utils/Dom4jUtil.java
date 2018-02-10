package com.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 使用dom4j读取xml文件的静态工具类
 * 限制有2：
 * 1.只有一个配置文件
 * 2.配置文件中同一个元素下不存在同名的自元素
 */
public class Dom4jUtil {

    /**
     * String, 程序唯一配置文件的路径
     */
    private static final String CONFIG_PATH = "conf.xml";

    /**
     * Element, 配置文件根元素
     */
    private static Element ROOT;

    /**
     * 本类为静态工具类，不得创建实例
     */
    private Dom4jUtil() {}

    /**
     * 初始化配置文件信息
     * 本方法将得到配置文件的根元素，并存至Dom4jUtil.ROOT
     * @throws DocumentException
     * @throws FileNotFoundException
     * @throws URISyntaxException
     */
    public static void init() throws DocumentException, FileNotFoundException, URISyntaxException {
        SAXReader sReader = new SAXReader();
        File f = SourceDataGetUtil.loadFile(Dom4jUtil.CONFIG_PATH);
        Document document = sReader.read(f);
        Dom4jUtil.ROOT = document.getRootElement();
    }

    /**
     * 获得属性值
     * @param args String..., 若长度为n，则前n-1个参数为元素名(元素从根元素的下一级开始传起)
     *                                 最后一个参数为属性名
     * @return String, 属性值
     */
    public static String getAttribute(String... args) {
        Element e = Dom4jUtil.ROOT;
        for (int i = 0; i < args.length - 1; i++)
            e = e.element(args[i]);
        return e.attribute(args[args.length - 1]).getValue();
    }

    /**
     * 获取文本值列表
     * @param args String..., 若长度为n，则前n-1个参数为上层元素名(元素从根元素的下一级开始传起)
     *                                 最后一个参数为叶子元素(其内部应为文本值)，通常会有多个
     *                                 会按顺序取出它们后形成一个字符串列表
     * @return List<String>, 文本值列表
     */
    public static List<String> getTextList(String... args) {
        List<String> list = new ArrayList<String>();
        Element e = Dom4jUtil.ROOT;
        for (int i = 0; i < args.length - 1; i++)
            e = e.element(args[i]);
        @SuppressWarnings("unchecked")
        Iterator<Element> iterator = e.elementIterator(args[args.length - 1]);
        while (iterator.hasNext()) {
            Element temp = iterator.next();
            list.add(temp.getText());
        }
        return list;
    }
}
