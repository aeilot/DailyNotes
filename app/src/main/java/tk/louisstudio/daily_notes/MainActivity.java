package tk.louisstudio.daily_notes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import tk.louisstudio.daily_notes.adapter.HomePagerAdapter;
import tk.louisstudio.daily_notes.fragment.CountFragment;
import tk.louisstudio.daily_notes.fragment.HomeFragment;
import tk.louisstudio.daily_notes.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
  private static MainActivity instance;
  private NoteDB db;
  private BottomNavigationView bottomNavigationView;
  private Note currentNote;
  private FragmentManager fragmentManager;
  private Fragment home, count, search;
  private static final String CUSTOM_ACTION = "add_note";
  // Utils
  public static MainActivity getInstance() {
    return instance;
  }

  public NoteDB getDB() {
    return db;
  }

  public Note getCurrentNote() {
    return currentNote;
  }

  public void setCurrentNote(Note note) {
    currentNote = note;
  }

  public void addNote() {
    currentNote = db.createNote();
    Tab.instance.add(currentNote);
    Intent intent = new Intent(MainActivity.this, AddActivity.class);
    startActivity(intent);
  }

  public int uploadwebDAV() {
    SharedPreferences getPrefs2 =
        getBaseContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    String address = getPrefs2.getString("webdav_address", "");
    String account = getPrefs2.getString("webdav_account", "");
    String password = getPrefs2.getString("webdav_password", "");
    String dbPath = getDatabasePath("note.db").toString();
    String backupPath = getFilesDir().toString();
    if (address == null || account == null) return 1;
    if (address.endsWith("/")) address = address.substring(0, address.length() - 1);
    File database = new File(dbPath);
    Sardine sardine = new OkHttpSardine();
    sardine.setCredentials(account, password);
    try {
      sardine.createDirectory(address + "/diary/");
      sardine.createDirectory(address + "/diary/backup/");
      if (sardine.exists(address + "/diary/note.db")) {
        sardine.move(address + "/diary/note.db", address + "/diary/backup/note.db");
      }
      sardine.put(address + "/diary/note.db", database, "application/x-www-form-urlencoded");
      return 0;
    } catch (Exception e) {
      Log.d("SYNC", e.getMessage());
      Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
      e.printStackTrace();
    }
    return 1;
  }

  private static final String[] REQUIRED_PERMISSION_LIST =
      new String[] {
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CAMERA,
        // Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        // Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
      };
  private static final int REQUEST_PERMISSION_CODE = 12345;
  private List<String> missingPermission = new ArrayList<>();
  /** Checks if there is any missing permissions, and requests runtime permission if needed. */
  private void checkAndRequestPermissions() {
    // Check for permissions
    for (String eachPermission : REQUIRED_PERMISSION_LIST) {
      if (ContextCompat.checkSelfPermission(this, eachPermission)
          != PackageManager.PERMISSION_GRANTED) {
        missingPermission.add(eachPermission);
      }
    }
    // Request for missing permissions
    if (missingPermission.isEmpty()) {
      // startSDKRegistration();
      // CaptureApplication.getInstance().mUser.init Device();
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      ActivityCompat.requestPermissions(
          this,
          missingPermission.toArray(new String[missingPermission.size()]),
          REQUEST_PERMISSION_CODE);
    }
  }

  public static byte[] readInputStream(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[1024];
    int len = 0;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((len = inputStream.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    bos.close();
    return bos.toByteArray();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MainActivity.instance = this;
    db = new NoteDB(getApplicationContext(), getDatabasePath("note.db").toString());
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      MainActivity.getInstance()
          .getWindow()
          .getDecorView()
          .setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    setContentView(R.layout.activity_main);
    SharedPreferences getPrefs =
        MainActivity.getInstance().getSharedPreferences("settings", Context.MODE_PRIVATE);
    String saying = getPrefs.getString("saying", null);
    if (saying != null) {
      Toast.makeText(getInstance(), saying, Toast.LENGTH_LONG).show();
    }
    Thread t =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                //  Initialize SharedPreferences
                Log.d("ENTER", "ENTER");
                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                //  If the activity has never started before...
                if (isFirstStart) {
                  final Intent i = new Intent(MainActivity.this, IntroActivity.class);
                  startActivity(i);
                  SharedPreferences.Editor e = getPrefs.edit();
                  e.putBoolean("firstStart", false);
                  e.apply();
                }
              }
            });
    t.start();
    Tab.instance = new Tab();
    Tab.instance.tabs = new ArrayList<>();
    home = new HomeFragment();
    count = new CountFragment();
    search = new SearchFragment();
    // Init titleBar
    TextView tb = findViewById(R.id.titleNav);
    // Init
    bottomNavigationView = findViewById(R.id.nav_view);
    // ViewPager Set-up
    ViewPager vp = findViewById(R.id.modal);
    List<Fragment> fragmentList = new ArrayList<>();
    fragmentList.add(count);
    fragmentList.add(home);
    fragmentList.add(search);
    HomePagerAdapter pagerAdapter =
        new HomePagerAdapter(
            getSupportFragmentManager(),
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            fragmentList);
    vp.setAdapter(pagerAdapter);
    vp.setCurrentItem(1);
    vp.addOnPageChangeListener(
        new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(
              int position, float positionOffset, int positionOffsetPixels) {}

          @Override
          public void onPageSelected(int position) {
            final Animation in =
                AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.anim_fade_in);
            in.setFillAfter(true);
            final Animation out =
                AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.anim_fade_out);
            out.setFillAfter(true);
            switch (position) {
              case 0:
                tb.setAnimation(out);
                tb.setText("Trash");
                tb.setAnimation(in);
                break;
              case 1:
                tb.setAnimation(out);
                tb.setText("Notes");
                tb.setAnimation(in);
                break;
              case 2:
                tb.setAnimation(out);
                tb.setText("Search");
                tb.setAnimation(in);
                break;
              default:
                break;
            }
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
          }

          @Override
          public void onPageScrollStateChanged(int state) {}
        });
    // Bottom Nav
    bottomNavigationView.setSelectedItemId(R.id.navigation_notes);
    bottomNavigationView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.navigation_notes:
                vp.setCurrentItem(1);
                return true;
              case R.id.navigation_search:
                vp.setCurrentItem(2);
                return true;
              case R.id.navigation_memory:
                vp.setCurrentItem(0);
                return true;
            }
            return false;
          }
        });
    // User Center
    ImageButton account = findViewById(R.id.AccountBut);
    account.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
          }
        });
    // Add
    ImageButton add = findViewById(R.id.addBut);
    add.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            getInstance().addNote();
          }
        });
    int checkStatus =
        ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.INTERNET);
    if (PackageManager.PERMISSION_GRANTED != checkStatus) {
      ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, 1001);
    }
    if (CUSTOM_ACTION.equals(getIntent().getAction())) {
      addNote();
    }
    if (User.instance.loginFromSharedPrefernce(getBaseContext()) != 0) {
      Intent it = new Intent(MainActivity.this, LoginActivity.class);
      startActivity(it);
    }
    boolean pinEnabled = getPrefs.getBoolean("pinEnable", false);
    if (pinEnabled) {
      Intent intent1 = new Intent(MainActivity.this, PinLockActivity.class);
      startActivity(intent1);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  public void updateList() {
    ((HomeFragment) home).updateList();
  }

  public void updateListC() {
    ((CountFragment) count).updateList();
  }

  long exitTime;

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    SharedPreferences getPrefs = getBaseContext().getSharedPreferences("settings", MODE_PRIVATE);
    boolean ex = getPrefs.getBoolean("exit", false);
    if (ex && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
      if ((System.currentTimeMillis() - exitTime) > 2000) {
        Toast.makeText(
                MainActivity.this, getString(R.string.click_agian_quit_app), Toast.LENGTH_SHORT)
            .show();
        exitTime = System.currentTimeMillis();
      } else {
        System.exit(0);
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
}
