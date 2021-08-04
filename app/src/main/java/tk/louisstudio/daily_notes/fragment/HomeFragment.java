package tk.louisstudio.daily_notes.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import com.baoyz.widget.PullRefreshLayout;
import com.flipboard.bottomsheet.BottomSheetLayout;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import tk.louisstudio.daily_notes.AddActivity;
import tk.louisstudio.daily_notes.MainActivity;
import tk.louisstudio.daily_notes.Note;
import tk.louisstudio.daily_notes.R;
import tk.louisstudio.daily_notes.Tab;
import tk.louisstudio.daily_notes.adapter.NoteAdapter;

public class HomeFragment extends Fragment {
  private ListView listView;
  private View bottomSheet;
  private BottomSheetLayout bottomSheetLayout;

  private View createBottomSheetView() {
    Note note = MainActivity.getInstance().getCurrentNote();
    View view =
        LayoutInflater.from(MainActivity.getInstance())
            .inflate(R.layout.homesheet, bottomSheetLayout, false);
    TextView date = view.findViewById(R.id.dateTT);
    TextView title = view.findViewById(R.id.titleTT);
    date.setText(note.getMonth() + " " + note.getDay() + ", " + note.getYear());
    title.setText(note.getShorttenTitle());
    TextView words = view.findViewById(R.id.word);
    words.setText(Integer.toString(note.getArticle().length()));
    TextView tags = view.findViewById(R.id.tags);
    tags.setText(note.getFormattedTags());
    // Buttons
    ImageButton ib = view.findViewById(R.id.edt_tags);
    ib.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            final EditText editText = new EditText(getContext());
            editText.setText(note.getFormattedTags());
            AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.change_tags))
                    .setView(editText)
                    .setPositiveButton(
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                            note.setTags(editText.getText().toString());
                            tags.setText(note.getFormattedTags());
                            MainActivity.getInstance().getDB().saveNote(note);
                            MainActivity.getInstance().updateList();
                          }
                        });
            builder.create().show();
          }
        });
    LinearLayout share = view.findViewById(R.id.share);
    LinearLayout trash = view.findViewById(R.id.trash);
    LinearLayout del = view.findViewById(R.id.del);
    ImageButton share2 = view.findViewById(R.id.share2);
    ImageButton trash2 = view.findViewById(R.id.trash2);
    ImageButton del2 = view.findViewById(R.id.del2);
    TextView share3 = view.findViewById(R.id.share3);
    TextView trash3 = view.findViewById(R.id.trash3);
    TextView del3 = view.findViewById(R.id.del3);
    share.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            new Share2.Builder(MainActivity.getInstance())
                .setContentType(ShareContentType.TEXT)
                .setTextContent(note.getArticle())
                .setTitle(getString(R.string.share))
                .build()
                .shareBySystem();
          }
        });
    share2.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            new Share2.Builder(MainActivity.getInstance())
                .setContentType(ShareContentType.TEXT)
                .setTextContent(note.getArticle())
                .setTitle(getString(R.string.share))
                .build()
                .shareBySystem();
          }
        });
    share3.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            new Share2.Builder(MainActivity.getInstance())
                .setContentType(ShareContentType.TEXT)
                .setTextContent(note.getArticle())
                .setTitle(getString(R.string.share))
                .build()
                .shareBySystem();
          }
        });
    trash.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            note.toTrash();
            dismiss();
            updateList();
            MainActivity.getInstance().updateListC();
          }
        });
    trash2.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            note.toTrash();
            dismiss();
            updateList();
            MainActivity.getInstance().updateListC();
          }
        });
    trash3.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            note.toTrash();
            dismiss();
            updateList();
            MainActivity.getInstance().updateListC();
          }
        });
    del.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.delete));
            builder.setMessage(getString(R.string.delque));
            builder.setNeutralButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {}
                });
            builder.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    MainActivity.getInstance().getDB().deleteNote(note);
                    dismiss();
                    updateList();
                  }
                });
            builder.show();
          }
        });
    del2.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.delete));
            builder.setMessage(getString(R.string.delque));
            builder.setNeutralButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {}
                });
            builder.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    MainActivity.getInstance().getDB().deleteNote(note);
                    dismiss();
                    updateList();
                  }
                });
            builder.show();
          }
        });
    del3.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.delete));
            builder.setMessage(getString(R.string.delque));
            builder.setNeutralButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {}
                });
            builder.setPositiveButton(
                getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    MainActivity.getInstance().getDB().deleteNote(note);
                    dismiss();
                    updateList();
                  }
                });
            builder.show();
          }
        });
    return view;
  }

  public void dismiss() {
    bottomSheetLayout.dismissSheet();
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_home, container, false);
    // mFooter = LayoutInflater.from(getContext()).inflate(R.layout.footer, null);
    // mHeader = (View)getLayoutInflater().inflate(R.layout.headert,null);
    bottomSheetLayout = MainActivity.getInstance().findViewById(R.id.rty);
    NoteAdapter adapter =
        new NoteAdapter(
            getContext(), R.layout.listlayout, MainActivity.getInstance().getDB().getNotDelNotes());
    listView = root.findViewById(R.id.list_view);
    listView.setDivider(null);
    listView.setAdapter(adapter);
    listView.setEmptyView(root.findViewById(R.id.empty));
    listView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Note current = MainActivity.getInstance().getDB().getNotes().get(i);
            Tab.instance.add(current);
            MainActivity.getInstance().setCurrentNote(current);
            Intent intent = new Intent(MainActivity.getInstance(), AddActivity.class);
            ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    MainActivity.getInstance(),
                    (TextView) view.findViewById(R.id.date_text),
                    getString(R.string.transitions_name));
            startActivity(intent, optionsCompat.toBundle());
          }
        });
    listView.setOnItemLongClickListener(
        new AdapterView.OnItemLongClickListener() {
          @Override
          public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Note current = MainActivity.getInstance().getDB().getNotes().get(position);
            MainActivity.getInstance().setCurrentNote(current);
            bottomSheet = createBottomSheetView();
            bottomSheetLayout.showWithSheetView(bottomSheet);
            return true;
          }
        });
    final PullRefreshLayout layout = root.findViewById(R.id.swipeRefreshLayout);
    // listen refresh event
    SharedPreferences getPrefs = getContext().getSharedPreferences("settings", MODE_PRIVATE);
    final boolean syncEnabled = getPrefs.getBoolean("sync", false);
    layout.setOnRefreshListener(
        new PullRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            // start refresh
            Thread t =
                new Thread(
                    new Runnable() {
                      @Override
                      public void run() {
                        Looper.prepare();
                        if (syncEnabled) {
                          int result = MainActivity.getInstance().uploadwebDAV();
                          if (result == 0) {
                            MainActivity.getInstance()
                                .runOnUiThread(
                                    new Runnable() {
                                      @Override
                                      public void run() {
                                        Toast.makeText(
                                                getContext(),
                                                getString(R.string.syncCompelete),
                                                Toast.LENGTH_LONG)
                                            .show();
                                      }
                                    });
                          } else {
                            MainActivity.getInstance()
                                .runOnUiThread(
                                    new Runnable() {
                                      @Override
                                      public void run() {
                                        Toast.makeText(
                                                getContext(),
                                                getString(R.string.syncFail),
                                                Toast.LENGTH_LONG)
                                            .show();
                                      }
                                    });
                          }
                          MainActivity.getInstance()
                              .runOnUiThread(
                                  new Runnable() {
                                    @Override
                                    public void run() {
                                      layout.setRefreshing(false);
                                    }
                                  });
                        } else {
                          MainActivity.getInstance()
                              .runOnUiThread(
                                  new Runnable() {
                                    @Override
                                    public void run() {
                                      Toast.makeText(
                                              getContext(),
                                              getString(R.string.enableitfirst),
                                              Toast.LENGTH_LONG)
                                          .show();
                                    }
                                  });
                        }
                        Looper.loop();
                      }
                    });
            t.start();
          }
        });
    // refresh complete
    return root;
  }

  public void updateList() {
    if (listView != null) {
      listView.setAdapter(
          new NoteAdapter(
              getContext(),
              R.layout.listlayout,
              MainActivity.getInstance().getDB().getNotDelNotes()));
      listView.invalidate();
      Log.d("UPDATED", "INVAL");
    }
  }
}
