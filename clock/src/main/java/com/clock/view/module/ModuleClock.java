package com.clock.view.module;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clock.dao.DaoViewPojo;
import com.clock.view.GameFrame;
import com.utils.Dom4jUtil;
import com.utils.ImageUtil;
import com.utils.SourceDataGetUtil;

/**
 * 模块：时钟
 */
public class ModuleClock extends Module {

    /**
     * double, 时针宽度
     * 实际是时针宽度与标准图片尺寸的比例
     */
    private static double HOUR_HAND_WIDTH;

    /**
     * double, 时针长度
     * 实际是时针长度与标准图片尺寸的比例
     */
    private static double HOUR_HAND_LENGTH;

    /**
     * double, 分针宽度
     * 实际是分针宽度与标准图片尺寸的比例
     */
    private static double MINUTE_HAND_WIDTH;

    /**
     * double, 分针长度
     * 实际是分针长度与标准图片尺寸的比例
     */
    private static double MINUTE_HAND_LENGTH;

    /**
     * double, 中心圆半径
     * 实际是中心圆半径与标准图片尺寸的比例
     */
    private static double SMALL_R;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleClock.class);

    /**
     * 表示一个点
     */
    private class Point {

        /**
         * double, 横坐标
         */
        private double x;

        /**
         * double, 纵坐标
         */
        private double y;

        /**
         * 构造函数
         * @param x double, 横坐标
         * @param y double, 纵坐标
         */
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    static {
        try {
            // HOUR_HAND_WIDTH
            String hourHandWidthStr = Dom4jUtil.getAttribute("view", "frame", "module", "moduleClock", "hourHandWidth");
            double hourHandWidth = Double.parseDouble(hourHandWidthStr);
            if (hourHandWidth > 0)
                ModuleClock.HOUR_HAND_WIDTH = hourHandWidth;
            else {
                LOGGER.error("view.frame.module.moduleClock.hourHandWidth illegal,shutdown");
                System.exit(0);
            }
            // HOUR_HAND_LENGTH
            String hourHandLengthStr = Dom4jUtil.getAttribute("view", "frame", "module", "moduleClock", "hourHandLength");
            double hourHandLength = Double.parseDouble(hourHandLengthStr);
            if (hourHandLength > 0)
                ModuleClock.HOUR_HAND_LENGTH = hourHandLength;
            else {
                LOGGER.error("view.frame.module.moduleClock.hourHandLength illegal,shutdown");
                System.exit(0);
            }
            // MINUTE_HAND_WIDTH
            String minuteHandWidthStr = Dom4jUtil.getAttribute("view", "frame", "module", "moduleClock", "minuteHandWidth");
            double minuteHandWidth = Double.parseDouble(minuteHandWidthStr);
            if (minuteHandWidth > 0)
                ModuleClock.MINUTE_HAND_WIDTH = minuteHandWidth;
            else {
                LOGGER.error("view.frame.module.moduleClock.minuteHandWidth illegal,shutdown");
                System.exit(0);
            }
            // MINUTE_HAND_LENGTH
            String minuteHandLengthStr = Dom4jUtil.getAttribute("view", "frame", "module", "moduleClock", "minuteHandLength");
            double minuteHandLength = Double.parseDouble(minuteHandLengthStr);
            if (minuteHandLength > 0)
                ModuleClock.MINUTE_HAND_LENGTH = minuteHandLength;
            else {
                LOGGER.error("view.frame.module.moduleClock.minuteHandLength illegal,shutdown");
                System.exit(0);
            }
            // SMALL_R
            String smallRStr = Dom4jUtil.getAttribute("view", "frame", "module", "moduleClock", "smallR");
            double smallR = Double.parseDouble(smallRStr);
            if (smallR > 0)
                ModuleClock.SMALL_R = smallR;
            else {
                LOGGER.error("view.frame.module.moduleClock.smallR illegal,shutdown");
                System.exit(0);
            }
        } catch (Exception e) {
            LOGGER.error("fail to init ModuleClock static", e);
            System.exit(0);
        }
    }

    /**
     * 构造函数
     * @param oX int, 从外部看，模块的横坐标
     * @param oY int, 从外部看，模块的纵坐标
     * @param oWidth int, 从外部看，模块的宽
     * @param oHeight int, 从外部看，模块的高
     * @param data DaoViewPojo, 数据信息
     */
    public ModuleClock(int oX, int oY, int oWidth, int oHeight, DaoViewPojo data) {
        super(oX, oY, oWidth, oHeight, 0, data);
    }

    /**
     * 绘制模块
     * @param g Graphics, 画笔
     * @throws IOException 
     */
    @Override
    public void draw(Graphics g) throws IOException {
        this.drawClock(g);
        this.drawWindow(g);
    }

    /**
     * 绘制钟表
     * @param g Graphics, 画笔
     * @throws IOException
     */
    private void drawClock(Graphics g) throws IOException {
        Graphics2D g2d = (Graphics2D)g;
        // 去锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 圆心
        int xCenter = this.iX + this.iWidth / 2;
        int yCenter = this.iY + this.iHeight / 2;
        // 记录初始信息，用后需恢复
        AffineTransform oldAT = g2d.getTransform();
        Stroke oldS = g2d.getStroke();
        // 时分秒信息
        Calendar calendar = this.data.getCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        // 简单常量
        double hexDH = 12.0;
        double hexHMS = 60.0;
        // 绘秒针
        this.drawSecond(g, second);
        // 绘时针
        double hAngle = 2 * Math.PI * (hour - hexDH + minute / hexHMS + second / (hexHMS * hexHMS)) / hexDH;
        g2d.rotate(hAngle, xCenter, yCenter);
        g2d.setStroke(new BasicStroke((float)(GameFrame.STANDARD_IMAGE_LENGTH * ModuleClock.HOUR_HAND_WIDTH)));
        g.drawLine(xCenter, yCenter, xCenter, (int)(yCenter - GameFrame.STANDARD_IMAGE_LENGTH * ModuleClock.HOUR_HAND_LENGTH));
        g2d.setTransform(oldAT);
       // 绘分针
       double mAngle = 2 * Math.PI * (minute + second / hexHMS) / hexHMS;
       g2d.rotate(mAngle, xCenter, yCenter);
       g2d.setStroke(new BasicStroke((float)(GameFrame.STANDARD_IMAGE_LENGTH * ModuleClock.MINUTE_HAND_WIDTH)));
       g.drawLine(xCenter, yCenter, xCenter, (int)(yCenter - GameFrame.STANDARD_IMAGE_LENGTH * ModuleClock.MINUTE_HAND_LENGTH));
       g2d.setTransform(oldAT);
       // 复原画笔
       g2d.setStroke(oldS);
       // 画中心的圆
       int smallR = (int)(GameFrame.STANDARD_IMAGE_LENGTH * ModuleClock.SMALL_R);
       g.fillOval(xCenter - smallR, yCenter - smallR, 2 * smallR, 2 * smallR);
    }

    /**
     * 绘制秒针
     * 以图片的变化来代表秒针的移动
     * @param g Graphics, 画笔
     * @param second int, 当前秒数
     * @throws IOException
     */
    private void drawSecond(Graphics g, int second) throws IOException {
        // 当前秒数的圆心角
        double sAngle = second * 2 * Math.PI / 60;
        // 标准图片尺寸。b0,b1均会转为该尺寸后再进行像素的拼接
        int sil = GameFrame.STANDARD_IMAGE_LENGTH;
        // b0为底层图片;b1为上层图片
        List<String> list = this.data.getImagePathList();
        int i0 = this.data.getImage0Index();
        int i1 = this.data.getImage1Index();
        BufferedImage b0 = SourceDataGetUtil.loadBufferedImage(list.get(i0));
        BufferedImage b1 = SourceDataGetUtil.loadBufferedImage(list.get(i1));
        // 将b0及b1的尺寸均调整为标准尺寸
        if (b0.getWidth() != sil || b0.getHeight() != sil)
            b0 = ImageUtil.changeLength(b0, sil, sil);
        if (b1.getWidth() != sil || b1.getHeight() != sil)
            b1 = ImageUtil.changeLength(b1, sil, sil);
        // 后续操作均以b0为主体，如有需要b1将替换b0对应位置
        // 因调整后b0,b1均为正方形，后文将讨论b0的内切圆
        // b0内切圆直径
        double d = b0.getWidth();
        // b0内切圆半径
        double r = d / 2;
        // b0内切圆圆心
        Point cc = this.new Point(r, r);
        // 以y=ax+b为格式，求当前直线函数的a,b值
        // a=parm[0],b=parm[1]
        double[] parm = null;
        if (second > 0 && second < 7.5) {
            double x = r + r * Math.tan(sAngle);
            double y = 0.0;
            Point temp = new Point(x, y);
            parm = this.equation(cc, temp);
        } else if (second > 7.5 && second < 22.5) {
            double x = d;
            double y = cc.y - r / Math.tan(sAngle);
            Point temp = new Point(x, y);
            parm = this.equation(cc, temp);
        } else if (second > 22.5 && second < 37.5) {
            double x = cc.x - r * Math.tan(sAngle);
            double y = d;
            Point temp = new Point(x, y);
            parm = this.equation(cc, temp);
        } else if (second > 37.5 && second < 52.5) {
            double x = 0.0;
            double y = cc.y + r / Math.tan(sAngle);
            Point temp = new Point(x, y);
            parm = this.equation(cc, temp);
        } else if (second > 52.5 && second < 60) {
            double x = cc.x + r * Math.tan(sAngle);
            double y = 0.0;
            Point temp = new Point(x, y);
            parm = this.equation(cc, temp);
        }
        double a = null==parm ? 0.0 : parm[0];
        double b = null==parm ? 0.0 : parm[1];
        // 逐像素，分区域替换图形
        for (int y = 0; y < b0.getHeight(); y++) {
            for (int x = 0; x < b0.getWidth(); x++) {
                double baseY = a * x + b;
                int p1 = b1.getRGB(x, y);
                if (second == 15) {
                    if (x >= r && y <= r) b0.setRGB(x, y, p1);
                } else if (second == 30) {
                    if (x >= r) b0.setRGB(x, y, p1);
                } else if (second == 45) {
                    if (x >= r || (x < r && y >= r)) b0.setRGB(x, y, p1);
                } else if (second > 0 && second < 30) {
                    if (x >= r && y <= baseY) b0.setRGB(x, y, p1);
                } else {
                    if (x >= r || (x < r && y >= baseY)) b0.setRGB(x, y, p1);
                }
            }
        }
        g.drawImage(b0, this.iX, this.iY, this.iX + this.iWidth, this.iY + this.iHeight, 0, 0, (int)d, (int)d, null);
    }

    /**
     * 由两点求二元一次方程
     * y=ax+b
     * 本方法无法计算斜率a不存在的情况
     * @param p1 Point, 点1
     * @param p2  Point, 点1
     * @return double[], 若设返回值为r，则r[0]=a,r[1]=b
     */
    private double[] equation(Point p1, Point p2) {
        double[] parm = new double[2];
        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;
        double a = (y2 - y1) / (x2 - x1);
        double b = y1 - a * x1;
        parm[0] = a;
        parm[1] = b;
        return parm;
    }
}
