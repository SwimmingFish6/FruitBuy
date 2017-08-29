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

import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitDB;
import open.fruitbuy.common.bean.StoreBean;
import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;

/**
 * Created by 李飞 on 2017/7/13.
 */

public class BindBankcard extends AppCompatActivity{
    Button btnReturn,save;
    FruitDB db;
    MyHttpService myHttpService;
    String token;
    boolean isCardId = false, isPassword = false;

    View.OnFocusChangeListener cardIdListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[0-9]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isCardId = false;
                if (temp.isEmpty()) {
                    Toast.makeText(BindBankcard.this, "卡号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(BindBankcard.this, "卡号只能包含数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() == 17) {
                            Toast.makeText(BindBankcard.this, "卡号长度需要符合规则", Toast.LENGTH_SHORT).show();
                        } else {
                            isCardId = true;
                        }
                    }
                }
            }
        }
    };

    View.OnFocusChangeListener passwordListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[a-z0-9A-Z]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isPassword = false;
                if (temp.isEmpty()) {
                    Toast.makeText(BindBankcard.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(BindBankcard.this, "密码只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(BindBankcard.this, "密码输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isPassword = true;
                        }
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
                case RequestType.USER_GET_BANKCARDINFO:
                    String cardId = msg.getData().getString("cardId");
                    ((TextView) findViewById(R.id.bank_card)).setText(cardId);
                    break;
                case RequestType.USER_BIND_BANKCARD:
                    if(msg.getData().getBoolean("success")) {
                        Toast.makeText(BindBankcard.this, "绑定银行卡成功", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(BindBankcard.this, "绑定银行卡失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(BindBankcard.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard);
        init();
        initView();
        bindView();
    }
    private void init(){
        db = new  FruitDB(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);
        token = db.querySettingValue("token");
    }
    private void initView(){
        myHttpService.getBankAccountInfo(token);
        btnReturn = (Button)findViewById(R.id.btn_header_left);
        save = (Button)findViewById(R.id.bind_bank_button);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.text_title)).setText("绑定银行卡");

        ((TextView) findViewById(R.id.bank_card)).setOnFocusChangeListener(cardIdListener);
        ((TextView) findViewById(R.id.bank_card_pwd)).setOnFocusChangeListener(passwordListener);
    }

    private void bindView(){
        btnReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                BindBankcard.this.finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((TextView) findViewById(R.id.bank_card_pwd)).clearFocus();
                ((TextView) findViewById(R.id.bank_card)).clearFocus();

                boolean isCorrect = isCardId && isPassword;

                if(isCorrect) {
                    String cardID = ((TextView) findViewById(R.id.bank_card)).getText().toString();
                    String cardPwd = ((TextView) findViewById(R.id.bank_card_pwd)).getText().toString();

                    myHttpService.bindBankAccount(token, cardID, cardPwd);
                }
                else{
                    Toast.makeText(BindBankcard.this, "请检查输入格式", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
