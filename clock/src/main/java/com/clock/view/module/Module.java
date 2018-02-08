package com.clock.view.module;

import java.awt.Graphics;
import java.awt.Image;

import com.clock.dao.DaoViewPojo;

public abstract class Module {

    /**
     * int, 从外部看，模块的横坐标
     */
    int oX;

    /**
     * int, 从外部看，模块的纵坐标
     */
    int oY;

    /**
     * int, 从外部看，模块的宽
     */
    int oWidth;

    /**
     * int, 从外部看，模块的高
     */
    int oHeight;

    /**
     * int, 模块实际有效区域的横坐标
     */
    int iX;
    
    /**
     * int, 模块实际有效区域的纵坐标
     */
    int iY;
    
    /**
     * int, 模块实际有效区域的宽
     */
    int iWidth;
    
    /**
     * int, 模块实际有效区域的高
     */
    int iHeight;

    /**
     * Image, 边框图片
     */
    Image window;

    /**
     * int, 边框图片宽度
     */
    int windowLength;

    int padding;

    DaoViewPojo data;

    /**
     * 
     * @param oX
     * @param oY
     * @param oWidth
     * @param oHeight
     * @param window Image, 若为null则表示本模块无边框
     * @param windowLength int, 若window为null则本字段需填0
     * @param padding int, 留白
     * @param data DaoViewPojo
     */
    Module(int oX, int oY, int oWidth, int oHeight, 
           Image window, int windowLength, int padding, DaoViewPojo data) {
        this.oX = oX;
        this.oY = oY;
        this.oWidth = oWidth;
        this.oHeight = oHeight;
        this.padding = padding;
        this.data = data;
        if (null != window) {
            this.window = window;
            this.windowLength = windowLength;
        }
        this.iX = this.oX + this.windowLength + this.padding;
        this.iY = this.oY + this.windowLength + this.padding;
        this.iWidth = this.oWidth - 2 * (this.windowLength + this.padding);
        this.iHeight = this.oHeight - 2 * (this.windowLength + this.padding);
    }

    void drawWindow(Graphics g) {
        int windowWidth = this.window.getWidth(null);
        int windowHeight = this.window.getHeight(null);
        // 左上
        g.drawImage(this.window, this.oX, this.oY, this.oX + this.windowLength, this.oY + this.windowLength, 0, 0, this.windowLength, this.windowLength, null);
        // 中上
        g.drawImage(this.window, this.oX + this.windowLength, this.oY, this.oX + this.oWidth - this.windowLength, this.oY + this.windowLength, this.windowLength, 0, windowWidth - this.windowLength, this.windowLength, null);
        // 右上
        g.drawImage(this.window, this.oX + this.oWidth - this.windowLength, this.oY, this.oX + this.oWidth, this.oY + this.windowLength, windowWidth - this.windowLength, 0, windowWidth, this.windowLength, null);
        // 左中
        g.drawImage(this.window, this.oX, this.oY + this.windowLength, this.oX + this.windowLength, this.oY + this.oHeight - this.windowLength, 0, this.windowLength, this.windowLength, windowHeight - this.windowLength, null);
        // 中
//        g.drawImage(this.window, this.oX + this.windowLength, this.oY + this.windowLength, this.oX + this.oWidth - this.windowLength, this.oY + this.oHeight - this.windowLength, this.windowLength, this.windowLength, windowWidth - this.windowLength, windowHeight - this.windowLength, null);
        // 右中
        g.drawImage(this.window, this.oX + this.oWidth - this.windowLength, this.oY + this.windowLength, this.oX + this.oWidth, this.oY + this.oHeight - this.windowLength, windowWidth - this.windowLength, this.windowLength, windowWidth, windowHeight - this.windowLength, null);
        // 左下
        g.drawImage(this.window, this.oX, this.oY + this.oHeight - this.windowLength, this.oX + this.windowLength, this.oY + this.oHeight, 0, windowHeight - this.windowLength, this.windowLength, windowHeight, null);
        // 中下
        g.drawImage(this.window, this.oX + this.windowLength, this.oY + this.oHeight - this.windowLength, this.oX + this.oWidth - this.windowLength, this.oY + this.oHeight, this.windowLength, windowHeight - this.windowLength, windowWidth - this.windowLength, windowHeight, null);
        // 右下
        g.drawImage(this.window, this.oX + this.oWidth - this.windowLength, this.oY + this.oHeight - this.windowLength, this.oX + this.oWidth, this.oY + this.oHeight, windowWidth - this.windowLength, windowHeight - this.windowLength, windowWidth, windowHeight, null);
    }

    abstract void draw(Graphics g);
}
