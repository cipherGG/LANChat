package com.example.administrator.lanchat;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MyUI {

    Context context;

    //构造函数
    public MyUI(Context context) {
        this.context = context;
    }

    //ViewPager集合
    void creatViewPagerList(List<View> list, int... pagerLayout) {

        for (int currentLayout : pagerLayout) {
            list.add(LayoutInflater.from(context).inflate(currentLayout, null));
        }

    }

    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {

        List<View> viewList;
        List<String> titleList;
        PagerAdapterAction pagerAdapterAction;

        public MyPagerAdapter(List<View> viewList, List<String> titleList, PagerAdapterAction pagerAdapterAction) {
            this.viewList = viewList;
            this.titleList = titleList;
            this.pagerAdapterAction = pagerAdapterAction;
        }

        //判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View view, Object object) {

            //return false;
            //return view == viewList.get((int)Integer.parseInt(object.toString()));
            return view == object;
        }

        @Override
        public int getCount() {

            //return 0;
            return viewList.size();
        }

        //加载滑进来的View
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(viewList.get(position));
            //return super.instantiateItem(container, position);
            View layout = viewList.get(position);
            //接口回调
            pagerAdapterAction.instantiateItemAction(layout, position);
            return viewList.get(position);
        }

        //销毁滑出去的View
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            //super.destroyItem(container, position, object);
            container.removeView(viewList.get(position));
        }

        //生成Title
        @Override
        public CharSequence getPageTitle(int position) {

            //return super.getPageTitle(position);
            if (titleList == null) {
                return super.getPageTitle(position);
            } else {
                return titleList.get(position);
            }
        }
    }

    //Toast创建
    void creatToast(String content) {

        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    //BaseDialog创建
    void creatBaseDialog(Drawable icon, String title, String message, final View view, final DialogAction dialogAction) {

        final AlertDialog.Builder bulider = new AlertDialog.Builder(context);
        bulider.setIcon(icon);
        bulider.setTitle(title);
        bulider.setMessage(message);
        bulider.setView(view);
        //接口回调
        dialogAction.lunchAction();
        bulider.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //接口回调
                dialogAction.positiveAction();
            }
        });
        bulider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                creatToast("返回");
            }
        });
        bulider.show();
    }

    //ViewPager适配器---接口
    interface PagerAdapterAction {

        void instantiateItemAction(View layout, int position);
    }

    //BaseDialog创建---接口
    interface DialogAction {

        void lunchAction();

        void positiveAction();
    }


}
