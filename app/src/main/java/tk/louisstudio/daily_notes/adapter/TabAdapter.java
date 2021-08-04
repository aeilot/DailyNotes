package tk.louisstudio.daily_notes.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import tk.louisstudio.daily_notes.AddActivity;
import tk.louisstudio.daily_notes.MainActivity;
import tk.louisstudio.daily_notes.Note;
import tk.louisstudio.daily_notes.R;
import tk.louisstudio.daily_notes.Tab;

public class TabAdapter extends ArrayAdapter<Note> {
  private int resourceId;

  public TabAdapter(Context context, int textViewResourceId, List<Note> objects) {
    super(context, textViewResourceId, objects);
    resourceId = textViewResourceId;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
    TextView tv = view.findViewById(R.id.tabNo);
    tv.setText(Tab.instance.tabs.get(position).getShorttenTitle());
    tv.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            MainActivity.getInstance().setCurrentNote(Tab.instance.tabs.get(position));
            Log.d("ONCLICK", "ON");
            AddActivity.instance.refresh();
            AddActivity.instance.dismiss();
          }
        });
    TextView close = view.findViewById(R.id.close);
    close.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Tab.instance.delete(position);
            AddActivity.instance.refreshList();
            AddActivity.instance.dismiss();
          }
        });
    return view;
  }
}
