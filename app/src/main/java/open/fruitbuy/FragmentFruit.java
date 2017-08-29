package open.fruitbuy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import open.fruitbuy.bean.FruitPreferenceBean;
import open.fruitbuy.bean.FruitStorageBean;
import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitDB;
import open.fruitbuy.commontool.FruitTypeInfo;
import open.fruitbuy.common.bean.StoreReverseBean;
import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentFruit extends Fragment {
    ListView listView;
    List<FruitPreferenceBean> fruitPreferenceBeanList;
    ArrayList<HashMap<String, Object>> listItems;
    FruitDB db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fruit, null);
        listView = (ListView) view.findViewById(R.id.fragment_fruit_view);

        init();
        initListView();
        return view;
    }

    private void init() {
        db = FruitDB.getInstance(getActivity());
        fruitPreferenceBeanList = db.queryPerference();

        initData(fruitPreferenceBeanList);
    }

    public void initData (List<FruitPreferenceBean> bean) {
        listItems = new ArrayList<HashMap<String, Object>>();

        for (FruitPreferenceBean each:bean) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("img", FruitTypeInfo.getPictureId((long) each.getTypeId()));
            map.put("typeId", each.getTypeId());
            map.put("currentNum", (each.getCurrentnum() == 0)? "0" : String.valueOf(each.getCurrentnum()) );
            map.put("name", FruitTypeInfo.getFruitTypeName((long) each.getTypeId()));
            listItems.add(map);
        }
    }


    public void initListView () {
        FruitAdapter fruitAdapter = new FruitAdapter(getActivity(), listItems);
        listView.setAdapter(fruitAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(getActivity(), FruitManagement.class);
                intent.putExtra("typeId", String.valueOf(listItems.get(position).get("typeId")));

                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && data.getBooleanExtra("perferenceChangeFlag", false)){
            fruitPreferenceBeanList = db.queryPerference();
        }
    }


    private class FruitAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, Object>> listData;

        public FruitAdapter (Context context, ArrayList<HashMap<String, Object>> listData) {
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
                convertView = inflater.inflate(R.layout.fruit_component, null);

            //(ImageView)convertView.findViewById(R.id.fragment_fruit_listitem_img).set
            HashMap<String, Object> item = listData.get(position);

            ((ImageView) convertView.findViewById(R.id.fruit_component_img)).setImageResource(Integer.parseInt(String.valueOf(listData.get(position).get("img"))));
            ((TextView) convertView.findViewById(R.id.fruit_component_name)).setText(String.valueOf(item.get("name")));
            ((TextView) convertView.findViewById(R.id.fruit_component_rest)).setText("剩余数量：" + item.get("currentNum"));

            return convertView;
        }
    }
}
