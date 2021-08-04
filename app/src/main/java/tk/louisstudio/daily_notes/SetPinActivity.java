package tk.louisstudio.daily_notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

public class SetPinActivity extends AppCompatActivity {
  public static final String TAG = "PinLockView";
  private PinLockListener mPinLockListener =
      new PinLockListener() {
        @Override
        public void onComplete(String pin) {
          Log.d(TAG, "Pin complete: " + pin);
          SharedPreferences getPrefs =
              getBaseContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
          SharedPreferences.Editor editor;
          editor = getPrefs.edit();
          editor.putString("pinCode", pin);
          editor.apply();
          Toast.makeText(
                  MainActivity.getInstance(), getString(R.string.pin) + pin, Toast.LENGTH_SHORT)
              .show();
          finish();
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
    setContentView(R.layout.activity_set_pin);
    PinLockView mPinLockView = findViewById(R.id.pin_lock_view2);
    mPinLockView.setPinLockListener(mPinLockListener);
    IndicatorDots mIndicatorDots = findViewById(R.id.indicator_dots2);
    // 2个控件产生关联
    mPinLockView.attachIndicatorDots(mIndicatorDots);
    // 添加监听事件
    mPinLockView.setPinLockListener(mPinLockListener);
    // 设置密码总长度
    mPinLockView.setPinLength(4);
  }
}
