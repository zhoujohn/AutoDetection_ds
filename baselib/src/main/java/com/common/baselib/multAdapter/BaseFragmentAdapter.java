package com.common.baselib.multAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 包名：com.common.baselib.multAdapter
 * 创建人：sws
 * 创建时间：2019/6/13  上午 10:48
 * 描述：
 * ================================================
 */
public class BaseFragmentAdapter<F extends Fragment> extends FragmentPagerAdapter {

    private List<F> mFragmentSet = new ArrayList<>(); // Fragment集合

    private F mCurrentFragment; // 当前显示的Fragment

    public BaseFragmentAdapter(FragmentActivity activity) {
        this(activity.getSupportFragmentManager());
    }

    public BaseFragmentAdapter(Fragment fragment) {
        this(fragment.getChildFragmentManager());
    }

    public BaseFragmentAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public F getItem(int position) {
        return mFragmentSet.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentSet.size();
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (getCurrentFragment() != object) {
            // 记录当前的Fragment对象
            mCurrentFragment = (F) object;
        }
        super.setPrimaryItem(container, position, object);
    }

    public void addFragment(F fragment) {
        mFragmentSet.add(fragment);
    }

    /**
     * 获取Fragment集合
     */
    public List<F> getAllFragment() {
        return mFragmentSet;
    }

    /**
     * 获取当前的Fragment
     */
    public F getCurrentFragment() {
        return mCurrentFragment;
    }
}