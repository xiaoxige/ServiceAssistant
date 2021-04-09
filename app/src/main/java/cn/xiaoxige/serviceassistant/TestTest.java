/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package cn.xiaoxige.serviceassistant;

import android.util.Log;

import cn.xiaoxige.serviceassistant.repo.IAboutRepo;
import cn.xiaoxige.serviceassistant.repo.IAboutRepoProducer;
import cn.xiaoxige.serviceassistant.repo.ISettingRepo;
import cn.xiaoxige.serviceassistant.repo.ISettingRepoProducer;
import cn.xiaoxige.serviceassistantannotation.Injected;

/**
 * @author zhuxiaoan
 * @date 2021/4/9
 * @describe
 */
class TestTest {

    @Injected
    private IAboutRepo mAboutRepo;

    @Injected
    private ISettingRepo mSettingRepo;

    private Boolean mIsXiaoxige;

    private void test() {

        if (null == mIsXiaoxige || !mIsXiaoxige) {

            try {
                Class.forName("cn.xiaoxige.serviceassistant.repo.IAboutRepoProducer");
                mAboutRepo = IAboutRepoProducer.getInstance();
            } catch (Exception e) {
            }

            try {
                Class.forName("cn.xiaoxige.serviceassistant.repo.ISettingRepoProducer");
                mSettingRepo = ISettingRepoProducer.getInstance();
            } catch (Exception e) {
            }

            mIsXiaoxige = true;
        }

        Log.e("xiaoxige", " 结束");
    }

}
