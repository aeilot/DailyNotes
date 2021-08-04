package tk.louisstudio.daily_notes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DNUtils {
  public static void transparencyBar(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = activity.getWindow();
      window.clearFlags(
          WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
              | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      window
          .getDecorView()
          .setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
      window.setNavigationBarColor(Color.TRANSPARENT);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window window = activity.getWindow();
      window.setFlags(
          WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
          WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  public static String getUriOfFirstPhoto(String Article) {
    int gantan = -1, zuo = -1, you = -1;
    while (true) {
      gantan = Article.indexOf('!', gantan + 1);
      if (gantan < 0) break;
      if (Article.charAt(gantan + 1) == '[') {
        zuo = gantan + 1;
        you = Article.indexOf(']', zuo);
        if (Article.charAt(you + 1) == '(') {
          zuo = you + 1;
          you = Article.indexOf(')', zuo);
          return Article.substring(zuo + 1, you);
        }
      }
    }
    return null;
  }

  public static boolean isKeyboardShown() {
    // 获取当前屏幕内容的高度
    int screenHeight = MainActivity.getInstance().getWindow().getDecorView().getHeight();
    // 获取View可见区域的bottom
    Rect rect = new Rect();
    // DecorView即为activity的顶级view
    MainActivity.getInstance().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
    // 考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
    // 选取screenHeight*2/3进行判断
    return screenHeight * 2 / 3 > rect.bottom;
  }

  public static void keyboardUp(View view) {
    InputMethodManager imm =
        (InputMethodManager)
            MainActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
  }

  public static int isValidDate(String str) {
    Calendar calendar = Calendar.getInstance();
    if (str.length() == 5) {
      str = Integer.toString(calendar.get(Calendar.YEAR)) + str.charAt(2) + str;
    }
    int convertSuccess = -1;
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    try {
      formatDate.parse(str);
      return 1;
    } catch (ParseException e) {
      convertSuccess = -1;
    }
    formatDate = new SimpleDateFormat("yyyy.MM.dd");
    try {
      formatDate.parse(str);
      return 2;
    } catch (ParseException e) {
      convertSuccess = -1;
    }
    formatDate = new SimpleDateFormat("yyyy MM dd");
    try {
      formatDate.parse(str);
      return 3;
    } catch (ParseException e) {
      convertSuccess = -1;
    }
    return convertSuccess;
  }

  public static Date parseDate(int type, String str) throws ParseException {
    SimpleDateFormat formatDate = null;
    switch (type) {
      case 1:
        formatDate = new SimpleDateFormat("yyyy-MM-dd");
        break;
      case 2:
        formatDate = new SimpleDateFormat("yyyy.MM.dd");
        break;
      case 3:
        formatDate = new SimpleDateFormat("yyyy MM dd");
        break;
      default:
        break;
    }
    Date date = formatDate.parse(str);
    return date;
  }
}
