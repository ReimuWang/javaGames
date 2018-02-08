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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javazoom.jl.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import utils.SourceDataGetUtil;

import com.clock.constants.Constants;
import com.clock.dao.DaoView;
import com.clock.dao.DaoViewPojo;
import com.clock.view.module.ModuleClock;

@Component
public class GameFrame extends Frame {

    @Autowired
    private DaoView dao;

    private DaoViewPojo data;

    private String title;

    /**
     * int, 窗体宽
     */
    private int width;

    /**
     * int, 窗体高
     */
    private int height;

    /**
     * int, 底边栏高度
     */
    private int bottomSidebarHeight;

    /**
     * int, 标题栏宽度
     */
    private int titleWidth;

    /**
     * int, 窗体x坐标
     */
    private int frameX;

    /**
     * int, 窗体y坐标
     */
    private int frameY;

    /**
     * int, 窗体左右下部宽度
     */
    private int lrdWidth;

    /**
     * int, 各组件间留白
     */
    private int padding;

    /**
     * long, 刷新间隔，单位为ms
     */
    private long repaintInterval;

    private String imageWindowName;

    private int imageWindowLength;

    private ModuleClock moduleClock;

    void launchFrame() throws URISyntaxException, IOException {
        this.data = this.dao.initData();
        super.setSize(this.width, this.height);
        LOGGER.info("frame width=" + this.width + ",height=" + this.height);
        super.setTitle(this.title);
        LOGGER.info("frame title=" + this.title);
        this.setLocation();
        this.initModule();
        super.addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    LOGGER.info("program shutdown by window close");
                    System.exit(0);
                }
            }
        );
        this.startScheduleRepaint();
        this.playBgm();

        super.setVisible(true);
        LOGGER.info("frame launch finish");
    }

    private void initModule() throws IOException {
        LOGGER.info("titleWidth=" + this.titleWidth);
        LOGGER.info("left right down width=" + this.lrdWidth);
        LOGGER.info("padding=" + this.padding);
        this.initModuleClock();
    }

    private void initModuleClock() throws IOException {
        int oX = this.lrdWidth + this.padding;
        int oY = this.titleWidth + this.padding;
        int oHeight = this.height - this.titleWidth - this.lrdWidth
                      - 2 * this.padding;
        int oWidth = oHeight;
        Image window = SourceDataGetUtil
                       .loadBufferedImage(Constants.IMAGE_WINDOW_PATH + 
                               this.imageWindowName);
        this.moduleClock = new ModuleClock(oX, oY, oWidth, oHeight, 
                           window, this.imageWindowLength, this.padding, this.data);
        LOGGER.info("init ModuleClock finish:" + this.moduleClock);
    }

    private void playBgm() {
        List<File> musicList = this.data.getBgmMusicList();
        if (null == musicList) {
            LOGGER.info("not use bgm");
            return;
        }
        List<String> nameList = new ArrayList<String>(musicList.size());
        for (File f : musicList) nameList.add(f.getName());
        LOGGER.info("use bgm=" + nameList);
        new Thread(new BGMplayer(musicList)).start();
    }

    @Override
    public void paint(Graphics g) {
        this.dao.refreshData(this.data);
        this.moduleClock.draw(g);
    }

    private class BGMplayer implements Runnable {

        /**
         * List<File>, 音乐列表，默认相信该值合法
         */
        private List<File> musicList;

        private BGMplayer(List<File> musicList) {
            this.musicList = musicList;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    File f = this.chooseRandomFile();
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    Player player = null;
                    if (null == player || player.isComplete()) {
                        fis = new FileInputStream(f);
                        bis = new BufferedInputStream(fis);
                        player = new Player(bis);
                    }
                    player.play();
                    player.close();
                    bis.close();
                    fis.close();
                }
            } catch(Exception e) {
                LOGGER.error("catch error when play bgm", e);
            }
        }

        private File chooseRandomFile() {
            int count = this.musicList.size();
            int index = new Random().nextInt(count);
            File f = this.musicList.get(index);
            LOGGER.debug("random choose bgm=" + f.getName());
            return f;
        }
    }

    private void startScheduleRepaint() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                GameFrame.this.repaint();
            }
        };
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(r, 0, this.repaintInterval, TimeUnit.MILLISECONDS);
        LOGGER.info("schedule repaint pool create finish,interval=" + this.repaintInterval);
    }

    private void setLocation() {
        Dimension  d = Toolkit.getDefaultToolkit().getScreenSize();
        this.frameX = (d.width - this.width) / 2;
        this.frameY = (d.height - this.bottomSidebarHeight - this.height) / 2;
        super.setLocation(this.frameX, this.frameY);
        LOGGER.info("frame x=" + this.frameX + ",y=" + this.frameY);
    }

    /**
     * 通过双缓冲解决闪烁问题
     * @param g Graphics
     */
    @Override
    public void update(Graphics g) {
        Image bImage = super.createImage(this.getWidth(), this.getHeight());
        Graphics bg = bImage.getGraphics();
        this.paint(bg);
        bg.dispose();
        g.drawImage(bImage, 0, 0, this);
    }

    private final static Logger LOGGER = LoggerFactory
            .getLogger(GameFrame.class);

    GameFrame() {
        super();
    }

    private static final long serialVersionUID = 1L;

    @Value("#{config.view_frame_title}")
    public void setTitle(String title) {
        this.title = title;
    }

    @Value("#{config.view_frame_width}")
    public void setWidth(int width) {
        this.width = width;
    }

    @Value("#{config.view_frame_height}")
    public void setHeight(int height) {
        this.height = height;
    }

    @Value("#{config.view_base_bottomSidebarHeight}")
    public void setBottomSidebarHeight(int bottomSidebarHeight) {
        this.bottomSidebarHeight = bottomSidebarHeight;
    }

    @Value("#{config.view_frame_titleWidth}")
    public void setTitleWidth(int titleWidth) {
        this.titleWidth = titleWidth;
    }

    @Value("#{config.view_frame_LRDWidth}")
    public void setLrdWidth(int lrdWidth) {
        this.lrdWidth = lrdWidth;
    }

    @Value("#{config.view_frame_padding}")
    public void setPadding(int padding) {
        this.padding = padding;
    }

    @Value("#{config.view_base_repaintInterval}")
    public void setRepaintInterval(long repaintInterval) {
        this.repaintInterval = repaintInterval;
    }

    @Value("#{config.view_base_imageWindowName}")
    public void setImageWindowName(String imageWindowName) {
        this.imageWindowName = imageWindowName;
    }

    @Value("#{config.view_base_imageWindowLength}")
    public void setImageWindowLength(int imageWindowLength) {
        this.imageWindowLength = imageWindowLength;
    }
}
