package tk.louisstudio.daily_notes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Tab {
  public ArrayList<Note> tabs;
  public static Tab instance = new Tab();

  Tab() {}

  public void add(Note n) {
    if (exist(n)) return;
    tabs.add(n);
  }

  public void delete(int index) {
    tabs.remove(index);
  }

  public String getDate(int index) {
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy MM dd");
    String n = formatDate.format(tabs.get(index).getDate());
    return n;
  }

  public boolean exist(Note n) {
    return tabs.contains(n);
  }
}
