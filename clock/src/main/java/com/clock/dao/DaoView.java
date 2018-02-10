package com.clock.dao;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 总dao
 * 包含view数据传输层所有逻辑代码
 */
@Repository
public class DaoView {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoView.class);

    /**
     * DaoViewController, 与本dao绑定的控制器
     */
    @Autowired
    private DaoViewController controller;

    /**
     * DaoViewPojo, 本次游戏用于传输的数据
     */
    private DaoViewPojo data;

    /**
     * boolean, 是否需要改变图片
     * 每隔一段时间需替换图片，该字段由于防止重复替换
     * 例如每当秒数为0时替换1次图片
     * 那么0秒时第一次替换可以成功，但是0秒到1秒之间后续将不再替换
     */
    private boolean ifNeedChangeImage = true;

    /**
     * 数据初始化
     */
    public void initData() {
        DaoViewPojo data = new DaoViewPojo();
        data.setBgmList(this.controller.createBgmList());
        data.setImagePathList(this.controller.createImagePathList());
        if (null == data.getImagePathList() || data.getImagePathList().size() == 0) {
            LOGGER.error("image list is null or empty,shutdown");
            System.exit(0);
        }
        if (data.getImagePathList().size() == 1) data.setImage1Index(0);
        this.data = data;
    }

    /**
     * 数据刷新
     * 每次paint时触发
     */
    public synchronized void refreshData() {
        this.data.setCalendar(Calendar.getInstance());
        if (null == this.data.getCalendar()) {
            LOGGER.error("calendar is null,shutdown");
            System.exit(0);
        }
        this.setImageIndex();
    }

    /**
     * 设置传入时刻需展示的图片在图片路径列表中的索引。
     * 若设i0为底层图片的索引，i1为上层图片的索引。这两张图片来自于imageList
     * 每当秒数为0时切换一次图片，此时i0变为原i1，i1沿着imageList向后变换为一张新图片
     */
    private void setImageIndex() {
        int second = this.data.getCalendar().get(Calendar.SECOND);
        if (second != 1 && second != 2) return;
        if (second == 2) {
            this.ifNeedChangeImage = true;
            return;
        }
        if (!this.ifNeedChangeImage) return;
        List<String> imagePathList = this.data.getImagePathList();
        int image0Index = this.data.getImage0Index();
        image0Index++;
        if (image0Index == imagePathList.size())
            image0Index = 0;
        this.ifNeedChangeImage = false;
        int image1Index = image0Index + 1;
        if (image1Index == imagePathList.size())
            image1Index = 0;
        this.data.setImage0Index(image0Index);
        this.data.setImage1Index(image1Index);
    }

    public DaoViewPojo getData() {
        return data;
    }
}
