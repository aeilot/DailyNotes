package tk.louisstudio.daily_notes;

import static tk.louisstudio.daily_notes.MainActivity.readInputStream;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {
  public static SettingsActivity se;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);
    se = this;
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.settings, new SettingsFragment())
        .commit();
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  public static class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      getPreferenceManager().setSharedPreferencesName("settings");
      setPreferencesFromResource(R.xml.root_preferences, rootKey);
      Preference lg = findPreference("logout");
      lg.setOnPreferenceClickListener(
          new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
              AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.se);
              builder.setTitle(MainActivity.getInstance().getString(R.string.logoutque));
              builder.setMessage(MainActivity.getInstance().getString(R.string.logoutque2));
              builder.setNeutralButton(
                  MainActivity.getInstance().getString(R.string.no),
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                  });
              builder.setPositiveButton(
                  MainActivity.getInstance().getString(R.string.yesa),
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      logout();
                    }
                  });
              builder.show();
              return true;
            }
          });
      Preference rs = findPreference("restore");
      rs.setOnPreferenceClickListener(
          new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
              SharedPreferences sharedPreferences =
                  MainActivity.getInstance().getSharedPreferences("settings", Context.MODE_PRIVATE);
              boolean syncEn = sharedPreferences.getBoolean("sync", false);
              if (!syncEn) return false;
              Thread t =
                  new Thread(
                      new Runnable() {
                        @Override
                        public void run() {
                          Looper.prepare();
                          restore();
                          Looper.loop();
                        }
                      });
              t.start();
              return true;
            }
          });
      SwitchPreferenceCompat lock = findPreference("pinEnable");
      lock.setOnPreferenceChangeListener(
          new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
              SharedPreferences sharedPreferences =
                  MainActivity.getInstance().getSharedPreferences("settings", Context.MODE_PRIVATE);
              boolean pinEn = sharedPreferences.getBoolean("pinEnable", false);
              if (pinEn) {
                Intent i = new Intent(SettingsActivity.se, PinLockActivity.class);
                startActivity(i);
              } else {
                Intent i = new Intent(SettingsActivity.se, SetPinActivity.class);
                startActivity(i);
              }
              lock.setChecked(pinEn);
              return true;
            }
          });
    }
  }

  public static void logout() {
    SharedPreferences sharedPreferences;
    sharedPreferences =
        MainActivity.getInstance().getSharedPreferences("pref_log", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor;
    editor = sharedPreferences.edit();
    editor.putString("un", null);
    editor.putString("pw", null);
    editor.apply();
    sharedPreferences =
        MainActivity.getInstance().getSharedPreferences("pref_login", Context.MODE_PRIVATE);
    editor = sharedPreferences.edit();
    editor.putString("un", null);
    editor.putString("pw", null);
    editor.commit();
    Toast.makeText(
            MainActivity.getInstance(),
            MainActivity.getInstance().getString(R.string.active),
            Toast.LENGTH_SHORT)
        .show();
  }

  public static void restore() {
    // download tmp
    SharedPreferences getPrefs2 =
        MainActivity.getInstance().getSharedPreferences("settings", Context.MODE_PRIVATE);
    String address = getPrefs2.getString("webdav_address", "");
    String account = getPrefs2.getString("webdav_account", "");
    String password = getPrefs2.getString("webdav_password", "");
    if (address == null || account == null) return;
    if (address.endsWith("/")) address = address.substring(0, address.length() - 1);
    String dbPath = MainActivity.getInstance().getDatabasePath("note.db").toString();
    File database = new File(dbPath);
    Sardine sardine = new OkHttpSardine();
    sardine.setCredentials(account, password);
    try {
      if (!sardine.exists(address + "/diary/backup/note.db")) return;
      InputStream is = sardine.get(address + "/diary/backup/note.db");
      new File(dbPath).createNewFile();
      MainActivity.getInstance().getDB().close();
      database.delete();
      FileOutputStream fos = new FileOutputStream(dbPath);
      byte[] getData = readInputStream(is);
      fos.write(getData);
      if (fos != null) {
        fos.close();
      }
      if (is != null) {
        is.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    MainActivity.getInstance().getDB().load(MainActivity.getInstance(), dbPath);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
