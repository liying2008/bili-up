package cc.duduhuo.openbilibili;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import cc.duduhuo.openbilibili.config.Const;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg);

        final SharedPreferences sp = getSharedPreferences(Const.PREFERENCES_NAME, MODE_PRIVATE);

        int id = sp.getInt(Const.ID, Const.ID_WEBVIEW);
        if (id == Const.ID_BILI) {
            rg.check(R.id.rbBili);
        } else if (id == Const.ID_BILI_BLUE) {
            rg.check(R.id.rbBiliBlue);
        } else {
            rg.check(R.id.rbWebView);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rbBili) {
                    sp.edit().putInt(Const.ID, Const.ID_BILI).apply();
                } else if (i == R.id.rbBiliBlue) {
                    sp.edit().putInt(Const.ID, Const.ID_BILI_BLUE).apply();
                } else {
                    sp.edit().putInt(Const.ID, Const.ID_WEBVIEW).apply();
                }
            }
        });
    }
}
