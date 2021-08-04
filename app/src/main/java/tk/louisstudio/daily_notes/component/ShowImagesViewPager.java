package tk.louisstudio.daily_notes.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

/** 为了解决photoview嵌套在部分父控件时闪退的bug，github上提供的解决方案 */
public class ShowImagesViewPager extends ViewPager {
  public ShowImagesViewPager(Context context) {
    this(context, null);
  }

  public ShowImagesViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    try {
      return super.onInterceptTouchEvent(ev);
    } catch (IllegalArgumentException e) {
      // uncomment if you really want to see these errors
      // e.printStackTrace();
      return false;
    }
  }
}
