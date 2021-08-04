package tk.louisstudio.daily_notes;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegeisterActivity extends AppCompatActivity {
  private Toast t;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DNUtils.transparencyBar(this);
    setContentView(R.layout.activity_register);
    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }
    final MaterialEditText email = findViewById(R.id.mail);
    final MaterialEditText pass = findViewById(R.id.pass);
    final MaterialEditText passagain = findViewById(R.id.passagain);
    final MaterialEditText usern = findViewById(R.id.usern);
    FloatingActionButton register = findViewById(R.id.registbut);
    usern.setOnFocusChangeListener(
        new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
            if (usern.getText().toString().equals("")) {
              usern.setError(getString(R.string.inputun));
            }
          }
        });
    pass.setOnFocusChangeListener(
        new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
            if (pass.getText().toString().equals("")) {
              if (usern.getText().toString().equals("")) {
                pass.setError(getString(R.string.inputpw));
              }
            }
          }
        });
    passagain.setOnFocusChangeListener(
        new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
            if (passagain.getText().toString().equals("")) {
              passagain.setError(getString(R.string.inputpw));
            }
            if (!passagain.getText().toString().equals(pass.getText().toString())) {
              passagain.setError(getString(R.string.different));
            }
          }
        });
    email.setOnFocusChangeListener(
        new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
            if (email.getText().toString().equals("")) {
              email.setError(getString(R.string.inputmail));
            }
          }
        });
    register.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            int code =
                User.instance.register(
                    usern.getText().toString(),
                    pass.getText().toString(),
                    passagain.getText().toString(),
                    email.getText().toString());
            int stringId = 0;
            switch (code) {
              case 0:
                stringId = R.string.successregis;
                break;
              case 1:
                stringId = R.string.usernameSame;
                break;
              case 2:
                stringId = R.string.different;
                break;
              case 3:
                stringId = R.string.internet;
                break;
            }
            t = Toast.makeText(getBaseContext(), getString(stringId), Toast.LENGTH_SHORT);
            t.show();
            finish();
          }
        });
  }
}
