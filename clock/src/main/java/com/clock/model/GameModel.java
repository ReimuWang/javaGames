package com.clock.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import utils.Dom4jUtil;
import utils.SourceDataGetUtil;

/**
 * 包含所有model逻辑代码
 */
@Service
public class GameModel {

    /**
     * int, 加载入内存的最大音乐数
     */
    private static int MAX_BGM_COUNT;

    /**
     * List<String>, 指定的bgm列表(其内容为文件的简单名称)
     */
    private static List<String> SONG_LIST;

    /**
     * int, 加载入内存的最大图片数
     */
    private static int MAX_IMAGE_COUNT;
    
    /**
     * List<String>, 指定的图片列表(其内容为文件的简单名称)
     */
    private static List<String> IMAGE_LIST;

    /**
     * String, 存放音乐素材的文件夹
     */
    private static final String BGM_FOLDER_PATH = "material/sound/bgm/";

    /**
     * String[], 有效的音乐格式数组
     */
    private static final String[] SOUND_SUFFIX_ARRAY = new String[] {"mp3"};

    /**
     * String, 存放图片素材的文件夹
     */
    private static final String IMAGE_FOLDER_PATH = "material/images/bg/";

    /**
     * String[], 有效的图片格式数组
     */
    private static final String[] IMAGE_SUFFIX_ARRAY = new String[] {"jpg", "jpeg", "png"};

    private static final Logger LOGGER = LoggerFactory.getLogger(GameModel.class);

    static {
        try {
            // MAX_BGM_COUNT
            String maxBgmCountStr = Dom4jUtil.getAttribute("model", "bgm", "maxBgmCount");
            int maxBgmCount = Integer.parseInt(maxBgmCountStr);
            if (maxBgmCount >= 0)
                GameModel.MAX_BGM_COUNT = maxBgmCount;
            else {
                LOGGER.error("model.bgm.maxBgmCount illegal,shutdown");
                System.exit(0);
            }
            // SONG_LIST
            GameModel.SONG_LIST = Dom4jUtil.getTextList("model", "bgm", "song");
            // MAX_IMAGE_COUNT
            String maxImageCountStr = Dom4jUtil.getAttribute("model", "image", "maxImageCount");
            int maxImageCount = Integer.parseInt(maxImageCountStr);
            if (maxImageCount >= 1)
                GameModel.MAX_IMAGE_COUNT = maxImageCount;
            else {
                LOGGER.error("model.image.maxImageCount illegal,shutdown");
                System.exit(0);
            }
            // IMAGE_LIST
            GameModel.IMAGE_LIST = Dom4jUtil.getTextList("model", "image", "picture");
        } catch (Exception e) {
            LOGGER.error("fail to init GameModel static", e);
            System.exit(0);
        }
    }

    /**
     * 创建bgm文件列表。
     * @return List<File>, 为空或出错则返回null
     */
    public List<File> createMusicList() {
        try {
            List<File> list = this.createFileList(GameModel.BGM_FOLDER_PATH, GameModel.SONG_LIST, GameModel.SOUND_SUFFIX_ARRAY, GameModel.MAX_BGM_COUNT);
            if (null != list && list.size() == 0) return null;
            return list;
        } catch (Exception e) {
            LOGGER.error("fail to create bgm list,shutdown", e);
            System.exit(0);
            return null;
        }
    }

    /**
     * 创建图片路径列表
     * 出错或未取到合法图片则返回null
     * @return List<String>, 图片路径列表
     */
    public List<String> createImagePathList() {
        try {
            List<File> list = this.createFileList(GameModel.IMAGE_FOLDER_PATH, GameModel.IMAGE_LIST, GameModel.IMAGE_SUFFIX_ARRAY, GameModel.MAX_IMAGE_COUNT);
            if (null != list && list.size() == 0) return null;
            List<String> strList = new ArrayList<String>(list.size());
            for (File f : list) strList.add(GameModel.IMAGE_FOLDER_PATH + f.getName());
            return strList;
        } catch (Exception e) {
            LOGGER.error("fail to create image list,shutdown", e);
            System.exit(0);
            return null;
        }
    }

    /**
     * 创建文件列表。步骤如下：
     * 1. 获得素材集：
     *     1.1.若指定文件简单名称(targetList)，则只按顺序读取指定的文件(不存在的会被自动过滤)
     *     1.2.否则随机取folderPath下所有文件(不递归)
     * 
     * 第一步结束的素材集是List<File>，随后会过滤掉不合法的数据：
     * 不是文件
     * 不符合suffixArray格式规范
     * 
     * 2. 从素材集中从头依序取前maxCount个素材返回(若不足则全部返回)
     * @param folderPath String, 文件所在目录相对根目录的路径
     * @param targetList List<String>, 指定的文件列表
     * @param suffixArray String[], 后缀格式规范
     * @param maxCount int, 最大可返回的文件数
     * @return List<File>, 文件列表，为空则返回空列表
     * @throws URISyntaxException 
     * @throws FileNotFoundException 
     */
    private List<File> createFileList(String folderPath, List<String> targetList, String[] suffixArray, int maxCount) throws FileNotFoundException, URISyntaxException {
        List<File> sourceList = new ArrayList<File>();
        // 获得指定文件
        for (String target : targetList) {
            File temp = SourceDataGetUtil.loadFile(folderPath + target);
            if (temp.exists()) sourceList.add(temp);
        }
        if (sourceList.size() == 0) {
            // 随机取得文件夹下的所有文件
            File folder = SourceDataGetUtil.loadFile(folderPath);
            sourceList = Arrays.asList(folder.listFiles());
            Collections.shuffle(sourceList);
        }
        if (sourceList.size() == 0) return null;
        // 过滤获取合法的文件
        List<File> dataList = new ArrayList<File>();
        for (File f : sourceList) {
            if (!f.isFile()) continue;
            if (!this.checkSuffix(f.getName(), suffixArray)) continue;
            if (dataList.size() >= maxCount) break;
            dataList.add(f);
        }
        return dataList;
    }

    /**
     * 检查文件格式是否正确
     * 即检查文件名的后缀(会将文件名先统一转为小写)是否合法
     * @param name String, 文件名
     * @param suffixArray String[], 合法的后缀数组
     * @return boolean, true--合法,false--非法
     */
    private boolean checkSuffix(String name, String[] suffixArray) {
        name = name.toLowerCase();
        boolean ifLegal = false;
        for (String suffix : suffixArray) {
            if (!name.endsWith(suffix)) continue;
            ifLegal = true;
            break;
        }
        return ifLegal;
    }
}
