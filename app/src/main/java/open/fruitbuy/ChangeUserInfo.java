package open.fruitbuy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

import open.fruitbuy.bean.SystemSettingBean;
import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitDB;
import open.fruitbuy.common.bean.UserBean;
import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;

/**
 * Created by 李飞 on 2017/7/13.
 */

public class ChangeUserInfo extends AppCompatActivity{
    Button btnReturn,save,btnLogout;
    FruitDB db;
    MyHttpService myHttpService;
    String token;
    boolean isAddress = false, isPhone = false, isRealName = false;

    View.OnFocusChangeListener addressListener = new View.OnFocusChangeListener() {
        private String temp;

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isAddress = false;
                if (temp.isEmpty()) {
                    Toast.makeText(ChangeUserInfo.this, "商店地址不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (temp.length() > 50) {
                        Toast.makeText(ChangeUserInfo.this, "商店地址输入过长", Toast.LENGTH_SHORT).show();
                    } else {
                        isAddress = true;
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener realNameListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^[\u4e00-\u9fa5]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isRealName = false;
                if (temp.isEmpty()) {
                    Toast.makeText(ChangeUserInfo.this, "真实姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(ChangeUserInfo.this, "真实姓名只能包含汉字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(ChangeUserInfo.this, "真实姓名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isRealName = true;
                        }
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener phoneListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isPhone = false;
                if (temp.isEmpty()) {
                    Toast.makeText(ChangeUserInfo.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(ChangeUserInfo.this, "手机格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isPhone = true;
                    }
                }
            }
        }

    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_GET_INFO:
                    UserBean data = (UserBean) msg.getData().get("userInfo");
                    ((TextView) findViewById(R.id.realname)).setText(data.getRealname());
                    ((TextView) findViewById(R.id.phoneText)).setText(data.getPhone());
                    ((TextView) findViewById(R.id.emailText)).setText(data.getEmail());
                    break;
                case RequestType.USER_LOGOUT:
                    intent = new Intent(ChangeUserInfo.this, Login.class);
                    if(msg.getData().getBoolean("success")) {
                        db.deleteSetting("token");
                        db.deleteSetting("autobuy");
                        db.deleteSetting("autoremind");
                        Toast.makeText(ChangeUserInfo.this, "退出登录成功", Toast.LENGTH_SHORT).show();
                        ChangeUserInfo.this.startActivity(intent);
                        ChangeUserInfo.this.finish();
                    }
                    else{
                        Toast.makeText(ChangeUserInfo.this, "退出登录失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(ChangeUserInfo.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        init();
        initView();
        bindView();
    }
    private void init(){
        db = FruitDB.getInstance(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);
        String address = db.querySettingValue("address");

        ((EditText)findViewById(R.id.addressText)).setText(address);
        token = db.querySettingValue("token");
    }
    private void initView(){
        myHttpService.getUserInfo(token);
        btnReturn = (Button)findViewById(R.id.btn_header_left);
        save = (Button)findViewById(R.id.change_info_button);
        btnLogout=(Button)findViewById(R.id.logout);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.text_title)).setText("修改个人信息");

        ((EditText) findViewById(R.id.addressText)).setOnFocusChangeListener(addressListener);
        ((EditText) findViewById(R.id.phoneText)).setOnFocusChangeListener(phoneListener);
        ((EditText) findViewById(R.id.realname)).setOnFocusChangeListener(realNameListener);

    }

    private void bindView(){
        btnReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ChangeUserInfo.this.finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((TextView) findViewById(R.id.addressText)).clearFocus();
                ((TextView) findViewById(R.id.phoneText)).clearFocus();
                ((TextView) findViewById(R.id.realname)).clearFocus();

                boolean isCorrect = isAddress && isPhone &&  isRealName;

                if(!isCorrect){
                    Toast.makeText(ChangeUserInfo.this, "地址格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }

                String address = ((TextView) findViewById(R.id.addressText)).getText().toString();
                db.deleteSetting("address");
                db.insertSetting("address", address);
                Toast.makeText(ChangeUserInfo.this, "保存地址成功", Toast.LENGTH_SHORT);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ChangeUserInfo.this, Login.class);
                myHttpService.logout(token);
                ChangeUserInfo.this.finish();
            }
        });
    }
}
