package com.lesforest.apps.mirrapp.components;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class AdapterImages extends PagerAdapter {

  private List<String> data;

  public AdapterImages(List<String> data) {
    super();
    this.data = data;
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {

    PhotoView photoView = new PhotoView(container.getContext());

    final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

    String currentLink = data.get(position);

    Timber.i(currentLink);

    Picasso.with(container.getContext())
        .load(currentLink)
        //.placeholder(R.drawable.ic_loop)
        .into(photoView, new Callback() {
      @Override public void onSuccess() {
        attacher.update();
      }

      @Override public void onError() {
        // just ignore, nothing to do
      }
    });

    container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    return photoView;
  }

  @Override public int getCount() {
    return null == data ? 0 : data.size();
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  public void setData(List<String> data) {
    this.data = data;
    notifyDataSetChanged();
  }
}