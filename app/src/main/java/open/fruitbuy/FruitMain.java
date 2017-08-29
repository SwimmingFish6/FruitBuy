package open.fruitbuy;

/**
 * Created by 李飞 on 2017/7/10.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FruitMain extends AppCompatActivity{
    private List<Fragment> fragments;
    private Button btnScan;
    private ViewPager viewPager;
    private FrameLayout fruitfl,addfruitfl,orderfl,settingfl;
    private TextView vfruit,vaddfruit,vorder,vsetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        //初始化默认为选中点击了“水果”按钮
        bindView();
        fruitfl.setSelected(true);
    }
    private void initView(){
        findViewById(R.id.btn_header_left).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_header_right).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("我的水果");
        btnScan = (Button)findViewById(R.id.btn_header_right);
        fruitfl = (FrameLayout)findViewById(R.id.layout_fruit);
        addfruitfl = (FrameLayout)findViewById(R.id.layout_addfruit);
        orderfl = (FrameLayout)findViewById(R.id.layout_order);
        settingfl = (FrameLayout)findViewById(R.id.layout_setting);

        vfruit = (TextView)findViewById(R.id.view_fruit);
        vaddfruit = (TextView)findViewById(R.id.view_addfruit);
        vorder = (TextView)findViewById(R.id.view_order);
        vsetting = (TextView)findViewById(R.id.view_setting);
        viewPager = (ViewPager)findViewById(R.id.fragment_content);
    }
    private void initData(){
        fragments = new ArrayList<>();
        fragments.add(new FragmentFruit());
        fragments.add(new FragmentAddFruit());
        fragments.add(new FragmentOrder());
        fragments.add(new FragmentSetting());
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MainPageChangeListener());
    }
    public void bindView () {
        fruitfl.setOnClickListener(new MainClickListener(0));
        addfruitfl.setOnClickListener(new MainClickListener(1));
        orderfl.setOnClickListener(new MainClickListener(3));
        settingfl.setOnClickListener(new MainClickListener(4));
        btnScan.setOnClickListener(new MainClickListener(5));
    }

    public void clearSelect () {
        fruitfl.setSelected(false);
        addfruitfl.setSelected(false);
        orderfl.setSelected(false);
        settingfl.setSelected(false);
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MainPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return (fragments != null && fragments.size() != 0) ? fragments.get(position) : null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return (fragments == null) ? 0 : fragments.size();
        }
    }

    private class MainClickListener implements View.OnClickListener {
        private int index = 0;

        public MainClickListener (int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            switch (index) {
                case 0:
                    clearSelect();
                    fruitfl.setSelected(true);
                    viewPager.setCurrentItem(0);
                    break;
                case 1:
                    clearSelect();
                    addfruitfl.setSelected(true);
                    viewPager.setCurrentItem(1);
                    break;
                case 2:
                    break;
                case 3:
                    clearSelect();
                    orderfl.setSelected(true);
                    viewPager.setCurrentItem(2);
                    break;
                case 4:
                    clearSelect();
                    settingfl.setSelected(true);
                    viewPager.setCurrentItem(3);
                    break;
                case 5:
                    Intent intent = new Intent(FruitMain.this, Scan.class);
                    FruitMain.this.startActivity(intent);
                    break;
            }
        }
    }

    private class MainPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    ((TextView) findViewById(R.id.text_title)).setText("我的水果");
                    clearSelect();
                    fruitfl.setSelected(true);
                    break;
                case 1:
                    ((TextView) findViewById(R.id.text_title)).setText("添加水果");
                    clearSelect();
                    addfruitfl.setSelected(true);
                    break;
                case 2:
                    ((TextView) findViewById(R.id.text_title)).setText("我的订单");
                    clearSelect();
                    orderfl.setSelected(true);
                    break;
                case 3:
                    ((TextView) findViewById(R.id.text_title)).setText("我的账号");
                    clearSelect();
                    settingfl.setSelected(true);
                    break;
            }
        }
    }
}
