package open.fruitbuy;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import open.fruitbuy.bean.FruitCheckInBean;
import open.fruitbuy.bean.FruitPreferenceBean;
import open.fruitbuy.common.bean.GoodsBean;
import open.fruitbuy.common.bean.OrderBean;
import open.fruitbuy.commontool.BaseNfcActivity;
import open.fruitbuy.commontool.ConfigUtil;
import open.fruitbuy.commontool.FruitDB;
import open.fruitbuy.commontool.FruitTypeInfo;
import open.fruitbuy.common.bean.UserBean;
import open.fruitbuy.common.service.MyHttpService;
import open.fruitbuy.common.service.constants.RequestType;

/**
 * Created by 李飞 on 2017/7/14.
 */

public class Scan extends BaseNfcActivity {
    private ImageView imgRotate;
    private Button btnReturn, btnOrder;
    private String nfcTagText;
    boolean autoorder;
    MyHttpService myHttpService;
    String token, buyerId;
    FruitDB db;
    ArrayList<OrderBean> orders;
    ArrayList<FruitCheckInBean> fruitInBeanArrayList;
    ArrayList<FruitCheckInBean> fruitOutBeanArrayList;
    ArrayList<FruitCheckInBean> fruitNeedInArrayList;
    ListView listViewScanIn, listViewScanOut, listViewNeedToOrder;
    ArrayList<HashMap<String, String>> listItemsScanIn, listItemsScanOut, listItemsNeedToOrder;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
                case RequestType.USER_GET_INFO:
                    UserBean data = (UserBean) msg.getData().get("userInfo");
                    buyerId = String.valueOf(data.getUid());
                    updateNeedInList();
                    initData();
                    initListView();
                    break;
                case RequestType.BUYER_CREATE_ORDER:
                    if (msg.getData().getBoolean("success")) {
                        Toast.makeText(Scan.this, "已自动下单", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Scan.this, "自动下单失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RequestType.USER_GET_ORDERS:
                    orders = (ArrayList<OrderBean>) msg.getData().getSerializable("orders");
                    break;
                default:
                    Toast.makeText(Scan.this, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;
                //TODO: other type of requst
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_scan);

        init();
        initView();
        initData();
        initListView();
        bindView();
    }

    public void initView() {
        btnReturn = (Button) findViewById(R.id.btn_header_left);
        btnOrder = (Button) findViewById(R.id.btn_order);
        findViewById(R.id.btn_header_right).setVisibility(View.INVISIBLE);

        imgRotate = (ImageView) findViewById(R.id.scan_rotate_image);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        imgRotate.startAnimation(operatingAnim);

        listViewScanIn = (ListView) findViewById(R.id.scan_in_listview);
        listViewScanOut = (ListView) findViewById(R.id.scan_out_listview);
        listViewNeedToOrder = (ListView) findViewById(R.id.need_to_order_listview);
    }

    public void bindView() {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scan.this.finish();
            }
        });
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = db.querySettingValue("address");

                if (address == null) {
                    Toast.makeText(Scan.this, "请填写您的地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean[] list = new boolean[fruitNeedInArrayList.size()];

                for (int i = 0; i < fruitNeedInArrayList.size(); i++) {
                    if (list[i]) {
                        continue;
                    }
                    int typeId_1 = fruitNeedInArrayList.get(i).getTypeid();
                    FruitPreferenceBean bean_1 = db.queryTypePerference(typeId_1);
                    String orderInfo = "{'address':'" + address + "','storeId':"
                            + bean_1.getSalerid() + ",'buyerId':" + buyerId + ",'goods':[{'typeId':"
                            + bean_1.getTypeId() + ",'typeName':'" + FruitTypeInfo.getFruitTypeName((long) bean_1.getTypeId())
                            + "','number':" + fruitNeedInArrayList.get(i).getAmount() + "}";
                    for (int j = i + 1; j < fruitNeedInArrayList.size(); j++) {
                        if (!list[i]) {
                            continue;
                        }
                        int typeId_2 = fruitNeedInArrayList.get(i).getTypeid();
                        FruitPreferenceBean bean_2 = db.queryTypePerference(typeId_2);

                        if (bean_1.getSalerid() == bean_2.getSalerid()) {
                            orderInfo += ", {'typeId':"
                                    + bean_2.getTypeId() + ",'typeName':'" + FruitTypeInfo.getFruitTypeName((long) bean_2.getTypeId())
                                    + "','number':" + fruitNeedInArrayList.get(j).getAmount() + "}";
                            db.insertSetting(String.valueOf(bean_2.getTypeId()), "true");
                            list[j] = true;
                        }
                    }
                    orderInfo += "]}";
                    myHttpService.createOrder(token, orderInfo);
                    db.insertSetting(String.valueOf(bean_1.getTypeId()), "true");
                    list[i] = true;
                }

                initData();
                initListView();
            }
        });
    }

    public void initData() {

        listItemsScanIn = new ArrayList<HashMap<String, String>>();
        listItemsScanOut = new ArrayList<HashMap<String, String>>();
        listItemsNeedToOrder = new ArrayList<HashMap<String, String>>();


        for (FruitCheckInBean bean : fruitInBeanArrayList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("key", FruitTypeInfo.getFruitTypeName((long) bean.getTypeid()));
            map.put("value", String.valueOf(bean.getAmount()));
            listItemsScanIn.add(map);
        }


        for (FruitCheckInBean bean : fruitOutBeanArrayList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("key", FruitTypeInfo.getFruitTypeName((long) bean.getTypeid()));
            map.put("value", String.valueOf(bean.getAmount()));
            listItemsScanOut.add(map);
        }

        for (FruitCheckInBean bean : fruitNeedInArrayList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("key", FruitTypeInfo.getFruitTypeName((long) bean.getTypeid()));
            map.put("value", String.valueOf(bean.getAmount()));
            listItemsNeedToOrder.add(map);
        }


    }

    public void initListView() {
        listViewScanIn.setAdapter(new ScanAdapter(this, listItemsScanIn));
        listViewScanOut.setAdapter(new ScanAdapter(this, listItemsScanOut));
        listViewNeedToOrder.setAdapter(new ScanAdapter(this, listItemsNeedToOrder));
    }

    private class ScanAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, String>> listData;

        public ScanAdapter(Context context,
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
                convertView = inflater.inflate(R.layout.scan_component, null);

            ((TextView) convertView.findViewById(R.id.basic_listitem_left)).setText(listData.get(position).get("key"));
            ((TextView) convertView.findViewById(R.id.basic_listitem_right)).setText(listData.get(position).get("value"));

            return convertView;
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Ndef ndef = Ndef.get(detectedTag);
        int fsid, typeid, oid, salerid;
        String date;


        nfcTagText = "";
        readNfcTag(intent);

        JSONObject fruitInfo = null;
        try {
            fruitInfo = new JSONObject(nfcTagText);
            fsid = fruitInfo.getInt("fsid");
            typeid = fruitInfo.getInt("typeid");
            oid = fruitInfo.getInt("orderid");
            salerid = fruitInfo.getInt("salerid");
            date = fruitInfo.getString("date");
            if (fruitInfo.has("isBuyerGetIn")) {
                if (!fruitInfo.has("isBuyerGetOut")) {
                    GetOut(fsid, typeid);
                    nfcTagText = nfcTagText.substring(0, nfcTagText.length() - 1) + ", 'isBuyerGetOut':'true'}";
                    NdefMessage ndefMessage = new NdefMessage(
                            new NdefRecord[]{createTextRecord(nfcTagText)});
                    boolean result = writeTag(ndefMessage, detectedTag);
                    if (result) {
                        Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "该水果已经出库", Toast.LENGTH_SHORT).show();
                }
            } else {
                GetIn(fsid, typeid, salerid, oid, date);

                nfcTagText = nfcTagText.substring(0, nfcTagText.length() - 1) + ", 'isBuyerGetIn':'true'}";
                NdefMessage ndefMessage = new NdefMessage(
                        new NdefRecord[]{createTextRecord(nfcTagText)});
                boolean result = writeTag(ndefMessage, detectedTag);
                if (result) {
                    Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            Toast.makeText(this, "芯片数据格式损坏", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        initData();
        initListView();
    }

    private void GetOut(int fsid, int typeId) {
        db.deleteStorage(fsid);
        FruitPreferenceBean outfruitPreferenceBean = db.queryTypePerference(typeId);
        int currentNum = outfruitPreferenceBean.getCurrentnum() - 1;
        db.updatePreferenceCurrentNumByOne(typeId, -1);
        autoorder = (outfruitPreferenceBean.getAutoorder() == 1) ? true : false;
        if ((currentNum < outfruitPreferenceBean.getMinnum()) && autoorder) {
            int number = outfruitPreferenceBean.getMaxnum() - currentNum;
            String address = db.querySettingValue("address");
            int storeId = outfruitPreferenceBean.getSalerid();
            String orderInfo = "{'address':'" + address + "'','storeId':" + storeId + ",'buyerId':" + buyerId + ",'goods':[{'typeId':" + typeId + ",'typeName':'" + FruitTypeInfo.getFruitTypeName((long) typeId) + "','number':" + number + "}]}";


            if (db.querySettingValue(String.valueOf(typeId)) == null) {
                myHttpService.createOrder(token, orderInfo);
                db.insertSetting(String.valueOf(typeId), "true");
            }
        } else {
            if (currentNum < outfruitPreferenceBean.getMinnum()) {
                updateOutList(typeId, 1);
            }
        }


        fruitOutBeanArrayList.add(new FruitCheckInBean(typeId, 1));
    }

    private void GetIn(int fsId, int typeId, int salerId, int orderId, String date) {
        db.insertStorage(fsId, typeId, salerId, orderId, date);
        db.updatePreferenceCurrentNumByOne(typeId, 1);

        updateInList(typeId, 1);
    }

    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }

            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    nfcTagText += textRecord;
                }
            } catch (Exception e) {

            }
        }
    }

    public static String parseTextRecord(NdefRecord ndefRecord) {
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }

        try {
            byte[] payload = ndefRecord.getPayload();

            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";

            int languageCodeLength = payload[0] & 0x3f;

            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

            String textRecord = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private void init() {
        fruitNeedInArrayList = new ArrayList<FruitCheckInBean>();
        fruitInBeanArrayList = new ArrayList<FruitCheckInBean>();
        fruitOutBeanArrayList = new ArrayList<FruitCheckInBean>();

        db = new FruitDB(getApplicationContext());
        HashMap<String, String> config = (HashMap<String, String>) ConfigUtil.loadServerConfig(ConfigUtil.SERVER_CONFIG_FILENAME, getAssets());
        token = db.querySettingValue("token");
        myHttpService = new MyHttpService(handler, config);
        myHttpService.getUserInfo(token);
        myHttpService.getOrders(token);
    }

    public void updateOutList(int typeid, int amount) {
        boolean flag = false;

        for (FruitCheckInBean each : fruitOutBeanArrayList) {
            if (each.getTypeid() == typeid) {
                each.setAmount(each.getAmount() + amount);
                flag = true;
                break;
            }
        }

        if (!flag) {
            fruitOutBeanArrayList.add(new FruitCheckInBean(typeid, amount));
        }
    }

    public void updateInList(int typeid, int amount) {
        boolean flag = false;

        for (FruitCheckInBean each : fruitInBeanArrayList) {
            if (each.getTypeid() == typeid) {
                each.setAmount(each.getAmount() + amount);
                flag = true;
                break;
            }
        }

        if (!flag) {
            fruitInBeanArrayList.add(new FruitCheckInBean(typeid, amount));
        }
    }

    public void updateNeedInList() {
        fruitNeedInArrayList.clear();
        List<FruitPreferenceBean> beanList = db.queryPerference();
        String address = db.querySettingValue("address");

        for (FruitPreferenceBean each : beanList) {
            if (db.querySettingValue(String.valueOf(each.getTypeId())) != null) {
                continue;
            }


            if (each.getCurrentnum() <= each.getMinnum()) {
                if (each.getAutoorder() == 1) {
                    String orderInfo = "{'address':'" + address + "','storeId':"
                            + each.getSalerid() + ",'buyerId':" + buyerId + ",'goods':[{'typeId':"
                            + each.getTypeId() + ",'typeName':'" + FruitTypeInfo.getFruitTypeName((long) each.getTypeId())
                            + "','number':" + (each.getMaxnum() - each.getCurrentnum()) + "}]}";

                    myHttpService.createOrder(token, orderInfo);

                    db.insertSetting(String.valueOf(each.getTypeId()), "true");
                    continue;
                }

                fruitNeedInArrayList.add(new FruitCheckInBean(each.getTypeId(), each.getMaxnum() - each.getCurrentnum()));
            }
        }
    }

    /**
     * 创建NDEF文本数据
     *
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    /**
     * 写数据
     *
     * @param ndefMessage 创建好的NDEF文本数据
     * @param tag         标签
     * @return
     */
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02x:", b));
        }

        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }

        return buf.toString();
    }

}
