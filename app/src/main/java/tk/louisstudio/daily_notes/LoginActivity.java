package tk.louisstudio.daily_notes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {
  private boolean remember;
  private Toast t;
  //    private int RC_SIGN_IN;
  //    private static GoogleSignInAccount account;
  //    private GoogleSignInClient mGoogleSignInClient;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }
    //        GoogleSignInOptions gso = new
    // GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    //                .requestEmail()
    //                .build();
    //        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    Switch switch1 = findViewById(R.id.switch1);
    switch1.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
              remember = true;
            } else {
              remember = false;
            }
          }
        });
    Button regist = findViewById(R.id.regis);
    Button skip = findViewById(R.id.skipbut);
    final MaterialEditText un = findViewById(R.id.username);
    final MaterialEditText pw = findViewById(R.id.password);
    un.setOnFocusChangeListener(
        new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
            if (un.getText().toString().equals("")) {
              un.setError(getString(R.string.inputun));
            }
          }
        });
    pw.setOnFocusChangeListener(
        new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
            if (pw.getText().toString().equals("")) {
              pw.setError(getString(R.string.inputpw));
            }
          }
        });
    Button login = findViewById(R.id.loginbut);
    login.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            // If Success
            String unt = un.getText().toString();
            String pwt = pw.getText().toString();
            int res = User.instance.Login(unt, pwt);
            if (res == 0) {
              t = Toast.makeText(getBaseContext(), getString(R.string.welcome), Toast.LENGTH_SHORT);
              t.show();
              if (remember) {
                User.instance.saveTo(getBaseContext());
              }
              User.instance.saveToCommon(getBaseContext());
              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
              startActivity(intent);
            } else if (res == 1) {
              t =
                  Toast.makeText(
                      getBaseContext(), getString(R.string.invalid_username), Toast.LENGTH_SHORT);
              t.show();
              un.setText("");
              pw.setText("");
            } else if (res == 2) {
              t =
                  Toast.makeText(
                      getBaseContext(), getString(R.string.invalid_password), Toast.LENGTH_SHORT);
              t.show();
              un.setText("");
              pw.setText("");
            } else {
              t =
                  Toast.makeText(
                      getBaseContext(), getString(R.string.internet), Toast.LENGTH_SHORT);
              t.show();
              un.setText("");
              pw.setText("");
            }
          }
        });
    regist.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, RegeisterActivity.class);
            startActivity(intent);
          }
        });
    TextView tou = findViewById(R.id.textView3);
    tou.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("https://www.louisstudio.tk/about");
            intent.setData(content_url);
            startActivity(intent);
          }
        });
    skip.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            un.setText("guest");
            pw.setText("123456");
          }
        });
    // todo: third party login
  }

  @Override
  public void onStart() {
    super.onStart();
    if (User.instance.loginFromSharedPrefernce(getBaseContext()) == 0) {
      Toast t = Toast.makeText(getBaseContext(), getString(R.string.welcome), Toast.LENGTH_SHORT);
      t.show();
      finish();
    }
  }
}
