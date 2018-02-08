package com.clock.view.module;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Calendar;

import com.clock.dao.DaoViewPojo;

public class ModuleClock extends Module {

    public ModuleClock(int oX, int oY, int oWidth, int oHeight, Image window,
                       int windowLength, int padding, DaoViewPojo data) {
        super(oX, oY, oWidth, oHeight, window, windowLength, 0, data);
    }

    @Override
    public void draw(Graphics g) {
        this.drawImages(g);
        this.drawWindow(g);
        this.drawClock(g);
    }

    private void drawImages(Graphics g) {
        Image image = this.data.getClockImageList().get(0);
        g.drawImage(image, this.iX, this.iY, this.iX + this.iWidth, this.iY + this.iHeight, 0, 0, image.getWidth(null), image.getHeight(null), null);
    }

    private void drawClock(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // 去锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 定圆心及半径
        int xCenter = this.iX + this.iWidth / 2;
        int yCenter = this.iY + this.iHeight / 2;
        int r = this.iWidth / 2;
        AffineTransform old = g2d.getTransform();
        // 画刻度 TODO
        // 时分秒信息
        Calendar calendar = this.data.getCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        double hexHMS = 60.0;
        double hexDH = 12.0;
        // 秒针
        double sAngle = second * 2 * Math.PI / hexHMS;
        g2d.rotate(sAngle, xCenter, yCenter);
        g.drawLine(xCenter, yCenter, xCenter, (int)(yCenter - r * 0.95));
        g2d.setTransform(old);
        // 遮蔽多边形
        g.setColor(Color.WHITE);
        int x0 = xCenter;
        int y0 = yCenter;
        int x1 = iX;
        int y1 = iY;
        int x2 = iX + iWidth;
        int y2 = iY;
        int x3 = iX + iWidth;
        int y3 = iY + iHeight;
        int x4 = iX;
        int y4 = iY + iHeight;
        int x5 = xCenter;
        int y5 = iY;
        if (second == 0 || second == 60) {
        } else if (second > 0 && second < 7.5) {
            int x = r + (int)(r * Math.tan(sAngle));
            int y= iY;
            g.fillPolygon(new int[]{x5,x0,x,x2,x3,x4,x1,x5}, 
                          new int[]{y5,y0,y,y2,y3,y4,y1,y5},
                          8);
        } else if (second > 7.5 && second < 22.5) {
            int x = iX + iWidth;
            int y= (int)(yCenter - r / Math.tan(sAngle));
            g.fillPolygon(new int[]{x5,x0,x,x3,x4,x1,x5}, 
                          new int[]{y5,y0,y,y3,y4,y1,y5},
                          7);
        } else if (second == 30) {
            g.fillRect(x1, y1, r, 2 * r);
        } else if (second > 22.5 && second < 37.5) {
            int x = (int)(xCenter - r * Math.tan(sAngle));
            int y= iY + iHeight;
            g.fillPolygon(new int[]{x5,x0,x,x4,x1,x5}, 
                          new int[]{y5,y0,y,y4,y1,y5},
                          6);
        } else if (second > 37.5 && second < 52.5) {
            int x = iX;
            int y= (int)(yCenter + r / Math.tan(sAngle));
            g.fillPolygon(new int[]{x5,x0,x,x1,x5}, 
                          new int[]{y5,y0,y,y1,y5},
                          5);
        } else if (second > 52.5 && second < 60) {
            int x = (int)(xCenter + r * Math.tan(sAngle));
            int y= iY;
            g.fillPolygon(new int[]{x5,x0,x,x5}, 
                          new int[]{y5,y0,y,y5},
                          4);
        }
        g.setColor(Color.BLACK);
        // 时针
        double hAngle = 2 * Math.PI * (hour - hexDH + minute / hexHMS + second / (hexHMS * hexHMS)) / hexDH;
        g2d.rotate(hAngle, xCenter, yCenter);
        g.drawLine(xCenter, yCenter, xCenter, (int)(yCenter - r * 0.45));    // TODO
        g2d.setTransform(old);
       // 分针
       double mAngle = 2 * Math.PI * (minute + second / hexHMS) / hexHMS;
       g2d.rotate(mAngle, xCenter, yCenter);
       g.drawLine(xCenter, yCenter, xCenter, (int)(yCenter - r * 0.7));    // TODO
    }
}
