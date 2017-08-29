package open.fruitbuy;

/**
 * Created by 李飞 on 2017/7/10.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import open.fruitbuy.commontool.FruitDB;
import open.fruitbuy.commontool.FruitTypeInfo;

public class FragmentAddFruit extends Fragment {
    private Spinner fruit;
    private Button save;
    private Switch auto_buy;
    private TextView storeName, minPriceTextView, maxPriceTextView, minTextView, maxTextView;
    View store;
    private FruitDB db;
    private List<String> listType = new ArrayList<>();
    private long[] typeIdList;
    private long currentType;
    private long preferStoreId;
    private String preferStoreName = null;
    private boolean isMin = false;
    private boolean isMax = false;
    private boolean isMinPrice = false;
    private boolean isMaxPrice = false;
    private boolean isStore = false;
    private int minR = 0;
    private float minP = 0;


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
                    Toast.makeText(getActivity(), "阈值不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(getActivity(), "阈值只能包含数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 3) {
                            Toast.makeText(getActivity(), "水果库存阈值超过限制", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "最大库存不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex)) {
                        Toast.makeText(getActivity(), "最大库存只能包含数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 3) {
                            Toast.makeText(getActivity(), "水果库存最大值超过限制", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Integer.parseInt(temp) < minR) {
                                Toast.makeText(getActivity(), "水果库存最大值必须大于阈值", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "最小价格不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex_1) && !temp.matches(regex_2)) {
                        Toast.makeText(getActivity(), "最小价格必须为数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 6) {
                            Toast.makeText(getActivity(), "最小价格输入过长", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "最大价格不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!temp.matches(regex_1) && !temp.matches(regex_2)) {
                        Toast.makeText(getActivity(), "最大价格必须为数字", Toast.LENGTH_SHORT).show();
                    } else {
                        if (temp.length() > 6) {
                            Toast.makeText(getActivity(), "价格输入过长", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Float.valueOf(temp).floatValue() < minP) {
                                Toast.makeText(getActivity(), "最大价格必须小于最小价格", Toast.LENGTH_SHORT).show();
                            } else {
                                isMaxPrice = true;
                            }
                        }
                    }
                }
            }
        }

    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addfruit, null);
        initView(view);
        bindView();
        return view;
    }

    private void initView(View view) {

        ((TextView) view.findViewById(R.id.add_fruit_min_price)).setOnFocusChangeListener(minPriceListener);
        ((TextView) view.findViewById(R.id.add_fruit_max_price)).setOnFocusChangeListener(maxPriceListener);
        ((TextView) view.findViewById(R.id.add_fruit_min_edit)).setOnFocusChangeListener(minListener);
        ((TextView) view.findViewById(R.id.add_fruit_max_edit)).setOnFocusChangeListener(maxListener);


        db = FruitDB.getInstance(getActivity());
        db.queryPerference();
        fruit = (Spinner) view.findViewById(R.id.fruit_spinner);
        store = view.findViewById(R.id.add_fruit_address_list);
        auto_buy = (Switch) view.findViewById(R.id.add_fruit_auto_buy_switch);
        save = (Button) view.findViewById(R.id.add_fruit_commit);
        storeName = (TextView) view.findViewById(R.id.add_fruit_address_choosen);
        maxTextView = (TextView) view.findViewById(R.id.add_fruit_max_edit);
        minTextView =  (TextView) view.findViewById(R.id.add_fruit_min_edit);
        maxPriceTextView = (TextView) view.findViewById(R.id.add_fruit_max_price);
        minPriceTextView = (TextView) view.findViewById(R.id.add_fruit_min_price);

    }

    private void bindView() {
        getFruitTypeInfo();//更新水果列表
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                minPriceTextView.clearFocus();
                maxPriceTextView.clearFocus();
                minTextView.clearFocus();
                maxTextView.clearFocus();

                boolean isCorrect = isMax && isMin && isMinPrice && isMaxPrice && isStore;
                if (!isCorrect) {
                    Toast.makeText(getActivity(), "请检查您的格式", Toast.LENGTH_SHORT).show();
                } else {

                    float min_price = Float.parseFloat(minPriceTextView.getText().toString());
                    float max_price = Float.parseFloat(maxPriceTextView.getText().toString());
                    int min_num = Integer.parseInt(minTextView.getText().toString());
                    int max_num = Integer.parseInt(maxTextView.getText().toString());
                    int isauto = (auto_buy.isChecked() == true ? 1 : 0);
                    db.insertPerference((int) currentType, 0, min_num, max_num, min_price, max_price, (int) preferStoreId, isauto);
                    Toast.makeText(getActivity(), "添加水果偏好成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChooseStore.class);
                intent.putExtra("typeId", currentType);
                //FragmentAddFruit.this.startActivity(intent);
                startActivityForResult(intent, 0);
            }
        });

        fruit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentType = typeIdList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentType = typeIdList[0];
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data.getStringExtra("storeId") != null) {
            preferStoreId = Long.valueOf(data.getStringExtra("storeId"));
            if (preferStoreId != 0) {
                preferStoreName = data.getStringExtra("storeName");
                storeName.setText(preferStoreName);
                isStore = true;
            }
        }
    }

    public void getFruitTypeInfo() {
        typeIdList = new long[FruitTypeInfo.fruitTypeNameMap.size()];
        Iterator iter = FruitTypeInfo.fruitTypeNameMap.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            typeIdList[i] = (long) entry.getKey();
            i++;
            listType.add(entry.getValue().toString());
        }

        ArrayAdapter adapterType = new ArrayAdapter<String>(getActivity(), R.layout.fruit_type_spinneritem, listType);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fruit.setAdapter(adapterType);
    }
}