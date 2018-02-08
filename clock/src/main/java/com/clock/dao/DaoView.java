package com.clock.dao;

import org.springframework.stereotype.Repository;

import com.clock.controller.GameController;

@Repository
public class DaoView {

    private DaoViewController controller;

    /**
     * 传输数据初始化
     * @return DaoViewPojo
     */
    public DaoViewPojo initData() {
        DaoViewPojo data = new DaoViewPojo();
        DaoViewController c = this.getController();
        data.setBgmMusicList(c.createBgmList());
        return data;
    }

    /**
     * 刷新需展现的数据
     * @param data DaoViewPojo
     */
    public synchronized void refreshData(DaoViewPojo data) {
        DaoViewController dvc = this.getController();
        data.setCalendar(dvc.getNowCalendar());
        data.setClockImageList(dvc.refreshClockImageList(data.getClockImageList()));
    }

    private DaoViewController getController() {
        if (null == this.controller)
            this.controller = GameController.INSTANCE;
        return this.controller;
    }
}
