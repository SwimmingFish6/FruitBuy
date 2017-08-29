package open.fruitbuy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;
import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitDB;

/**
 * Created by 李飞 on 2017/7/13.
 */

public class OrderInfo extends AppCompatActivity{
    Button btnReturn, btnFinish;
    ListView listView;
    ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();
    String oid, store, date, address, price, status, priceString, typeString, amountString, typeIdString;
    ArrayList<String> priceList, typeList, amountList, typeIdList;
    FruitDB db;
    MyHttpService myHttpService;
    String token;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.BUYER_ORDERS_RECEIVE:
                    if(msg.getData().getBoolean("success")){
                        Toast.makeText(OrderInfo.this, "已完成订单", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_info);
        Intent intent = getIntent();

        oid = intent.getStringExtra("oid");
        store = intent.getStringExtra("store");
        date = intent.getStringExtra("date");
        address = intent.getStringExtra("address");
        price = intent.getStringExtra("price");
        status = intent.getStringExtra("status");
        priceString = intent.getStringExtra("priceList");
        typeString = intent.getStringExtra("typeList");
        amountString = intent.getStringExtra("amountList");
        typeIdString = intent.getStringExtra("typeIdList");

        String typeTemp[] = typeString.split(",");
        typeList = new ArrayList<String>();
        for (int i = 0; i < typeTemp.length; i++) {
            typeList.add(typeTemp[i]);

        }
        typeTemp = priceString.split(",");
        priceList = new ArrayList<String>();
        for (int i = 0; i < typeTemp.length; i++)
            priceList.add(typeTemp[i]);

        typeTemp = amountString.split(",");
        amountList = new ArrayList<String>();
        for (int i = 0; i < typeTemp.length; i++)
            amountList.add(typeTemp[i]);

        typeTemp = typeIdString.split(",");
        typeIdList = new ArrayList<String>();
        for (int i = 0; i < typeTemp.length; i++)
            typeIdList.add(typeTemp[i]);

        init();
        initView();
        initData();
        bindView();
    }

    private void init() {
        db = new  FruitDB(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        myHttpService = new MyHttpService(handler, config);
        token = db.querySettingValue("token");
    }

    public void initView () {
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnFinish = (Button) findViewById(R.id.btn_finish_order);
//        btnComment = (Button) findViewById(R.id.order_info_comment);

        ((TextView) findViewById(R.id.text_title)).setText("订单详情");
        listView = (ListView) findViewById(R.id.order_info_fruit_list);

        ((TextView) findViewById(R.id.order_info_id)).setText(oid);
        ((TextView) findViewById(R.id.order_info_store_name)).setText(store);
        ((TextView) findViewById(R.id.order_info_datetime)).setText(date);
        ((TextView) findViewById(R.id.order_info_address)).setText(address);
        ((TextView) findViewById(R.id.order_info_price)).setText(price);
        ((TextView) findViewById(R.id.order_info_state)).setText(status);
    }

    public void initData () {
        for (int i = 0 ; i < typeList.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", typeList.get(i));
            map.put("price", priceList.get(i));
            map.put("amount", amountList.get(i));
            listItems.add(map);
        }
    }

    public void bindView () {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String each: typeIdList){
                    db.deleteSetting(each);
                }

                myHttpService.receiveOrder(token, oid);
            }
        });
//        btnComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        final OrderAdapter orderAdapter = new OrderAdapter(this, listItems);
        listView.setAdapter(orderAdapter);
    }

    private class OrderAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, String>> listData;

        public OrderAdapter (Context context,
                             ArrayList<HashMap<String, String>> listData) {
            this.inflater = LayoutInflater.from(context);
            this.listData = listData;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.basic_listitem, null);

            ((TextView) convertView.findViewById(R.id.basic_listitem_left)).setText(listData.get(position).get("name"));
            ((TextView) convertView.findViewById(R.id.basic_listitem_right)).setText(listData.get(position).get("amount") + " * ￥" + listData.get(position).get("price"));

            return convertView;
        }
    }
}
