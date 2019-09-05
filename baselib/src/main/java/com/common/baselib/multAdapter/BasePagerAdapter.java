package com.common.baselib.multAdapter;

import androidx.viewpager.widget.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * ================================================
 * 包名：com.common.baselib.multAdapter
 * 创建人：sws
 * 创建时间：2019/6/3  下午 11:31
 * 描述：
 * ================================================
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {
    protected List<T> mData;
    private SparseArray<View> mViews;

    public BasePagerAdapter(List<T> data) {
        mData = data;
        mViews = new SparseArray<View>(data.size());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position > mData.size()) {
            position %= mData.size();
        }
        View view = newView(mData.get(position));

//        View view = mViews.get(position);
//        if (view == null || view != null) {
//            view = newView(mData.get(position));
//            mViews.put(position, view);
//        }
        container.addView(view);
        return view;
    }

    public abstract View newView(T bean);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
//        container.removeView(mViews.get(position));
    }

    public T getItem(int position) {
        return mData.get(position);
    }
}


