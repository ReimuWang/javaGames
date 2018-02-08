package com.clock.dao;

import java.awt.Image;
import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * 包含view运行所需的一切后台数据
 */
public class DaoViewPojo {

//==================== 不可变信息 ====================
    private List<File> bgmMusicList;

//==================== 实时更新的信息 ====================
    private Calendar calendar;

    private List<Image> clockImageList;

    public Calendar getCalendar() {
        return calendar;
    }

    void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<File> getBgmMusicList() {
        return bgmMusicList;
    }

    void setBgmMusicList(List<File> bgmMusicList) {
        this.bgmMusicList = bgmMusicList;
    }

    public List<Image> getClockImageList() {
        return clockImageList;
    }

    public void setClockImageList(List<Image> clockImageList) {
        this.clockImageList = clockImageList;
    }
}
