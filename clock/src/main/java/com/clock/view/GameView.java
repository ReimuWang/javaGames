package com.clock.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameView {

    private final static Logger LOGGER = LoggerFactory.getLogger(GameView.class);

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
