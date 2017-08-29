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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import open.fruitbuy.commontool.FruitDB;

public class FragmentSetting extends Fragment {
    private View toUserInfo,toBankcard,toChangePwd;
    private Switch toOnekey,toMessage;
    private FruitDB db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        init(view);
        initView(view);
        bindView();
        return view;
    }

    private void init(View view){
        db = new FruitDB(getContext());
        boolean autobuy = Boolean.valueOf(db.querySettingValue("autobuy")).booleanValue();
        boolean autoremind = Boolean.valueOf(db.querySettingValue("autoremind")).booleanValue();

        ((Switch) view.findViewById(R.id.message_reminder_switch)).setChecked(autoremind);
        ((Switch) view.findViewById(R.id.auto_buy_switch)).setChecked(autobuy);

    }

    private void initView (View view) {
        toUserInfo = view.findViewById(R.id.to_account_management_view);
        toBankcard = view.findViewById(R.id.to_bank_card_setting);
        toChangePwd = view.findViewById(R.id.to_change_password);
        toOnekey = (Switch) view.findViewById(R.id.auto_buy_switch);
        toMessage = (Switch) view.findViewById(R.id.message_reminder_switch);
    }

    private void bindView () {
        toUserInfo.setOnClickListener(new SettingClickListener());
        toBankcard.setOnClickListener(new SettingClickListener());
        toChangePwd.setOnClickListener(new SettingClickListener());
        toOnekey.setOnCheckedChangeListener(new SettingCheckedChangeListener());
        toMessage.setOnCheckedChangeListener(new SettingCheckedChangeListener());

    }

    private class SettingClickListener implements View.OnClickListener {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.to_account_management_view:
                    intent = new Intent(getActivity(), ChangeUserInfo.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.to_bank_card_setting:
                    intent = new Intent(getActivity(), BindBankcard.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.to_change_password:
                    intent = new Intent(getActivity(), ChangePassword.class);
                    getActivity().startActivity(intent);
                    break;
            }
        }
    }

    private class SettingCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.auto_buy_switch:
                    db.deleteSetting("autobuy");
                    db.insertSetting("autobuy", String.valueOf(isChecked));
                    break;
                case R.id.message_reminder_switch:
                    db.deleteSetting("autoremind");
                    db.insertSetting("autoremind", String.valueOf(isChecked));
                    break;
            }
        }
    }


}
