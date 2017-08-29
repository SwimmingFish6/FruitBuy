package open.fruitbuy;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;
/**
 * Created by 李飞 on 2017/7/10.
 */

public class Register extends AppCompatActivity {
    Button btnReturn,btnReturn1,btnRegister,btnSendCode;
    private MyHttpService myHttpService;
    boolean isUsername = false;
    boolean isRealName = false;
    boolean isPhone = false;
    boolean isEmail = false;
    boolean isCode = false;
    boolean isPassword = false;
    boolean isPasswordConfirm = false;

    View.OnFocusChangeListener userNameListener = new View.OnFocusChangeListener() {
        private String temp;
        private String regex = "^[a-z0-9A-Z]+$";

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isUsername = false;
                if (temp.isEmpty()) {
                    Toast.makeText(Register.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "用户名只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(Register.this, "用户名输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isUsername = true;
                        }
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
                    Toast.makeText(Register.this, "真实姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "真实姓名只能包含汉字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(Register.this, "真实姓名输入过长", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Register.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "手机格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isPhone = true;
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener emailListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isEmail = false;
                if (temp.isEmpty()) {
                    Toast.makeText(Register.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isEmail = true;
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener codeListener = new View.OnFocusChangeListener() {
        private String temp;
        String regex = "^[0-9a-zA-z][0-9a-zA-z][0-9a-zA-z][0-9a-zA-z]$";


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isCode = false;
                if (temp.isEmpty()) {
                    Toast.makeText(Register.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "验证码格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        isCode = true;
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
                    Toast.makeText(Register.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(Register.this, "密码只能包含大小写字母与数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 20) {
                            Toast.makeText(Register.this, "密码输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            isPassword = true;
                        }
                    }
                }
            }
        }

    };

    View.OnFocusChangeListener passwordConfirmListener = new View.OnFocusChangeListener() {
        private String temp;

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText _v = (EditText) v;
            temp = _v.getText().toString();
            if (!hasFocus) {
                isPasswordConfirm = false;
                String temp2 = ((TextView) findViewById(R.id.register_pwd)).getText().toString();
                if (!temp.equals(temp2)) {
                    Toast.makeText(Register.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                } else {
                    isPasswordConfirm = true;
                }
            }
        }

    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_REGISTER:
                    if (msg.getData().getBoolean("success")) {
                        intent = new Intent(Register.this, Login.class);
                        Register.this.startActivity(intent);
                        Register.this.finish();
                        Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RequestType.USER_REGISTER_VERIFY:
                    if(msg.getData().getBoolean("success")){
                        Toast.makeText(Register.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Register.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(Register.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        initView();
        bindView();
    }
    private void initView(){
        btnSendCode = (Button)findViewById(R.id.register_send_code);
        btnRegister = (Button)findViewById(R.id.register_commit_button);
        btnReturn1 = (Button)findViewById(R.id.register_return_button);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_header_left).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.text_title)).setText("注册");

        ((TextView) findViewById(R.id.register_username)).setOnFocusChangeListener(userNameListener);
        ((TextView) findViewById(R.id.register_realname)).setOnFocusChangeListener(realNameListener);
        ((TextView) findViewById(R.id.register_phone)).setOnFocusChangeListener(phoneListener);
        ((TextView) findViewById(R.id.register_email)).setOnFocusChangeListener(emailListener);
        ((TextView) findViewById(R.id.register_code)).setOnFocusChangeListener(codeListener);
        ((TextView) findViewById(R.id.register_pwd)).setOnFocusChangeListener(passwordListener);
        ((TextView) findViewById(R.id.register_chechpwd)).setOnFocusChangeListener(passwordConfirmListener);

    }
    private void bindView(){
        btnReturn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register.this.finish();
            }
        });
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHttpService.verifyEmail(((TextView) findViewById(R.id.register_email)).getText().toString());
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.register_username)).clearFocus();
                ((TextView) findViewById(R.id.register_realname)).clearFocus();
                ((TextView) findViewById(R.id.register_phone)).clearFocus();
                ((TextView) findViewById(R.id.register_email)).clearFocus();
                ((TextView) findViewById(R.id.register_code)).clearFocus();
                ((TextView) findViewById(R.id.register_pwd)).clearFocus();
                ((TextView) findViewById(R.id.register_chechpwd)).clearFocus();


                boolean isCorrect = isCode && isUsername && isPassword && isPasswordConfirm && isEmail && isPhone && isRealName;
                if (!isCorrect) {
                    Toast.makeText(Register.this, "请检查您的格式", Toast.LENGTH_SHORT).show();
                } else {
                    String registerName = ((TextView) findViewById(R.id.register_username)).getText().toString();
                    String registerRealName = ((TextView) findViewById(R.id.register_realname)).getText().toString();
                    String registerPhone = ((TextView) findViewById(R.id.register_phone)).getText().toString();
                    String registaerEmail = ((TextView) findViewById(R.id.register_email)).getText().toString();
                    String registerVerifyCode = ((TextView) findViewById(R.id.register_code)).getText().toString();
                    String registerPassword = ((TextView) findViewById(R.id.register_pwd)).getText().toString();

                    myHttpService.register(registerName, registerPassword, registerPhone, registaerEmail, registerRealName, "buyer", registerVerifyCode);
                }
            }
        });

    }

    private void init() {
        Map<String, String> serverConfigMap = loadServerConfig("server.properties");
        if (serverConfigMap != null) {
            myHttpService = new MyHttpService(handler, serverConfigMap);
        }
    }

    private Map<String, String> loadServerConfig(String filename) {
        AssetManager am = getAssets();
        Properties props = new Properties();
        try {
            InputStream inputStream = am.open(filename);
            props.load(inputStream);
            Map<String, String> result = new HashMap<>();
            for (Object key : props.keySet()) {
                result.put((String) key, (String) props.get(key));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
