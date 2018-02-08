package com.clock.model;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import utils.SourceDataGetUtil;
import utils.Utils;

import com.clock.constants.Constants;

@Service
public class GameModel {

    private int bgmCount;

    public Calendar getNowCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 获得bgm文件列表。
     * 若指定音乐则按指定的个数，顺序返回
     * 反之取得bgm目录下的所有.mp3文件，而后乱序返回
     * @return List<File>, 为空或出错则返回null
     */
    public List<File> createBgmMusicList() {
        try {
            List<File> sortBgmList = this.sortBgmList();
            if (null != sortBgmList) {
                LOGGER.info("use bgm sort txt");
                return sortBgmList;
            }
            List<File> randomBgmList = this.randomBgmList();
            LOGGER.info("default random bgm,size=" + randomBgmList.size());
            return randomBgmList;
        } catch (Exception e) {
            LOGGER.error("fail to create bgm musicList", e);
            return null;
        }
    }

    public List<Image> refreshClockImageList(List<Image> clockImageList) {
        try {
            int count = 9;
            List<Image> list = new ArrayList<Image>(count);
            for (int i = 0; i < count; i++) {
                Image image = SourceDataGetUtil.loadBufferedImage("material/images/" + i + ".jpg");
                list.add(image);
            }
            return list;
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * 随机返回bgm文件夹下设定个数个.mp3的音乐
     * @return List<File>, 为空或出错则返回null
     */
    private List<File> randomBgmList() {
        List<File> fileList = null;
        try {
            fileList = new ArrayList<File>();
            File folder = SourceDataGetUtil.loadFile(Constants.BGM_PATH);
            List<File> allFile = Arrays.asList(folder.listFiles());
            Collections.shuffle(allFile);
            int counter = 0;
            for (File temp : allFile) {
                String tempName = temp.getName();
                if (!temp.isFile()) {
                    LOGGER.warn("bgm folder has illegal file:"
                                + tempName);
                    continue;
                }
                if (Constants.BGM_SORT_TXT.equals(tempName)) continue;
                if (!tempName.endsWith(Constants.SOUND_SUFFIX)) {
                    LOGGER.warn("bgm folder has illegal file:" + tempName);
                    continue;
                }
                fileList.add(temp);
                counter++;
                if (counter >= this.bgmCount) break;
            }
        } catch(Exception e) {
            LOGGER.error("fail to randomBgmList", e);
            fileList = null;
        }
        if (null != fileList && fileList.size() == 0) return null;
        return fileList;
    }

    /**
     * 获得bgm文件下指定好顺序及个数的bgm列表
     * 若文件不存在，文件为空，文件中没有合法的音乐，出错则返回null
     * @return List<File>
     */
    private List<File> sortBgmList() {
        List<File> fileList = null;
        try {
            File txtFile = SourceDataGetUtil.loadFile(Constants.BGM_PATH
                                                    + Constants.BGM_SORT_TXT);
            List<String> lineList = Utils.readFileByLine(txtFile);
            fileList = new ArrayList<File>();
            for (String line : lineList) {
                try {
                    if (StringUtils.isBlank(line)) continue;
                    if (!line.endsWith(Constants.SOUND_SUFFIX)) {
                        LOGGER.warn("bgm sort txt has illegal name=" + line);
                        continue;
                    }
                    File temp = SourceDataGetUtil.loadFile(Constants.BGM_PATH
                            + line);
                    fileList.add(temp);
                } catch (Exception e) {
                    LOGGER.warn("bgm sort txt has illegal name=" + line, e);
                }
            }
        } catch(Exception e) {
            LOGGER.error("fail to sortBgmList", e);
            fileList = null;
        }
        if (null != fileList && fileList.size() == 0) return null;
        return fileList;
    }

    private final static Logger LOGGER = LoggerFactory
            .getLogger(GameModel.class);

    @Value("#{config.model_bgmCount}")
    public void setBgmCount(int bgmCount) {
        this.bgmCount = bgmCount;
    }
}
