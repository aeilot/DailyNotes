package tk.louisstudio.daily_notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

public class PinLockActivity extends AppCompatActivity {
  private PinLockView mPinLockView;
  private IndicatorDots mIndicatorDots;
  public static final String TAG = "PinLockView";
  private PinLockListener mPinLockListener =
      new PinLockListener() {
        @Override
        public void onComplete(String pin) {
          Log.d(TAG, "Pin complete: " + pin);
          SharedPreferences getPrefs =
              getBaseContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
          String Code = getPrefs.getString("pinCode", null);
          if (pin.equals(Code)) {
            finish();
          } else {
            Toast.makeText(
                    MainActivity.getInstance(),
                    getString(R.string.pinerror) + pin,
                    Toast.LENGTH_SHORT)
                .show();
          }
        }

        @Override
        public void onEmpty() {
          Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
          Log.d(
              TAG,
              "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DNUtils.transparencyBar(this);
    setContentView(R.layout.activity_pin_lock);
    mPinLockView = findViewById(R.id.pin_lock_view);
    mPinLockView.setPinLockListener(mPinLockListener);
    mIndicatorDots = findViewById(R.id.indicator_dots);
    // 2个控件产生关联
    mPinLockView.attachIndicatorDots(mIndicatorDots);
    // 添加监听事件
    mPinLockView.setPinLockListener(mPinLockListener);
    // 设置密码总长度
    mPinLockView.setPinLength(4);
    Button but = findViewById(R.id.button2);
    but.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            SharedPreferences sharedPreferences;
            sharedPreferences =
                getBaseContext().getSharedPreferences("pref_login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor;
            editor = sharedPreferences.edit();
            editor.putString("un", null);
            editor.putString("pw", null);
            editor.commit();
            Intent intent = new Intent(PinLockActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
          }
        });
  }
}
