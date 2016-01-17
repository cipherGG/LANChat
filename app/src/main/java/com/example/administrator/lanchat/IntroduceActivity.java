package com.example.administrator.lanchat;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class IntroduceActivity extends AppCompatActivity {
    MyUI myUI = new MyUI(this);
    ViewPager viewPager;
    List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        //实例化ViewPager和List
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewList = new ArrayList<>();

        //加载ListView
        myUI.creatViewPagerList(viewList, R.layout.viewpager1, R.layout.viewpager2, R.layout.viewpager3, R.layout.viewpager4, R.layout.viewpager5);

        //加载适配器
        viewPager.setAdapter(myUI.new MyPagerAdapter(viewList, null, new MyUI.PagerAdapterAction() {
            //最后一个页面的button事件
            @Override
            public void instantiateItemAction(View layout, int position) {
                if (position == 4) {
                    Button button = (Button) layout.findViewById(R.id.v5_btn);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(IntroduceActivity.this, MainActivity.class);
                            IntroduceActivity.this.startActivity(i);
                        }
                    });
                }
            }
        }));


    }

}
