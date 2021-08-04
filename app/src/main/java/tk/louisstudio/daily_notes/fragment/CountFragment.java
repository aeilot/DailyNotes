package tk.louisstudio.daily_notes.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.flipboard.bottomsheet.BottomSheetLayout;
import java.util.List;
import tk.louisstudio.daily_notes.MainActivity;
import tk.louisstudio.daily_notes.Note;
import tk.louisstudio.daily_notes.R;
import tk.louisstudio.daily_notes.adapter.NoteAdapter;

public class CountFragment extends Fragment {
  private ListView listView;
  private AlertDialog.Builder builder;
  private List<Note> ntl;
  private View bottomSheet;
  private BottomSheetLayout bottomSheetLayout;

  private View createBottomSheetView() {
    View view =
        LayoutInflater.from(MainActivity.getInstance())
            .inflate(R.layout.trashsheet, bottomSheetLayout, false);
    Note note = MainActivity.getInstance().getCurrentNote();
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
            AlertDialog.Builder builder2 =
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
            builder2.create().show();
          }
        });
    LinearLayout dele = view.findViewById(R.id.dele);
    LinearLayout restore = view.findViewById(R.id.restore);
    LinearLayout deleA = view.findViewById(R.id.deleA);
    LinearLayout restoreA = view.findViewById(R.id.restoreA);
    dele.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            MainActivity.getInstance()
                .getDB()
                .deleteNote(MainActivity.getInstance().getCurrentNote());
            dismiss();
            updateList();
          }
        });
    restore.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            note.restore();
            dismiss();
            updateList();
            MainActivity.getInstance().updateList();
          }
        });
    deleA.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            delALL();
            dismiss();
            updateList();
          }
        });
    restoreA.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            resALL();
            dismiss();
            updateList();
            MainActivity.getInstance().updateList();
          }
        });
    return view;
  }

  public void dismiss() {
    bottomSheetLayout.dismissSheet();
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View root = inflater.inflate(R.layout.fragment_count, container, false);
    bottomSheetLayout = MainActivity.getInstance().findViewById(R.id.rty);
    ntl = MainActivity.getInstance().getDB().queryNotes(new String[] {"TRASH"});
    NoteAdapter adapter = new NoteAdapter(getContext(), R.layout.listlayout, ntl);
    listView = root.findViewById(R.id.trash);
    listView.setDivider(null);
    listView.setAdapter(adapter);
    listView.setEmptyView(root.findViewById(R.id.empty3));
    listView.setOnItemLongClickListener(
        (parent, view, position, id) -> {
          Note current = MainActivity.getInstance().getDB().getNotes().get(position);
          MainActivity.getInstance().setCurrentNote(current);
          bottomSheet = createBottomSheetView();
          bottomSheetLayout.showWithSheetView(bottomSheet);
          return true;
        });
    listView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            del(position);
            updateList();
          }
        });
    return root;
  }

  public void del(int pos) {
    builder =
        new AlertDialog.Builder(MainActivity.getInstance())
            .setTitle(getString(R.string.option))
            .setPositiveButton(
                getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.getInstance().getDB().deleteNote(ntl.get(pos));
                    dialogInterface.dismiss();
                    updateList();
                    MainActivity.getInstance().updateList();
                  }
                })
            .setNegativeButton(
                getString(R.string.restore),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    ntl.get(pos).restore();
                    dialogInterface.dismiss();
                    updateList();
                    MainActivity.getInstance().updateList();
                  }
                });
    builder.create().show();
  }

  public void delALL() {
    builder =
        new AlertDialog.Builder(MainActivity.getInstance())
            .setTitle(getString(R.string.delque))
            .setPositiveButton(
                getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    for (int j = 0; j < ntl.size(); j++) {
                      MainActivity.getInstance().getDB().deleteNote(ntl.get(j));
                    }
                    dialogInterface.dismiss();
                    updateList();
                  }
                })
            .setNegativeButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                });
    builder.create().show();
  }

  public void resALL() {
    builder =
        new AlertDialog.Builder(MainActivity.getInstance())
            .setTitle(getString(R.string.resque))
            .setPositiveButton(
                getString(R.string.restore),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    for (int j = 0; j < ntl.size(); j++) {
                      ntl.get(j).restore();
                    }
                    dialogInterface.dismiss();
                  }
                })
            .setNegativeButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                });
    builder.create().show();
  }

  public void updateList() {
    if (listView != null) {
      ntl = MainActivity.getInstance().getDB().queryNotes(new String[] {"TRASH"});
      listView.setAdapter(new NoteAdapter(getContext(), R.layout.listlayout, ntl));
      listView.invalidate();
    }
  }
}
