package com.clock.controller;

import java.io.File;
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
import com.utils.Dom4jUtil;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

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
        try {
            Dom4jUtil.init();
        } catch (Exception e) {
            LOGGER.error("fall to init config,shutdown", e);
            System.exit(0);
        }
        try (AbstractApplicationContext aac = new ClassPathXmlApplicationContext("applicationContext.xml")) {
            String simpleName = GameController.class.getSimpleName();
            String beanId = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            GameController.INSTANCE = (GameController)aac.getBean(beanId);
        } catch (Exception e) {
            LOGGER.error("fail to create controller,shutdown", e);
            System.exit(0);
        }
    }

    /**
     * 由main方法调用
     * 业务逻辑的总入口
     */
    public void start() {
        LOGGER.info("clock start...");
        this.view.start();
    }

    /**
     * 创建并返回BGM文件列表
     * view会依序循环播放列表中的音乐
     * 要么为null，要么size()>0。不准出现null != list && size()==0的情况
     * 若设定为需要bgm，则null是严重错误，view检测到这种情况会将程序强制退出
     * 列表中的文件均是实际存在的.mp3文件
     * @return List<File>, BGM文件列表
     */
    @Override
    public List<File> createBgmList() {
        return this.model.createMusicList();
    }

    /**
     * 返回图片列表
     * 该列表中存储了图片相对于根目录的路径，这些图片是view展现的图片的全集
     * 若设返回值为list
     * null == list || list.size() == 0是严重错误，view检测到这种情况会将程序强制退出
     * @return List<String>, 图片路径列表
     */
    @Override
    public List<String> createImagePathList() {
        return this.model.createImagePathList();
    }
}
