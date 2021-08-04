package tk.louisstudio.daily_notes.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.ArrayList;
import java.util.List;
import tk.louisstudio.daily_notes.MainActivity;
import tk.louisstudio.daily_notes.R;

/** Created by Administrator on 2017/5/3. 嵌套了viewpager的图片浏览 */
public class ShowImagesDialog extends Dialog {

  private View mView;
  private Context mContext;
  private ShowImagesViewPager mViewPager;
  private TextView mIndexText;
  private List<String> mImgUrls;
  private List<String> mTitles;
  private List<View> mViews;
  private ShowImagesAdapter mAdapter;
  private int pos;

  public ShowImagesDialog(@NonNull Context context, List<String> imgUrls, int position) {
    super(context, R.style.transparentBgDialog);
    this.mContext = context;
    this.mImgUrls = imgUrls;
    this.pos = position;
    initView();
    initData();
  }

  private void initView() {
    mView = View.inflate(mContext, R.layout.dialog_images_brower, null);
    mViewPager = (ShowImagesViewPager) mView.findViewById(R.id.vp_images);
    mIndexText = (TextView) mView.findViewById(R.id.tv_image_index);
    mTitles = new ArrayList<>();
    mViews = new ArrayList<>();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(mView);
    Window window = getWindow();
    WindowManager.LayoutParams wl = window.getAttributes();
    wl.x = 0;
    wl.y = 0;
    WindowManager manager = MainActivity.getInstance().getWindowManager();
    DisplayMetrics outMetrics = new DisplayMetrics();
    manager.getDefaultDisplay().getMetrics(outMetrics);
    int width = outMetrics.widthPixels;
    int height = outMetrics.heightPixels;
    wl.height = height;
    wl.width = width;
    wl.gravity = Gravity.CENTER;
    window.setAttributes(wl);
  }

  private void initData() {
    // 点击图片监听
    //        PhotoViewAttacher.OnPhotoTapListener listener = new
    // PhotoViewAttacher.OnPhotoTapListener() {
    //            @Override
    //            public void onPhotoTap(View view, float x, float y) {
    //                dismiss();
    //            }
    //        };
    for (int i = 0; i < mImgUrls.size(); i++) {
      final PhotoView photoView = new PhotoView(mContext);
      ViewGroup.LayoutParams layoutParams =
          new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      photoView.setLayoutParams(layoutParams);
      photoView.setOnPhotoTapListener(
          new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
              dismiss();
            }
          });
      // 点击图片外围（无图片处）监听
      /**
       * photoView.setOnViewTapListener(new OnViewTapListener() { @Override public void
       * onViewTap(View view, float x, float y){ dismiss(); } });
       */
      Glide.with(mContext)
          .load(mImgUrls.get(i))
          .placeholder(R.mipmap.ic_launcher)
          .error(R.mipmap.ic_launcher)
          .into(
              new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(
                    @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                  photoView.setImageDrawable(resource);
                }
              });
      mViews.add(photoView);
      mTitles.add(i + "");
    }

    mAdapter = new ShowImagesAdapter(mViews, mTitles);
    mViewPager.setAdapter(mAdapter);
    mIndexText.setText(1 + "/" + mImgUrls.size());
    mViewPager.setOnPageChangeListener(
        new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(
              int position, float positionOffset, int positionOffsetPixels) {}

          @Override
          public void onPageSelected(int position) {
            mIndexText.setText(position + 1 + "/" + mImgUrls.size());
          }

          @Override
          public void onPageScrollStateChanged(int state) {}
        });
    mViewPager.setCurrentItem(pos);
  }
}
