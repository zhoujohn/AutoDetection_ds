package com.common.mvplib.base.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.common.baselib.utils.CommonUtils;
import com.common.mvplib.R;
import com.common.mvplib.base.fragment.BaseFragment;
import com.common.mvplib.config.LayoutConfig;
import com.common.mvplib.databinding.LayoutActFgComBinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 包名：com.common.mvplib.base.activity
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:51
 * 描述：
 * ================================================
 */
public abstract class BaseFmgActivity extends BaseActivity<LayoutActFgComBinding> {

    /**
     * fragment的map集合
     */
    public LinkedHashMap<String, BaseFragment> mapFm = new LinkedHashMap<String, BaseFragment>();
    /**
     * fragment管理器
     */
    private FragmentManager manager;

    private BaseFragment currFragment;

    public BaseFragment getFragment(String fgKey) {
        return mapFm.get(fgKey);
    }

    /**
     * 获取布局文件Id
     *
     * @return
     */
    public int getLayoutId() {
        return R.layout.layout_act_fg_com;
    }


    /**
     * 配置 topbar and loading
     *
     * @return
     */
    public LayoutConfig initLayoutConfig() {
        manager = getSupportFragmentManager();
        return null;
    }


    /**
     * 切换fragment:
     */
    public void showFragment(String fgClazz) {
        showFragment(fgClazz, null);
    }

    /**
     * 切换fragment:
     */
    public void showFragment(String fgClazz, Bundle arguements) {
        if (!mapFm.isEmpty()) {
            for (Map.Entry<String, BaseFragment> me : mapFm.entrySet()) {
                Fragment currFg = me.getValue();
                if (currFg != null && currFg.isAdded()) {
                    manager.beginTransaction().hide(currFg).commitAllowingStateLoss();
                }
            }
        }
        BaseFragment currFg = mapFm.get(fgClazz);
        if (currFg == null) {
            currFg = (BaseFragment) Fragment.instantiate(this, fgClazz);
            currFg.setArguments(arguements);
            mapFm.put(fgClazz, currFg);
            if (!currFg.isAdded()) {
                manager.beginTransaction().add(R.id.fmg_container, currFg).commitAllowingStateLoss();
            }
        } else {
            manager.beginTransaction().show(currFg).commitAllowingStateLoss();
        }
        currFragment = currFg;
    }

    /**
     * 回退fragment
     */
    public void backFragment() {
        List<String> keyList = getKeyList();

        int lastIndex = keyList.size() - 1;
        int secondLastIndex = keyList.size() - 2;

        manager.beginTransaction().remove(mapFm.get(keyList.get(lastIndex))).commitAllowingStateLoss();
        mapFm.remove(keyList.get(lastIndex));

        showFragment(keyList.get(secondLastIndex));

    }

    /**
     * 回退
     */
    public void back() {
        if (CommonUtils.isListNull(mapFm)) {
            return;
        }
        if (mapFm.size() < 2) {
            CommonUtils.closeSoftKeyBoard(this);
            setRes();
            finish();
        } else {
            backFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if (currFragment != null) {
            if (!currFragment.backFragment()) {
                back();
            }
        }
    }

    private List<String> getKeyList() {
        List<String> keyList = new ArrayList<String>();
        for (Map.Entry<String, BaseFragment> me : mapFm.entrySet()) {
            keyList.add(me.getKey());
        }
        return keyList;
    }

    public void setRes() {

    }
}
