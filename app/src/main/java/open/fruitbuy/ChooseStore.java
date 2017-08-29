package open.fruitbuy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import open.fruitbuy.common.bean.FruitTypeBean;
import open.fruitbuy.common.bean.ReverseDetailBean;
import open.fruitbuy.common.bean.StoreBean;
import open.fruitbuy.common.bean.StoreReverseBean;
import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;
import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitTypeInfo;

/**
 * Created by 李飞 on 2017/7/13.
 */

public class ChooseStore extends AppCompatActivity{
    Button btnReturn;
    ListView listViewStore;
    ArrayList<HashMap<String, String>> listItems;
    private MyHttpService myHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_store);

        initHttpConnect();
        Intent intent = getIntent();
        long typeId = intent.getLongExtra("typeId", 10001);
        myHttpService.getStoreReversesDetailByType(""+typeId);

        initView();
        bindView();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.BUYER_GET_REVERSES:
                    ArrayList<ReverseDetailBean> stores = (ArrayList<ReverseDetailBean>) msg.getData().getSerializable("data");
                    initData(stores);
                    initListView();
                    break;
                default:
                    Toast.makeText(ChooseStore.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };


    private void initHttpConnect() {
        Map<String, String> serverConfigMap = ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        if (serverConfigMap != null) {
            myHttpService = new MyHttpService(handler, serverConfigMap);
        }
    }

    private void initView(){
        btnReturn = (Button)findViewById(R.id.btn_header_left);
        ((Button)findViewById(R.id.btn_header_right)).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.text_title)).setText("水果商店");
        listViewStore = (ListView) findViewById(R.id.choose_store_view);
    }

    private void bindView(){
        btnReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = getIntent();
                intent.putExtra("storeId", 0);
                setResult(0, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed () {
        Intent intent = getIntent();
        intent.putExtra("storeId", 0);
        setResult(0, intent);
        finish();
    }

    private void initData (ArrayList<ReverseDetailBean> stores) {
        listItems = new ArrayList<HashMap<String, String>>();

        for(ReverseDetailBean each:stores){
            HashMap<String, String> newItem = new HashMap<>();
            newItem.put("storeId", String.valueOf(each.getStoreId()));
            newItem.put("storeName", each.getStoreName());
            newItem.put("storeAddress", each.getAddress());
            newItem.put("storeScore", String.valueOf(each.getRank()));
            newItem.put("storeVolume", String.valueOf(each.getVolume()));

            listItems.add(newItem);
        }

    }

    private void initListView () {
        StoreAdapter storeAdapter = new StoreAdapter(this, listItems);
        listViewStore.setAdapter(storeAdapter);
        listViewStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = getIntent();
                intent.putExtra("storeId", listItems.get(i).get("storeId"));
                intent.putExtra("storeName", listItems.get(i).get("storeName"));
                setResult(0, intent);
                finish();
            }
        });
    }

    public class StoreAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, String>> listData;

        public StoreAdapter (Context context,
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
                convertView = inflater.inflate(R.layout.store_component, null);

            ((TextView) convertView.findViewById(R.id.store_component_name)).setText(listData.get(position).get("storeName"));
            ((TextView) convertView.findViewById(R.id.store_component_address)).setText(listData.get(position).get("storeAddress"));
            ((TextView) convertView.findViewById(R.id.store_component_score)).setText(listData.get(position).get("storeScore"));
            ((TextView) convertView.findViewById(R.id.store_component_volume)).setText(listData.get(position).get("storeVolume"));

            return convertView;
        }
    }
}
