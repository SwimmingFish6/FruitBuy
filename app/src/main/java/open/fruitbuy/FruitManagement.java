package open.fruitbuy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import open.fruitbuy.bean.FruitCheckInBean;
import open.fruitbuy.bean.FruitPreferenceBean;
import open.fruitbuy.common.bean.ReverseDetailBean;
import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitDB;
import open.fruitbuy.commontool.FruitTypeInfo;
import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by fruit on 2017/7/12.
 */

public class FruitManagement extends AppCompatActivity {
    private Button save;
    private Switch auto_buy;
    MyHttpService myHttpService;
    private TextView storeName, minPriceTextView, maxPriceTextView, minTextView, maxTextView,fruit;
    View store;
    private FruitDB db;
    //private long[] typeIdList;
    private long currentTypeID;
    private long preferStoreId;
    private String preferStoreName = null;
    private boolean isMin = true;
    private boolean isMax = true;
    private boolean isMinPrice = true;
    private boolean isMaxPrice = true;
//    private boolean isStore = false;
    private int minR = 0;
    private float minP = 0;
    FruitPreferenceBean bean;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.BUYER_GET_REVERSES:
                    ArrayList<ReverseDetailBean> stores = (ArrayList<ReverseDetailBean>) msg.getData().getSerializable("data");

                    for(ReverseDetailBean each : stores){
                        if(each.getStoreId() == preferStoreId){
                            storeName.setText(each.getStoreName());
                            break;
                        }
                    }

                    break;
                default:
                    Toast.makeText(FruitManagement.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };



    View.OnFocusChangeListener minListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[0-9]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isMin = false;
                if (temp.isEmpty()) {
                    Toast.makeText(FruitManagement.this, "阈值不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(FruitManagement.this, "阈值只能包含数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 3) {
                            Toast.makeText(FruitManagement.this, "水果库存阈值超过限制", Toast.LENGTH_SHORT).show();
                        } else {
                            minR = Integer.parseInt(temp);
                            isMin = true;
                        }
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener maxListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[0-9]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isMax = false;
                if (temp.isEmpty()) {
                    Toast.makeText(FruitManagement.this, "最大库存不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(FruitManagement.this, "最大库存只能包含数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 3) {
                            Toast.makeText(FruitManagement.this, "水果库存最大值超过限制", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Integer.parseInt(temp) < minR) {
                                Toast.makeText(FruitManagement.this, "水果库存最大值必须大于阈值", Toast.LENGTH_SHORT).show();
                            } else {
                                isMax = true;
                            }
                        }
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener minPriceListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex_1 = "^[0-9]+$";
        private String regex_2 = "^[0-9]+[.][0-9]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isMinPrice = false;
                if (temp.isEmpty()) {
                    Toast.makeText(FruitManagement.this, "最小价格不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex_1) && !temp.matches(regex_2)) {
                        Toast.makeText(FruitManagement.this, "最小价格必须为数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 6) {
                            Toast.makeText(FruitManagement.this, "最小价格输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            minP = Float.valueOf(temp).floatValue();
                            isMinPrice = true;
                        }
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener maxPriceListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex_1 = "^[0-9]+$";
        private String regex_2 = "^[0-9]+[.][0-9]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isMaxPrice = false;
                if (temp.isEmpty()) {
                    Toast.makeText(FruitManagement.this, "最大价格不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex_1) && !temp.matches(regex_2)) {
                        Toast.makeText(FruitManagement.this, "最大价格必须为数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 6) {
                            Toast.makeText(FruitManagement.this, "价格输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Float.valueOf(temp).floatValue() < minP) {
                                Toast.makeText(FruitManagement.this, "最大价格必须小于最小价格", Toast.LENGTH_SHORT).show();
                            } else {
                                isMaxPrice = true;
                            }
                        }
                    }
                }
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_management);


        initHttp();
        initView();
        bindView();
    }

    private void initHttp(){
        Map<String, String> serverConfigMap = ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        if (serverConfigMap != null) {
            myHttpService = new MyHttpService(handler, serverConfigMap);
        }
    }

    private void initView() {


        db = FruitDB.getInstance(FruitManagement.this);
        db.queryPerference();
        fruit = (TextView) findViewById(R.id.fruit_manage_name);
        store = findViewById(R.id.fruit_manage_address_list);
        auto_buy = (Switch) findViewById(R.id.fruit_manage_auto_buy_switch);
        save = (Button) findViewById(R.id.fruit_manage_commit);
        storeName = (TextView) findViewById(R.id.fruit_manage_address_choosen);
        maxTextView = (TextView) findViewById(R.id.fruit_manage_max_edit);
        minTextView =  (TextView) findViewById(R.id.fruit_manage_min_edit);
        maxPriceTextView = (TextView) findViewById(R.id.fruit_manage_max_price);
        minPriceTextView = (TextView) findViewById(R.id.fruit_manage_min_price);

        minPriceTextView.setOnFocusChangeListener(minPriceListener);
        maxPriceTextView.setOnFocusChangeListener(maxPriceListener);
        minTextView.setOnFocusChangeListener(minListener);
        maxTextView.setOnFocusChangeListener(maxListener);

        currentTypeID = Long.valueOf(getIntent().getStringExtra("typeId")).longValue();

        bean = db.queryTypePerference((int) currentTypeID);

        fruit.setText(FruitTypeInfo.getFruitTypeName(currentTypeID));
        minTextView.setText(bean.getMinnum() + "");
        maxTextView.setText(bean.getMaxnum() + "");
        minPriceTextView.setText(String.valueOf(bean.getMinprice()));
        maxPriceTextView.setText(String.valueOf(bean.getMaxprice()));
        auto_buy.setChecked((bean.getAutoorder() == 1)? true:false);
        preferStoreId = bean.getSalerid();
        myHttpService.getStoreReversesDetailByType(String.valueOf(currentTypeID));

    }

    private void bindView() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                minPriceTextView.clearFocus();
                maxPriceTextView.clearFocus();
                minTextView.clearFocus();
                maxTextView.clearFocus();

                boolean isCorrect = isMax && isMin && isMinPrice && isMaxPrice;
                if (!isCorrect) {
                    Toast.makeText(FruitManagement.this, "请检查您的格式", Toast.LENGTH_SHORT).show();
                } else {

                    float min_price = Float.parseFloat(minPriceTextView.getText().toString());
                    float max_price = Float.parseFloat(maxPriceTextView.getText().toString());
                    int min_num = Integer.parseInt(minTextView.getText().toString());
                    int max_num = Integer.parseInt(maxTextView.getText().toString());
                    int current_num = bean.getCurrentnum();
                    int isauto = (auto_buy.isChecked() == true ? 1 : 0);
                    db.deletePerference((int) currentTypeID);
                    db.insertPerference((int) currentTypeID, current_num, min_num, max_num, min_price, max_price, (int) preferStoreId, isauto);
                    Toast.makeText(FruitManagement.this, "修改水果偏好成功", Toast.LENGTH_SHORT).show();
                }

                Intent intent = getIntent();
                intent.putExtra("perferenceChangeFlag", true);
                setResult(0, intent);
                finish();
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FruitManagement.this, ChooseStore.class);
                intent.putExtra("typeId", currentTypeID);
                //FragmentAddFruit.this.startActivity(intent);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("perferenceChangeFlag", false);
        setResult(0, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data.getStringExtra("storeId") != null) {
            preferStoreId = Long.valueOf(data.getStringExtra("storeId"));
            if (preferStoreId != 0) {
                preferStoreName = data.getStringExtra("storeName");
                storeName.setText(preferStoreName);
//                isStore = true;
            }
        }
    }
}
