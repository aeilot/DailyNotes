package tk.louisstudio.daily_notes.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {
  List<Fragment> list;

  public HomePagerAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> list) {
    super(fm, behavior);
    this.list = list;
  }

  @Override
  public void setPrimaryItem(ViewGroup container, int position, Object object) {
    super.setPrimaryItem(container, position, object);
  }

  @NonNull
  @Override
  public Fragment getItem(int position) {
    return list.get(position);
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    super.destroyItem(container, position, object);
  }
}
