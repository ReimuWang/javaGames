package com.clock.dao;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * 包含view运行所需的一切后台数据
 */
public class DaoViewPojo {

    /**
     * Calendar, 本次显示的日期
     */
    private Calendar calendar;

    /**
     * List<File>, bgm列表
     * 启动时初始化一次，运行过程中不会修改
     * 要么为null，要么size()>0。不会出现null != list && size()==0的情况
     * 列表中的文件均是实际存在的.mp3文件
     */
    private List<File> bgmList;

    /**
     * List<String>, 钟表中全部图片相对根目录的路径列表
     * 启动时初始化一次，运行过程中不会修改
     * 要么为null，要么size()>0。不会出现null != list && size()==0的情况
     * 列表中的文件均是实际存在的图片文件
     */
    private List<String> imagePathList;

    /**
     * int, 底层图片在图片列表中的index
     */
    private int image0Index;

    /**
     * int, 上层图片在图片列表中的index
     */
    private int image1Index = 1;

    public Calendar getCalendar() {
        return calendar;
    }

    void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<File> getBgmList() {
        return bgmList;
    }

    public void setBgmList(List<File> bgmList) {
        this.bgmList = bgmList;
    }

    public List<String> getImagePathList() {
        return imagePathList;
    }

    public void setImagePathList(List<String> imagePathList) {
        this.imagePathList = imagePathList;
    }

    public int getImage0Index() {
        return image0Index;
    }

    public void setImage0Index(int image0Index) {
        this.image0Index = image0Index;
    }

    public int getImage1Index() {
        return image1Index;
    }

    public void setImage1Index(int image1Index) {
        this.image1Index = image1Index;
    }
}
