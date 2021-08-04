package tk.louisstudio.daily_notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
  public static User instance = new User();
  public String username;
  public String password;
  public String email;
  public String expire;
  public boolean isLog = false;

  User() {}

  public int loginFromSharedPrefernce(Context context) {
    if (isLog) {
      return 0;
    }
    SharedPreferences sharedPreferences;
    sharedPreferences = context.getSharedPreferences("pref_login", Context.MODE_PRIVATE);
    String un = sharedPreferences.getString("un", null);
    String pw = sharedPreferences.getString("pw", null);
    return Login(un, pw);
  }

  public void saveTo(Context context) {
    SharedPreferences sharedPreferences;
    sharedPreferences = context.getSharedPreferences("pref_login", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor;
    editor = sharedPreferences.edit();
    editor.putString("un", this.username);
    editor.putString("pw", this.password);
    editor.commit();
  }

  public void saveToCommon(Context context) {
    SharedPreferences sharedPreferences;
    sharedPreferences = context.getSharedPreferences("pref_log", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor;
    editor = sharedPreferences.edit();
    editor.putString("un", this.username);
    editor.putString("ex", this.expire);
    editor.commit();
  }

  public int Login(String un, String pw) {
    if (isLog) {
      return 0;
    }
    try {
      URL url = new URL("http://cloud.rockysoft.cn/louis/login.php");
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
      // 设置连接主机超时时间
      // urlConn.setConnectTimeout(5 * 1000);
      // 设置从主机读取数据超时
      // urlConn.setReadTimeout(5 * 1000);
      urlConn.setDoInput(true);
      urlConn.setDoOutput(true);
      urlConn.setRequestMethod("POST");
      urlConn.setUseCaches(false);
      urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
      urlConn.setRequestProperty("Charset", "utf-8");
      urlConn.connect();
      DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
      String encoded_name = new String(Base64.encode(un.getBytes(), Base64.NO_WRAP));
      dop.writeBytes("username=" + URLEncoder.encode(encoded_name, "UTF-8"));
      String encoded_password = new String(Base64.encode(pw.getBytes(), Base64.NO_WRAP));
      dop.writeBytes("&password=" + URLEncoder.encode(encoded_password, "UTF-8"));
      dop.flush();
      dop.close();
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
      StringBuilder result = new StringBuilder();
      String readLine = null;
      while ((readLine = bufferedReader.readLine()) != null) {
        result.append(readLine);
      }
      bufferedReader.close();
      urlConn.disconnect();
      String res = new String(Base64.decode(result.toString(), Base64.NO_WRAP));
      Log.println(Log.DEBUG, "Louis", res);
      if (res.startsWith("error")) {
        return Integer.valueOf(res.substring(6));
      } else {
        int ex = res.indexOf("expire=");
        int and = res.indexOf("&", ex);
        this.expire = res.substring(ex + 7, and);
        isLog = true;
        username = un;
        password = pw;
        instance.saveToCommon(MainActivity.getInstance().getBaseContext());
        return 0;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 3;
  }

  public int register(String un, String pw, String pwa, String email) {
    if (!pw.equals(pwa)) {
      return 2;
    }
    try {
      URL url = new URL("http://cloud.rockysoft.cn/louis/register.php");
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
      // 设置连接主机超时时间
      // urlConn.setConnectTimeout(5 * 1000);
      // 设置从主机读取数据超时
      // urlConn.setReadTimeout(5 * 1000);
      urlConn.setDoInput(true);
      urlConn.setDoOutput(true);
      urlConn.setRequestMethod("POST");
      urlConn.setUseCaches(false);
      urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
      urlConn.setRequestProperty("Charset", "utf-8");
      urlConn.connect();
      DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
      String encoded_name = new String(Base64.encode(un.getBytes(), Base64.NO_WRAP));
      dop.writeBytes("username=" + URLEncoder.encode(encoded_name, "UTF-8"));
      String encoded_password = new String(Base64.encode(pw.getBytes(), Base64.NO_WRAP));
      dop.writeBytes("&password=" + URLEncoder.encode(encoded_password, "UTF-8"));
      String encoded_email = new String(Base64.encode(email.getBytes(), Base64.NO_WRAP));
      dop.writeBytes("&email=" + URLEncoder.encode(encoded_email, "UTF-8"));
      dop.flush();
      dop.close();
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
      StringBuilder result = new StringBuilder();
      String readLine = null;
      while ((readLine = bufferedReader.readLine()) != null) {
        result.append(readLine);
      }
      bufferedReader.close();
      urlConn.disconnect();
      String res = result.toString();
      if (res.startsWith("success")) {
        return 0;
      } else {
        return 1;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 3;
  }

  public boolean isExpired() {
    Date today = new Date();
    Date ex = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try {
      ex = sdf.parse(expire);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (ex == null) {
      return false;
    }
    if (today.getTime() >= ex.getTime()) {
      // return true;
      return false;
    } else {
      return false;
    }
  }
}
