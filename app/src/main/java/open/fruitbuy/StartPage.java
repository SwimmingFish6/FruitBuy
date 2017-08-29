package open.fruitbuy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class StartPage extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        Typeface typeFace =Typeface.createFromAsset(getAssets(),"fonts/icefruit.ttf");
        ((TextView) findViewById(R.id.start_title)).setTypeface(typeFace);
        handler.sendEmptyMessageDelayed(0,2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome(){
        Intent intent = new Intent(StartPage.this, Login.class);
        startActivity(intent);
        finish();
    }

}

