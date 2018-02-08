package com.clock.dao;

import java.awt.Image;
import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * 与view绑定的controller需实现本接口
 */
public interface DaoViewController {

    /**
     * Calendar, 获得当前日期信息Calendar类
     * @return Calendar, 当前日期信息Calendar类
     */
    Calendar getNowCalendar();

    /**
     * 创建并返回BGM文件列表
     * view会依序循环播放列表中的音乐
     * 要么为null，要么size()>0。不准出现null == list && size()==0的情况
     * 列表中的文件均是实际存在的.mp3文件
     * @return List<File>, BGM文件列表
     */
    List<File> createBgmList();

    /**
     * 
     * @param clockImageList List<Image>
     * @return
     */
    List<Image> refreshClockImageList(List<Image> clockImageList);
}
