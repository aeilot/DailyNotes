package tk.louisstudio.daily_notes.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import tk.louisstudio.daily_notes.AddActivity;
import tk.louisstudio.daily_notes.DNUtils;
import tk.louisstudio.daily_notes.MainActivity;
import tk.louisstudio.daily_notes.Note;
import tk.louisstudio.daily_notes.R;
import tk.louisstudio.daily_notes.Tab;
import tk.louisstudio.daily_notes.adapter.NoteAdapter;

public class SearchFragment extends Fragment {
  private List<Note> ntl;
  private ListView listView;

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_search, container, false);
    SearchView sv = root.findViewById(R.id.mainSearch);
    listView = root.findViewById(R.id.queryL);
    listView.setDivider(null);
    listView.setEmptyView(root.findViewById(R.id.empty2));
    sv.setImeOptions(3);
    sv.setIconifiedByDefault(false);
    sv.setQueryHint(getString(R.string.search));
    sv.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            int res = DNUtils.isValidDate(query);
            if (res != -1) {
              try {
                Date dt = DNUtils.parseDate(res, query);
                ntl = MainActivity.getInstance().getDB().queryNotes(dt);
              } catch (ParseException e) {
                ntl = MainActivity.getInstance().getDB().queryNotes(query);
              }
            } else if (query.startsWith("tags=")) {
              query = query.substring(5, query.length());
              String[] tags = query.split(",");
              ntl = MainActivity.getInstance().getDB().queryNotes(tags);
            } else if (query.startsWith("title=")) {
              query = query.substring(6, query.length());
              ntl = MainActivity.getInstance().getDB().queryNotesTitle(query);
            } else {
              ntl = MainActivity.getInstance().getDB().queryNotes(query);
            }
            refresh(ntl);
            return true;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
            return false;
          }
        });
    listView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Note current = ntl.get(i);
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
    TextView tag = root.findViewById(R.id.tagText);
    tag.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            sv.setQuery("tags=", false);
          }
        });
    DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd");
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    String toDate = dateFormat.format(calendar.getTime());
    calendar.set(Calendar.HOUR_OF_DAY, -24);
    String yesterdayDate = dateFormat.format(calendar.getTime());
    TextView yest = root.findViewById(R.id.yesterText);
    yest.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            sv.setQuery(yesterdayDate, true);
          }
        });
    TextView today = root.findViewById(R.id.todayText);
    today.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            sv.setQuery(toDate, true);
          }
        });
    TextView dat = root.findViewById(R.id.dateSText);
    dat.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            sv.setQuery("2020 01 01", false);
          }
        });
    TextView tit = root.findViewById(R.id.titleText);
    tit.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            sv.setQuery("title=", false);
          }
        });
    return root;
  }

  private void refresh(List<Note> n) {
    if (listView != null) {
      listView.setAdapter(new NoteAdapter(getContext(), R.layout.listlayout, n));
      listView.invalidate();
    }
  }
}
