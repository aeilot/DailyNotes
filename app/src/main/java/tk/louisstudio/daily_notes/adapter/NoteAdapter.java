package tk.louisstudio.daily_notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import tk.louisstudio.daily_notes.Note;
import tk.louisstudio.daily_notes.R;

public class NoteAdapter extends ArrayAdapter<Note> {
  private int resourceId;

  public NoteAdapter(Context context, int textViewResourceId, List<Note> objects) {
    super(context, textViewResourceId, objects);
    resourceId = textViewResourceId;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final Note note = getItem(position); // 获取当前项的Note实例
    View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
    TextView article = view.findViewById(R.id.article_text);
    TextView date = view.findViewById(R.id.date_text);
    TextView title = view.findViewById(R.id.title_text);
    article.setText(note.getShorttenAtricle());
    date.setText(note.getMonth() + " " + note.getDay() + ", " + note.getYear());
    title.setText(note.getTitle());
    return view;
  }
}
