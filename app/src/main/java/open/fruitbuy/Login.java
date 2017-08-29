package open.fruitbuy;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import open.fruitbuy.bean.SystemSettingBean;
import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitDB;
import open.fruitbuy.commontool.FruitTypeInfo;
import open.fruitbuy.commontool.MacInfo;
import open.fruitbuy.common.bean.FruitTypeBean;
import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;


public class Login extends AppCompatActivity {
    private Button btnLogin,btnRegister,btnForgetPwd;
    private MyHttpService myHttpService;
    FruitDB db;
    long[] drawList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initHttpConnect();
        myHttpService.getFruitTypes();
        init();
        initView();
        bindView();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_LOGIN:
                    if (msg.getData().getBoolean("success")) {
                        intent = new Intent(Login.this, FruitMain.class);
                        String token = msg.getData().getString("token");
                        db.insertSetting("token", token);
                        db.insertSetting("autotake", "false");
                        db.insertSetting("autoremind", "false");
                        Login.this.startActivity(intent);
                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Login.this.finish();
                    }
                    else {
                        Toast.makeText(Login.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case RequestType.GET_FRUIT_TYPES:
                    ArrayList<FruitTypeBean> result = (ArrayList<FruitTypeBean>) msg.getData().getSerializable("types");
                    HashMap<Long, String> nameMap = new HashMap<Long, String>();
                    for (int i = 0; i < result.size(); i++) {
                        nameMap.put(result.get(i).getTypeId(),
                                result.get(i).getTypeName() + result.get(i).getClassName());
                    }

                    HashMap<Long, Long> picMap = new HashMap<Long, Long>();
                    for(int i=1; i<=41;i++){
                        picMap.put((long) i+10000, drawList[i]);
                    }

                    FruitTypeInfo.initialize(nameMap, picMap);

                    if(checkForSetting()){
                        intent = new Intent(Login.this, FruitMain.class);
                        Login.this.startActivity(intent);
                        Login.this.finish();
                    }
                    break;
                default:
                    Toast.makeText(Login.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };
    private void initView() {
        findViewById(R.id.btn_header_left).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.text_title)).setText("修鲜水果管家");
        btnLogin = (Button)findViewById(R.id.login_button);
        btnRegister = (Button)findViewById(R.id.register_button);
        btnForgetPwd = (Button)findViewById(R.id.forget_pwd_button);
    }
    private void bindView () {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextView) findViewById(R.id.usernameText)).getText().toString();
                String password = ((TextView) findViewById(R.id.pwdText)).getText().toString();
                String mac_id = MacInfo.getMachineHardwareAddress();

                myHttpService.login("buyer", username, password, mac_id);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                Login.this.startActivity(intent);
            }
        });

        btnForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                Login.this.startActivity(intent);
            }
        });
    }

    private void init(){
        db = FruitDB.getInstance(getApplicationContext());
        db.createPreferenceTable();
        db.createStorageTable();
        db.createSysytemSettingTable();

        drawList = new long[42];

        drawList[0] = 0;
        drawList[1] = (long) R.drawable.a10001;
        drawList[2] = (long) R.drawable.a10002;
        drawList[3] = (long) R.drawable.a10003;
        drawList[4] = (long) R.drawable.a10004;
        drawList[5] = (long) R.drawable.a10005;
        drawList[6] = (long) R.drawable.a10006;
        drawList[7] = (long) R.drawable.a10007;
        drawList[8] = (long) R.drawable.a10008;
        drawList[9] = (long) R.drawable.a10009;
        drawList[10] = (long) R.drawable.a10010;
        drawList[11] = (long) R.drawable.a10011;
        drawList[12] = (long) R.drawable.a10012;
        drawList[13] = (long) R.drawable.a10013;
        drawList[14] = (long) R.drawable.a10014;
        drawList[15] = (long) R.drawable.a10015;
        drawList[16] = (long) R.drawable.a10016;
        drawList[17] = (long) R.drawable.a10017;
        drawList[18] = (long) R.drawable.a10018;
        drawList[19] = (long) R.drawable.a10019;
        drawList[20] = (long) R.drawable.a10020;
        drawList[21] = (long) R.drawable.a10021;
        drawList[22] = (long) R.drawable.a10022;
        drawList[23] = (long) R.drawable.a10023;
        drawList[24] = (long) R.drawable.a10024;
        drawList[25] = (long) R.drawable.a10025;
        drawList[26] = (long) R.drawable.a10026;
        drawList[27] = (long) R.drawable.a10027;
        drawList[28] = (long) R.drawable.a10028;
        drawList[29] = (long) R.drawable.a10029;
        drawList[30] = (long) R.drawable.a10030;
        drawList[31] = (long) R.drawable.a10031;
        drawList[32] = (long) R.drawable.a10032;
        drawList[33] = (long) R.drawable.a10033;
        drawList[34] = (long) R.drawable.a10034;
        drawList[35] = (long) R.drawable.a10035;
        drawList[36] = (long) R.drawable.a10036;
        drawList[37] = (long) R.drawable.a10037;
        drawList[38] = (long) R.drawable.a10038;
        drawList[39] = (long) R.drawable.a10039;
        drawList[40] = (long) R.drawable.a10040;
        drawList[41] = (long) R.drawable.a10041;

    }

    private void initHttpConnect() {
        Map<String, String> serverConfigMap = ConfigUtil.loadServerConfig("server.properties", getAssets());
        if (serverConfigMap != null) {
            myHttpService = new MyHttpService(handler, serverConfigMap);
        }
    }

    private boolean checkForSetting(){
        List<SystemSettingBean> settings = db.querySetting();
        for(SystemSettingBean setting : settings){
            if (setting.getKey().equals("token")) {
                return true;
            }
        }
        return false;
    }
}

