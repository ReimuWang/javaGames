package com.clock.controller;

import java.awt.Image;
import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;

import com.clock.dao.DaoViewController;
import com.clock.model.GameModel;
import com.clock.view.GameView;

/**
 * 总controller
 * 包含controller的所有代码
 */
@Controller
public class GameController implements DaoViewController {

    /**
     * GameController, controller单例
     */
    public static GameController INSTANCE;

    private final static Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    /**
     * GameModel, 本controller实例所关联的model，为单例
     */
    @Autowired
    private GameModel model;

    /**
     * GameView, 本controller实例所关联的view，为单例
     */
    @Autowired
    private GameView view;

    static {
        try (AbstractApplicationContext aac = new ClassPathXmlApplicationContext("applicationContext.xml")) {
            String simpleName = GameController.class.getSimpleName();
            String beanId = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            GameController.INSTANCE = (GameController)aac.getBean(beanId);
        } catch (Exception e) {
            LOGGER.error("fail to create controller", e);
            System.exit(0);
        }
    }

    /**
     * 由main方法调用
     * 业务逻辑的总入口
     */
    public void start() {
        this.view.start();
    }

    /**
     * Calendar, 获得当前日期信息Calendar类
     * @return Calendar, 当前日期信息Calendar类
     */
    @Override
    public Calendar getNowCalendar() {
        return this.model.getNowCalendar();
    }

    /**
     * 创建并返回BGM文件列表
     * view会依序循环播放列表中的音乐
     * 要么为null，要么size()>0。不准出现null == list && size()==0的情况
     * 列表中的文件均是实际存在的.mp3文件
     * @return List<File>, BGM文件列表
     */
    @Override
    public List<File> createBgmList() {
        return this.model.createBgmMusicList();
    }

    @Override
    public List<Image> refreshClockImageList(List<Image> clockImageList) {
        return this.model.refreshClockImageList(clockImageList);
    }
}
