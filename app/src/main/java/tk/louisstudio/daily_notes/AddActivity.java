package tk.louisstudio.daily_notes;

import static tk.louisstudio.daily_notes.MainActivity.getInstance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.flipboard.bottomsheet.BottomSheetLayout;
import java.io.File;
import java.util.List;
import tk.louisstudio.daily_notes.adapter.TabAdapter;
import tk.louisstudio.daily_notes.component.ShowImagesDialog;
import ws.vinta.pangu.Pangu;

public class AddActivity extends AppCompatActivity {
  private Note cn;
  public static AddActivity instance;
  private WebView wv;
  private ListView lv;
  private BottomSheetLayout bottomSheetLayout;
  private boolean isSource;
  private EditText et;
  private View bottomSheet;
  private boolean stop = false;
  private EditText ett;

  public void refresh() {
    cn = MainActivity.getInstance().getCurrentNote();
    setup(cn.getArticle());
    isSource = false;
    wv.setVisibility(View.VISIBLE);
    et.setVisibility(View.GONE);
    wv.requestFocus();
    wv.requestFocusFromTouch();
  }

  public void hideSoftKeyboard() {
    Activity activity = AddActivity.this;
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(
          view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  public void dismiss() {
    bottomSheetLayout.dismissSheet();
  }

  public static int dp2px(float dpValue) {
    return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
  }

  private View createBottomSheetView() {
    View view =
        LayoutInflater.from(AddActivity.this).inflate(R.layout.tabsheet, bottomSheetLayout, false);
    TabAdapter adapters = new TabAdapter(getBaseContext(), R.layout.tablayout, Tab.instance.tabs);
    lv = view.findViewById(R.id.tabList);
    lv.setAdapter(adapters);
    lv.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.getInstance().setCurrentNote(Tab.instance.tabs.get(position));
            AddActivity.instance.refresh();
            AddActivity.instance.dismiss();
          }
        });
    return view;
  }

  public static String formatter(String str) {
    Pangu pg = new Pangu();
    return pg.spacingText(str);
  }

  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface", "JavascriptInterface"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add);
    instance = AddActivity.this;
    SharedPreferences getPrefs = getBaseContext().getSharedPreferences("settings", MODE_PRIVATE);
    isSource = false;
    // EditText et = findViewById(R.id.edt_text);
    cn = MainActivity.getInstance().getCurrentNote();
    wv = findViewById(R.id.et);
    wv.getSettings().setJavaScriptEnabled(true);
    wv.addJavascriptInterface(new JsInterface(), "androidJS");
    wv.setWebChromeClient(new WebChromeClient());
    wv.setWebViewClient(
        new WebViewClient() {
          @Override
          public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setup(cn.getArticle());
          }

          @Override
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.equals("file:///android_asset/index.html") || !url.startsWith("javascript:")) {
              Uri uri = Uri.parse(url);
              Intent intent = new Intent(Intent.ACTION_VIEW, uri);
              startActivity(intent);
              return true;
            }
            return false;
          }

          @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
          @Override
          public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (!url.equals("file:///android_asset/index.html") || !url.startsWith("javascript:")) {
              Uri uri = Uri.parse(url);
              Intent intent = new Intent(Intent.ACTION_VIEW, uri);
              startActivity(intent);
              return true;
            }
            return false;
          }
        });
    wv.getSettings().setAllowContentAccess(true);
    wv.getSettings().setAllowFileAccess(true);
    wv.loadUrl("file:///android_asset/index.html");
    ImageButton tab = findViewById(R.id.tab);
    bottomSheetLayout = findViewById(R.id.tabSheet);
    tab.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d("BOTTOM", "UP");
            hideSoftKeyboard();
            bottomSheet = createBottomSheetView();
            bottomSheetLayout.showWithSheetView(bottomSheet);
          }
        });
    int sel = getPrefs.getInt("autosave", 10);
    Thread t =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                Looper.prepare();
                do {
                  try {
                    Thread.sleep(sel * 100);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  runOnUiThread(
                      new Runnable() {
                        @Override
                        public void run() {
                          arti(
                              new Callback() {
                                @Override
                                public void onResult(String value) {
                                  Note note = cn;
                                  note.setArticle(value);
                                  note.setTitle(ett.getText().toString());
                                  MainActivity.getInstance().getDB().saveNote(note);
                                }
                              });
                        }
                      });
                } while (!stop);
                Looper.loop();
              }
            });
    if (sel >= 0) {
      t.start();
    }
    ett = findViewById(R.id.titleT);
    boolean center = getPrefs.getBoolean("title_center", false);
    if (center) {
      ett.setGravity(Gravity.CENTER);
    }
    ett.setText(cn.getTitle());
    ImageButton che = findViewById(R.id.check);
    che.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            arti(
                new Callback() {
                  @Override
                  public void onResult(String value) {
                    Note note = cn;
                    note.setArticle(value);
                    note.setTitle(ett.getText().toString());
                    MainActivity.getInstance().getDB().saveNote(note);
                    MainActivity.getInstance().updateList();
                    // Tab.instance.tabs.remove(note);
                    Log.d("UPDATED", "UP");
                    stop = true;
                    finish();
                  }
                });
          }
        });
    Button h1 = findViewById(R.id.h1);
    Button h2 = findViewById(R.id.h2);
    Button h3 = findViewById(R.id.h3);
    Button h4 = findViewById(R.id.h4);
    Button h5 = findViewById(R.id.h5);
    Button h6 = findViewById(R.id.h6);
    Button md = findViewById(R.id.md);
    et = findViewById(R.id.edt_text);
    //        ImageButton undo =  findViewById(R.id.undo);
    //        ImageButton redo =  findViewById(R.id.redo);
    ImageButton format = findViewById(R.id.format);
    ImageButton listb = findViewById(R.id.listb);
    ImageButton italic = findViewById(R.id.italic);
    ImageButton listn = findViewById(R.id.listn);
    ImageButton link = findViewById(R.id.link);
    ImageButton codeb = findViewById(R.id.codeb);
    ImageButton bold = findViewById(R.id.bold);
    ImageButton quote = findViewById(R.id.quote);
    ImageButton bk = findViewById(R.id.bk);
    ImageButton image = findViewById(R.id.image);
    ImageButton camera = findViewById(R.id.camera);
    ImageButton preview = findViewById(R.id.preview);
    et.setFocusable(true);
    et.setFocusableInTouchMode(true);
    wv.setFocusable(true);
    wv.setFocusableInTouchMode(true);
    format.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (isSource) {
              et.setText(formatter(et.getText().toString()));
            } else {
              arti(
                  new Callback() {
                    @Override
                    public void onResult(String value) {
                      setup(formatter(value));
                    }
                  });
            }
          }
        });
    preview.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            preview();
          }
        });
    md.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            editSource();
          }
        });
    h1.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("# Text");
          }
        });
    h2.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("## Text");
          }
        });
    h3.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("### Text");
          }
        });
    h4.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("#### Text");
          }
        });
    h5.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("##### Text");
          }
        });
    h6.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("###### Text");
          }
        });
    //        redo.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View view) {
    //                redo();
    //            }
    //        });
    //        undo.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View view) {
    //                undo();
    //            }
    //        });
    listb.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("- Text");
          }
        });
    listn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("1. Text");
          }
        });
    link.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("[Alt](https://)");
          }
        });
    codeb.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("```Text```");
          }
        });
    bold.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("**Text**");
          }
        });
    quote.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("> Text");
          }
        });
    bk.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("~~Text~~");
          }
        });
    image.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 2);
          }
        });
    camera.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            gotoCamera();
          }
        });
    italic.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sendToJS("*Text*");
          }
        });
    boolean on = getPrefs.getBoolean("always_on", false);
    if (on) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }

  private File cameraSavePath; // 拍照照片路径
  private Uri uripic; // 照片uri
  public Uri uri;
  public String pah;

  public void gotoCamera() {
    cameraSavePath =
        new File(
            Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg");

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      // 第二个参数为 包名.fileprovider
      uripic =
          FileProvider.getUriForFile(
              AddActivity.this, "tk.louisstudio.daily_notes.FileProvider", cameraSavePath);
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    } else {
      uripic = Uri.fromFile(cameraSavePath);
    }
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uripic);
    AddActivity.this.startActivityForResult(intent, 1);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1 && resultCode == RESULT_OK) {
      String photoPath;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        photoPath = String.valueOf(cameraSavePath);
      } else {
        photoPath = uripic.getEncodedPath();
      }
      pah = photoPath;
    }
    if (requestCode == 2) {
      // 从相册返回的数据
      if (data != null) {
        // 得到图片的全路径
        uri = data.getData();
        String path = getFilePathFromContentUri(uri, getContentResolver());
        pah = path;
      }
    }
    pah = "file://" + pah;
    sendToJS("![Alt](" + pah + ")");
    super.onActivityResult(requestCode, resultCode, data);
  }

  public static String getFilePathFromContentUri(
      Uri selectedVideoUri, ContentResolver contentResolver) {
    String filePath;
    String[] filePathColumn = {MediaStore.MediaColumns.DATA};

    Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
    //      也可用下面的方法拿到cursor
    //      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null,
    // null);

    cursor.moveToFirst();

    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    filePath = cursor.getString(columnIndex);
    cursor.close();
    return filePath;
  }

  public static class JsInterface {
    JsInterface() {}

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void setValue(List<String> ls, int index) {
      new ShowImagesDialog(AddActivity.instance, ls, index);
    }
  }

  interface Callback {
    void onResult(String value);
  };

  public void arti(Callback callback) {
    wv.evaluateJavascript(
        "javascript:getArticle();",
        new ValueCallback<String>() {
          @Override
          public void onReceiveValue(String s) {
            // s是JS方法的返回值
            s = format(s);
            callback.onResult(s);
          }
        });
  }

  public void sendToJS(String msg) {
    if (msg == null) return;
    if (isSource) {
      int index = et.getSelectionStart();
      Editable editable = et.getText();
      editable.insert(index, msg);
    } else {
      msg = convert(msg);
      wv.loadUrl("javascript:insert(" + "\"" + msg + "\"" + ");");
    }
  }

  public void setup(String msg) {
    msg = convert(msg);
    msg = format(msg);
    wv.loadUrl("javascript:msetup(" + "\"" + msg + "\"" + ");");
    Log.d("SETUP", "SETUP CALLED");
  }

  public void editSource() {
    if (isSource) {
      wv.setVisibility(View.VISIBLE);
      setup(et.getText().toString());
      et.setVisibility(View.GONE);
      wv.requestFocus();
      wv.requestFocusFromTouch();
      isSource = false;
    } else {
      arti(
          new Callback() {
            @Override
            public void onResult(String value) {
              wv.setVisibility(View.GONE);
              et.setVisibility(View.VISIBLE);
              et.setText(value);
              et.requestFocus();
              et.requestFocusFromTouch();
              isSource = true;
            }
          });
    }
  }

  public void preview() {
    wv.loadUrl("javascript:preview();");
  }
  //    public void redo() {
  //        wv.evaluateJavascript("javascript:redo()", new ValueCallback<String>() {
  //            @Override
  //            public void onReceiveValue(String s) {
  //                //s是JS方法的返回值
  //            }
  //        });
  //    }
  //    public void undo() {
  //        wv.evaluateJavascript("javascript:undo()", new ValueCallback<String>() {
  //            @Override
  //            public void onReceiveValue(String s) {
  //                //s是JS方法的返回值
  //            }
  //        });
  //    }
  public void refreshList() {
    if (lv != null) {
      lv.setAdapter(
          new TabAdapter(
              getInstance().getApplicationContext(), R.layout.tablayout, Tab.instance.tabs));
      lv.invalidate();
    }
  }

  long exitTime;

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // SharedPreferences getPrefs = getBaseContext().getSharedPreferences("settings", MODE_PRIVATE);
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
      if ((System.currentTimeMillis() - exitTime) > 2000) {
        Toast.makeText(
                AddActivity.this, getString(R.string.click_agian_quit_app), Toast.LENGTH_SHORT)
            .show();
        exitTime = System.currentTimeMillis();
      } else {
        arti(
            new Callback() {
              @Override
              public void onResult(String value) {
                stop = true;
                Note note = cn;
                note.setArticle(value);
                note.setTitle(ett.getText().toString());
                MainActivity.getInstance().getDB().saveNote(note);
                finish();
                MainActivity.getInstance().updateList();
              }
            });
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onDestroy() {
    if (wv != null) {
      // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
      // destory()
      ViewParent parent = wv.getParent();
      if (parent != null) {
        ((ViewGroup) parent).removeView(wv);
      }
      wv.stopLoading();
      // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
      wv.getSettings().setJavaScriptEnabled(false);
      wv.clearHistory();
      wv.clearView();
      wv.removeAllViews();
      wv.destroy();
    }
    super.onDestroy();
  }

  public static String format(String unicode) {
    unicode = unicode.replace("\\n", "\n");
    unicode = unicode.replace("\"", "");
    //        unicode = unicode.replace("\\\\","");
    unicode = unicode.replace("\\u003C", "<");
    String regex = "<[^>]*>";
    unicode = unicode.replaceAll(regex, "");
    return unicode;
  }

  public static String convert(String string) {

    StringBuffer unicode = new StringBuffer();

    for (int i = 0; i < string.length(); i++) {

      // 取出每一个字符
      char c = string.charAt(i);

      // 转换为unicode
      unicode.append(String.format("\\u%04x", Integer.valueOf(c)));
    }

    return unicode.toString();
  }
}
