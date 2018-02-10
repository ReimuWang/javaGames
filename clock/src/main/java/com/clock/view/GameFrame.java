package com.clock.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clock.dao.DaoView;
import com.clock.view.module.Module;
import com.clock.view.module.ModuleClock;

import javazoom.jl.player.Player;
import utils.Dom4jUtil;

/**
 * 游戏窗体
 */
@SuppressWarnings("serial")
@Component
public class GameFrame extends Frame {

    /**
     * int, 标准图片边长
     * 程序核心静态字段
     * 窗口参数将基于本值计算
     * 上层图片与下层图片在拼接前也均会转为该值
     */
    public static int STANDARD_IMAGE_LENGTH;

    /**
     * String, 标题
     */
    private static String TITLE;

    /**
     * boolean, true--播放bgm,false--不播放bgm
     */
    private static boolean IF_PLAY_BGM;

    /**
     * long, 刷新间隔，单位为ms
     */
    private static int REPAINT_INTERVAL;

    /**
     * int, frame标题宽度
     */
    private static int TITLE_WIDTH;

    /**
     * int, 不可使用的最左侧的宽度
     */
    private static int L_WIDTH;

    /**
     * int, 不可使用的最右侧的宽度
     */
    private static int R_WIDTH;

    /**
     * int, 不可使用的最下侧的宽度
     */
    private static int D_WIDTH;

    /**
     * int, 边栏宽度
     */
    private static int SIDEBAR;

    /**
     * boolean, true--边栏在上部,false--边栏在下部
     */
    private static boolean IF_SIDEBAR_UP;

    /**
     * int, 组件间留白
     */
    private static int PADDING;

    private static final Logger LOGGER = LoggerFactory.getLogger(GameFrame.class);

    /**
     * DaoView, view层数据传输逻辑
     */
    @Autowired
    private DaoView dao;

    /**
     * ModuleClock, 本frame中的ModuleClock实例
     */
    private ModuleClock moduleClock;

    private class BGMplayer implements Runnable {

        /**
         * List<File>, 音乐列表
         */
        private List<File> musicList;

        /**
         * int, 当前播放的音乐在列表中的index
         */
        private int nowIndex;

        /**
         * 构造函数
         * @param musicList List<File>, 音乐列表
         */
        private BGMplayer(List<File> musicList) {
            this.musicList = musicList;
        }

        /**
         * 线程执行
         */
        @Override
        public void run() {
            try {
                while (true) {
                    File f = null;
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    Player player = null;
                    f = this.musicList.get(this.nowIndex);
                    fis = new FileInputStream(f);
                    bis = new BufferedInputStream(fis);
                    player = new Player(bis);
                    player.play();
                    player.close();
                    bis.close();
                    fis.close();
                    this.nowIndex++;
                    if (this.nowIndex >= this.musicList.size())
                        this.nowIndex = 0;
                }
            } catch(Exception e) {
                LOGGER.error("catch error when play bgm,shutdown", e);
                System.exit(0);
            }
        }
    }

    static {
        try {
            // 获得os类型
            String os = Dom4jUtil.getAttribute("view", "frame", "os");
            // STANDARD_IMAGE_LENGTH
            String standardImageLengthStr = Dom4jUtil.getAttribute("view", "frame", "standardImageLength");
            int standardImageLength = Integer.parseInt(standardImageLengthStr);
            if (standardImageLength > 0)
                GameFrame.STANDARD_IMAGE_LENGTH = standardImageLength;
            else {
                LOGGER.error("view.frame.standardImageLength illegal,shutdown");
                System.exit(0);
            }
            // TITLE
            GameFrame.TITLE = Dom4jUtil.getAttribute("view", "frame", "title");
            // IF_PLAY_BGM
            String ifBgm = Dom4jUtil.getAttribute("view", "frame", "ifPlayBgm");
            if ("1".equals(ifBgm))
                GameFrame.IF_PLAY_BGM = true;
            else if (!"0".equals(ifBgm)) {
                LOGGER.error("view.frame.ifPlayBgm illegal,shutdown");
                System.exit(0);
            }
            // REPAINT_INTERVAL
            String repaintIntervalStr = Dom4jUtil.getAttribute("view", "frame", "repaintInterval");
            int repaintInterval = Integer.parseInt(repaintIntervalStr);
            if (repaintInterval > 0)
                GameFrame.REPAINT_INTERVAL = repaintInterval;
            else {
                LOGGER.error("view.frame.repaintInterval illegal,shutdown");
                System.exit(0);
            }
            // TITLE_WIDTH
            String titleWidthStr = Dom4jUtil.getAttribute("view", "frame", "os", os, "titleWidth");
            int titleWidth = Integer.parseInt(titleWidthStr);
            if (titleWidth >= 0)
                GameFrame.TITLE_WIDTH = titleWidth;
            else {
                LOGGER.error("view.frame.os." + os + ".titleWidth illegal,shutdown");
                System.exit(0);
            }
            // L_WIDTH
            String lWidthStr = Dom4jUtil.getAttribute("view", "frame", "os", os, "lWidth");
            int lWidth = Integer.parseInt(lWidthStr);
            if (lWidth >= 0)
                GameFrame.L_WIDTH = lWidth;
            else {
                LOGGER.error("view.frame.os." + os + ".lWidth illegal,shutdown");
                System.exit(0);
            }
            // R_WIDTH
            String rWidthStr = Dom4jUtil.getAttribute("view", "frame", "os", os, "rWidth");
            int rWidth = Integer.parseInt(rWidthStr);
            if (rWidth >= 0)
                GameFrame.R_WIDTH = rWidth;
            else {
                LOGGER.error("view.frame.os." + os + ".rWidth illegal,shutdown");
                System.exit(0);
            }
            // D_WIDTH
            String dWidthStr = Dom4jUtil.getAttribute("view", "frame", "os", os, "dWidth");
            int dWidth = Integer.parseInt(dWidthStr);
            if (dWidth >= 0)
                GameFrame.D_WIDTH = dWidth;
            else {
                LOGGER.error("view.frame.os." + os + ".dWidth illegal,shutdown");
                System.exit(0);
            }
            // SIDEBAR
            String sidebarStr = Dom4jUtil.getAttribute("view", "frame", "os", os, "sidebar");
            int sidebar = Integer.parseInt(sidebarStr);
            if (sidebar >= 0)
                GameFrame.SIDEBAR = sidebar;
            else {
                LOGGER.error("view.frame.os." + os + ".sidebar illegal,shutdown");
                System.exit(0);
            }
            // IF_SIDEBAR_UP
            String ifSidebarUp = Dom4jUtil.getAttribute("view", "frame", "os", os, "ifSidebarUp");
            if ("1".equals(ifSidebarUp))
                GameFrame.IF_SIDEBAR_UP = true;
            else if (!"0".equals(ifSidebarUp)) {
                LOGGER.error("view.frame.os." + os + ".ifSidebarUp illegal,shutdown");
                System.exit(0);
            }
            // PADDING
            String paddingStr = Dom4jUtil.getAttribute("view", "frame", "padding");
            int padding = Integer.parseInt(paddingStr);
            if (padding >= 0)
                GameFrame.PADDING = padding;
            else {
                LOGGER.error("view.frame.os.padding illegal,shutdown");
                System.exit(0);
            }
        } catch (Exception e) {
            LOGGER.error("fail to init GameFrame static", e);
            System.exit(0);
        }
    }

    /**
     * 绘制组件
     */
    @Override
    public void paint(Graphics g) {
        try {
            this.dao.refreshData();
            this.moduleClock.draw(g);
        } catch (Exception e) {
            LOGGER.error("fail to paint,shutdown", e);
            System.exit(0);
        }
    }

    /**
     * 通过双缓冲解决闪烁问题
     * @param g Graphics, 画笔
     */
    @Override
    public void update(Graphics g) {
        Image bImage = super.createImage(this.getWidth(), this.getHeight());
        Graphics bg = bImage.getGraphics();
        this.paint(bg);
        bg.dispose();
        g.drawImage(bImage, 0, 0, this);
    }

    /**
     * 加载窗体，唯一启动入口
     * @throws URISyntaxException
     * @throws IOException
     */
    void launchFrame() throws URISyntaxException, IOException {
        // 初始化数据
        this.dao.initData();
        // 计算窗体宽高，计算时给了一个微调的值
        // 该值应随实际情况调整
        int adjustment = 7;
        int fWidth = GameFrame.STANDARD_IMAGE_LENGTH + 2 * (Module.EDGING_LENGTH + GameFrame.PADDING) + GameFrame.L_WIDTH + GameFrame.R_WIDTH - adjustment;
        int fHeight =GameFrame.STANDARD_IMAGE_LENGTH + 2 * (Module.EDGING_LENGTH + GameFrame.PADDING) + GameFrame.D_WIDTH + GameFrame.TITLE_WIDTH - adjustment;
        super.setSize(fWidth, fHeight);
        // 设置窗体标题
        super.setTitle(GameFrame.TITLE);
        // 设置窗体位置居中
        Dimension  d = Toolkit.getDefaultToolkit().getScreenSize();
        int frameX = (d.width - fWidth) / 2;
        int frameY = (d.height - GameFrame.SIDEBAR - fHeight) / 2;
        if (GameFrame.IF_SIDEBAR_UP)
            frameY += GameFrame.SIDEBAR;
        super.setLocation(frameX, frameY);
        // 初始化ModuleClock
        int mcOX = GameFrame.L_WIDTH + GameFrame.PADDING;
        int mcOY = GameFrame.TITLE_WIDTH + GameFrame.PADDING;
        int mcW = GameFrame.STANDARD_IMAGE_LENGTH + Module.EDGING_LENGTH;
        this.moduleClock = new ModuleClock(mcOX, mcOY, mcW, mcW, this.dao.getData());
        // 设置关闭窗体时的行为
        super.addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    LOGGER.info("program shutdown by window close");
                    System.exit(0);
                }
            }
        );
        // 启动重绘定时任务
        this.startScheduleRepaint();
        // 播放音乐
        if (GameFrame.IF_PLAY_BGM) {
            List<File> bgmMusicList = this.dao.getData().getBgmList();
            if (null == bgmMusicList || bgmMusicList.size() == 0) {
                LOGGER.error("need to paly bgm,but bgm list is null or empty,shutdown");
                System.exit(0);
            }
            new Thread(new BGMplayer(bgmMusicList)).start();
        }
        // 设置窗体可见
        super.setVisible(true);
    }

    /**
     * 启动重绘定时任务
     */
    private void startScheduleRepaint() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                GameFrame.this.repaint();
            }
        };
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(r, 0, GameFrame.REPAINT_INTERVAL, TimeUnit.MILLISECONDS);
    }
}
