package tk.louisstudio.daily_notes;

import java.text.SimpleDateFormat;
import java.util.*;

// 本文件是 Note 的结构，谨慎修改
public class Note {
  private int id;
  private String article;
  private Date date;
  private String title;
  private List<String> tags = new ArrayList<String>();

  public Note(int id, Date date, String article, String title, String tag) {
    this.id = id;
    this.article = article;
    this.date = date;
    this.title = title;
    setTags(tag);
  }

  public String getArticle() {
    return article;
  }

  public void setArticle(String article) {
    this.article = article;
  }

  public String getTitle() {
    if (title == null) {
      return "TITLE";
    }
    return title;
  }

  public void setTitle(String title) {
    title = title.replace("\n", "");
    this.title = title;
  }

  public Date getDate() {
    return date;
  }

  public int getId() {
    return id;
  }

  public String getFormattedTags() {
    if (tags == null) return "";
    StringBuilder tg = new StringBuilder();
    for (int i = 0; i < tags.size(); i++) {
      tg.append(tags.get(i)).append(",");
    }
    if (tg.length() < 1) return "";
    return tg.substring(0, tg.length() - 1);
  }

  public void setTags(String str) {
    if (str != null) {
      String[] res = str.split(",");
      tags.addAll(Arrays.asList(res));
    }
  }

  public List<String> getTags() {
    return tags;
  }

  public String getShorttenAtricle() {
    String rv = "";
    if (article == null || article.length() < 300) rv = article;
    else rv = article.substring(0, 300);
    return rv;
  }

  public String getShorttenTitle() {
    String rv = "";
    if (title == null || title.length() < 8) rv = getTitle();
    else rv = getTitle().substring(0, 5) + "...";
    return rv;
  }

  public String getDay() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd");
    return sdf.format(date);
  }

  public String getMonth() {
    SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.US);
    return sdf.format(date);
  }

  public String getYear() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    return sdf.format(date);
  }

  public void restore() {
    tags.remove("TRASH");
    MainActivity.getInstance().getDB().saveNote(this);
  }

  public void toTrash() {
    if (!tags.contains("TRASH")) {
      tags.add("TRASH");
    }
    MainActivity.getInstance().getDB().saveNote(this);
  }
}
