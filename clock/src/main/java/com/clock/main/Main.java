package com.clock.main;

import com.clock.controller.GameController;

/**
 * 唯一入口
 */
public class Main {

    public static void main(String[] args) {
        GameController.INSTANCE.start();
    }
}
