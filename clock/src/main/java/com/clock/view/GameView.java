package com.clock.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 外部与view层沟通的唯一入口
 */
@Component
public class GameView {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameView.class);

    /**
     * GameFrame, 本view所创建的游戏窗体
     */
    @Autowired
    private GameFrame frame;

    public void start() {
        try {
            this.frame.launchFrame();
        } catch (Exception e) {
            LOGGER.error("fail to launch frame", e);
        }
    }
}
