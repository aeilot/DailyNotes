package tk.louisstudio.daily_notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 本文件请不要擅自修改
public class NoteDB {
  private SQLiteDatabase db;
  private Context context;
  private List<Note> notes = new ArrayList<Note>();
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  public NoteDB(Context context, String path) {
    this.context = context;
    load(context, path);
  }

  public void load(Context context, String path) {
    db = context.openOrCreateDatabase(path, 0, null);
    // load note list
    String sql =
        "create table if not exists note_table(id integer primary key autoincrement,t text default null,article blob default null,tit text default null,tag text default null);";
    db.execSQL(sql);
    try {
      db.query("note_table", new String[] {"tit"}, null, null, null, "", "id desc limit 32");
    } catch (Exception e) {
      e.printStackTrace();
      db.execSQL("alter table note_table add tit text default null");
      db.execSQL("alter table note_table add tag text default null");
    }
    notes = new ArrayList<Note>();
    Cursor cursor = db.query("note_table", null, null, null, null, "", "id desc limit 32");
    while (cursor.moveToNext()) {
      int id = cursor.getInt(0);
      String t = cursor.getString(1);
      byte[] bytes = cursor.getBlob(2);
      String title = cursor.getString(3);
      String tags = cursor.getString(4);
      String article = bytes == null ? "" : new String(bytes);
      Date dt = new Date();
      try {
        dt = sdf.parse(t);
      } catch (Exception e) {
        e.printStackTrace();
      }
      notes.add(new Note(id, dt, article, title, tags));
    }
    if (notes.isEmpty()) {
      Note note =
          new Note(
              1,
              new Date(),
              MainActivity.getInstance().getString(R.string.initNote),
              "Intro",
              null);
      sql = "insert into note_table values(?,?,?,?,?)";
      Object[] args = {
        note.getId(),
        sdf.format(note.getDate()),
        note.getArticle().getBytes(),
        note.getTitle(),
        note.getFormattedTags()
      };
      db.execSQL(sql, args);
      notes.add(note);
    }
  }

  public void close() {
    db.close();
  }

  public List<Note> getNotes() {
    return notes;
  }

  public Note readNote(int id) {
    if (db == null || !db.isOpen()) return null;
    String[] args = {String.valueOf(id)};
    Cursor cursor = db.query("note_table", null, "id=?", args, null, "", "");
    Note note = null;
    if (cursor.moveToNext()) {
      String t = cursor.getString(1);
      byte[] bytes = cursor.getBlob(2);
      String title = cursor.getString(3);
      String tags = cursor.getString(4);
      String article = bytes == null ? "" : new String(bytes);
      Date dt = new Date();
      try {
        dt = sdf.parse(t);
      } catch (Exception e) {
        e.printStackTrace();
      }
      note = new Note(id, dt, article, title, tags);
    }
    return note;
  }

  public Note createNote() {
    Date now = new Date();
    String t = sdf.format(now);
    String[] columns = {"seq"};
    String[] args = {"note_table"};
    Cursor cursor = db.query("sqlite_sequence", columns, "name=?", args, null, null, null);
    int id = 0;
    if (cursor.moveToNext()) {
      id = cursor.getInt(0);
    }
    id++;
    db.execSQL(String.format("insert into note_table(id,t) values(%d,'%s')", id, t));
    SharedPreferences getPrefs =
        MainActivity.getInstance()
            .getBaseContext()
            .getSharedPreferences("settings", Context.MODE_PRIVATE);
    String setUpTemplate = getPrefs.getString("template", "Hello World");
    String title = getPrefs.getString("title_template", "TITLE");
    Note note = new Note(id, now, setUpTemplate, title, null);
    notes.add(0, note);
    return note;
  }

  public void saveNote(Note note) {
    String sql = "update note_table set article=?, tit=?, tag=? where id=?";
    Object[] args = {
      note.getArticle().getBytes(), note.getTitle(), note.getFormattedTags(), note.getId()
    };
    db.execSQL(sql, args);
  }

  public List<Note> queryNotes(Date time) {
    List _notes = new ArrayList<Note>();
    String st = sdf.format(time);
    String sel = String.format("date(t)>=date('%s') and date(t)<=date('%s')", st, st);
    String[] cols = {"id", "t", "article"};
    Cursor cursor = db.query("note_table", cols, sel, null, null, null, null);
    // Cursor cursor=db.query("note_table",null,"t>? and t<?",args,null,null,null);
    while (cursor.moveToNext()) {
      int id = cursor.getInt(0);
      String t = cursor.getString(1);
      byte[] bytes = cursor.getBlob(2);
      String title = cursor.getString(3);
      String tags = cursor.getString(4);
      String article = bytes == null ? "" : new String(bytes);
      Date dt = new Date();
      try {
        dt = sdf.parse(t);
      } catch (Exception e) {
        e.printStackTrace();
      }
      _notes.add(new Note(id, dt, article, title, tags));
    }
    return _notes;
  }

  public List<Note> queryNotes(String k) {
    List<Note> ntl = getNotes();
    List<Note> res = new ArrayList<Note>();
    for (int i = 0; i < ntl.size(); i++) {
      if (ntl.get(i).getArticle().toLowerCase().contains(k.toLowerCase())) {
        res.add(ntl.get(i));
      }
    }
    return res;
  }

  public List<Note> queryNotes(String[] k) {
    List<Note> ntl = getNotes();
    List<Note> res = new ArrayList<Note>();
    int count = 0;
    for (int i = 0; i < ntl.size(); i++) {
      count = 0;
      for (int j = 0; j < k.length; j++) {
        if (ntl.get(i).getTags().contains(k[j])) {
          count++;
        }
      }
      if (count == k.length) {
        res.add(ntl.get(i));
      }
    }
    return res;
  }

  public List<Note> getNotDelNotes() {
    List<Note> ln = new ArrayList<>();
    for (int i = 0; i < notes.size(); i++) {
      if (!notes.get(i).getTags().contains("TRASH")) {
        ln.add(notes.get(i));
      }
    }
    return ln;
  }

  public List<Note> queryNotesTitle(String title) {
    List<Note> ntl = getNotes();
    List<Note> res = new ArrayList<Note>();
    for (int i = 0; i < ntl.size(); i++) {
      if (ntl.get(i).getTitle().toLowerCase().contains(title.toLowerCase())) {
        res.add(ntl.get(i));
      }
    }
    return res;
  }

  public void deleteNote(Note note) {
    db.execSQL(String.format("delete from note_table where id=%d", note.getId()));
    for (int i = notes.size() - 1; i >= 0; i--) {
      if (notes.get(i).getId() == note.getId()) {
        notes.remove(i);
        break;
      }
    }
  }
}
