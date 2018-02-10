package com.clock.dao;

import java.io.File;
import java.util.List;

/**
 * 与view绑定的controller需实现本接口
 */
public interface DaoViewController {

    /**
     * 创建并返回BGM文件列表
     * view会依序循环播放列表中的音乐
     * 要么为null，要么size()>0。不准出现null != list && size()==0的情况
     * 若设定为需要bgm，则null是严重错误，view检测到这种情况会将程序强制退出
     * 列表中的文件均是实际存在的.mp3文件
     * @return List<File>, BGM文件列表
     */
    List<File> createBgmList();

    /**
     * 返回图片列表
     * 该列表中存储了图片相对于根目录的路径，这些图片是view展现的图片的全集
     * 若设返回值为list
     * null == list || list.size() == 0是严重错误，view检测到这种情况会将程序强制退出
     * @return List<String>, 图片路径列表
     */
    List<String> createImagePathList();
}
