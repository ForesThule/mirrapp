package com.lesforest.apps.mirrapp.components;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.desmond.squarecamera.CameraActivity;
import com.lesforest.apps.mirrapp.Cv;
import com.lesforest.apps.mirrapp.R;
import com.lesforest.apps.mirrapp.ui.ClaimActivity;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import timber.log.Timber;

public class AlbumPresenter {
  private Uri currentPhotoUri;
  private ClaimActivity activity;
  public AlertDialog photoChooserDialog;
  PhotoAlbum photoAlbum;

  public AlbumPresenter(ClaimActivity act, PhotoAlbum photoAlbum) {
    activity = act;
    this.photoAlbum = photoAlbum;
  }

  public void takeNewPhoto(int request) {

    if (photoAlbum.getData().size() < Cv.PHOTO_FACT_LIMIT) {

//      assignImageUri();

      Intent takePhotoIntent = new Intent(activity, CameraActivity.class);

      if (null != takePhotoIntent.resolveActivity(activity.getPackageManager())) {
        activity.startActivityForResult(takePhotoIntent, request);
      }
    } else {
      showWarningExceedPhotoLimit();
    }
  }


  public void getPhotoFromGallery() {

    int size = photoAlbum.getData().size();

    if (size < Cv.PHOTO_FACT_LIMIT) {

      Matisse.from(activity)
          .choose(MimeType.allOf())
          .countable(true)
          .maxSelectable(Cv.PHOTO_FACT_LIMIT - size)
          //                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
          .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
          .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
          .thumbnailScale(1.0f)
          .imageEngine(new PicassoEngine())
          .theme(R.style.Matisse_rs)
          .forResult(Cv.REQUEST_GALLERY);

    } else {
      showWarningExceedPhotoLimit();
    }
  }

  public void onSquareCameraResult(Intent intent) {

    Uri uri = intent.getData();

    try {

      saveCurrentPhotoUri(uri);

      activity.getAlbumViewer().setViewPagerController();
      //activity.getViewer().setViewPagerController();

      showAlbum();

//      activity.saveSubData();
    } catch (Exception ex) {

      ex.printStackTrace();

      showCameraPermissionsDialog();
    }
  }

  private void saveCurrentPhotoUri() {
    photoAlbum.getData().add(currentPhotoUri.toString());
  }

  private void saveCurrentPhotoUri(Uri uri) {
    photoAlbum.getData().add(uri.toString());
  }

  public void onGalleryResult(Intent onGalleryResultIntent) {

    Timber.i("onGalleryResult: %s",onGalleryResultIntent.getData());

    try {

//      assignImageUri();

      Timber.i(currentPhotoUri.toString());

      Bitmap bitmap = null;

      try {

        // TODO: 20.11.17 support multiSelect
        List<Uri> uris = Matisse.obtainResult(onGalleryResultIntent);
        Uri data = uris.get(0);


        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data);
      } catch (IOException e) {
        e.printStackTrace();
      }

      OutputStream os = null;
      try {
        os = new BufferedOutputStream(new FileOutputStream(currentPhotoUri.getPath()));

        if (bitmap != null) {

          Timber.i("gallery image byteCount = " + bitmap.getByteCount());

          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

          saveCurrentPhotoUri();
          activity.getAlbumViewer().setViewPagerController();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {

        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      showAlbum();
    } catch (Exception ex) {

      ex.printStackTrace();
      showCameraPermissionsDialog();
    }
  }

  public void showAlbum() {
    photoAlbum.showAlbum();
    photoAlbum.notifyDataSetChanged();
  }

  private void showWarningExceedPhotoLimit() {

    new AlertDialog.Builder(activity).setMessage(
        String.format(Locale.getDefault(), activity.getString(R.string.err_img_limit),
            Cv.PHOTO_FACT_LIMIT)).setPositiveButton(R.string.btn_ok, null).show();
  }

  public void removePhoto(String link) {

    List<String> newPhotoLinks = new ArrayList<>();

    for (String oldLink : photoAlbum.getData()) {
      if (!oldLink.equals(link)) {
        newPhotoLinks.add(oldLink);
      }
    }
    photoAlbum.getData().clear();

    for (String l : newPhotoLinks) {
      photoAlbum.getData().add(l);
    }

    photoAlbum.setData(newPhotoLinks);

    new Thread(() -> {
      File file = new File(link);

      if (file.exists()) {
        file.delete();
      }
    }).start();

    Picasso.with(activity).invalidate(link);  // Fix show deleted image
    showAlbum();

    activity.onBackPressed();
  }

  public void showTakePhotoChooser() {
    photoChooserDialog = new AlertDialog.Builder(activity).setMessage(R.string.msg_camera_or_gallery)
        .setPositiveButton(R.string.btn_camera, (__, ___) -> takeNewPhoto(Cv.REQUEST_CAMERA_PHOTOALBUM))
        .setNeutralButton(R.string.btn_cancel, null)
        .setNegativeButton(R.string.btn_gallery, (__, ___) -> getPhotoFromGallery())
        .show();
  }


  public void showRemovePhotoDialog(String link) {
    new AlertDialog.Builder(activity).setMessage(R.string.mst_remove_img)
        .setPositiveButton(R.string.btn_yes, (__, ___) -> removePhoto(link))
        .setNegativeButton(R.string.btn_cancel, null)
        .show();
  }

  private File createImageFile() throws IOException {

    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

    File photoDir = createPhotoDir();

    Timber.i(photoDir.getAbsolutePath());

    File tempFile = File.createTempFile(timeStamp, ".jpg", photoDir);

    Timber.i(tempFile.getAbsolutePath());

    return tempFile;
  }

  public void assignImageUri() {
    try {
      currentPhotoUri = Uri.fromFile(createImageFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void showCameraPermissionsDialog() {

    new AlertDialog.Builder(activity).setMessage("Не поддерживается на данном устройстве."
        + "Проверьте разрешения для приложения: 1) Использовать камеру\n2) Чтение и запись SD card")
        .setPositiveButton(R.string.btn_ok, null)
        .show();
  }

  private File createPhotoDir() {
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tps");
    file.mkdirs();
    return file;
  }

  public void updateAlbum(List<ImageView> presenterAlbum) {

    Timber.i("updateAlbum: %s","YEAT NOT IMPLEMENTED");
    //photoAlbum. = presenterAlbum;
  }

  public void openImage() {
    PhotoAlbumViewer viewer =activity.getAlbumViewer();
    viewer.setPresenter(this);
    viewer.setData(photoAlbum.getData());
    viewer.setVisibility(View.VISIBLE);
  }

  public void openInImageViewer(View view) {

    PhotoAlbumViewer viewer = activity.getAlbumViewer();
    viewer.setPresenter(this);
    viewer.setData(photoAlbum.getData());

    int index = Integer.parseInt((String) view.getTag());

    if (index < photoAlbum.getData().size()) {

      viewer.getImageViewerPager().setCurrentItem(index);
      viewer.setVisibility(View.VISIBLE);

      activity.hideActionBar();

    } else {
      showTakePhotoChooser();
    }
  }

  public Bitmap bitmapResize(Bitmap imageBitmap) {

    Bitmap bitmap = imageBitmap;
    float lengthbmp = bitmap.getHeight();
    float widthbmp = bitmap.getWidth();

    // Get Screen width
    DisplayMetrics displaymetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    float hight = displaymetrics.heightPixels / 3;
    float width = displaymetrics.widthPixels / 3;

    int convertHighet = (int) hight, convertWidth = (int) width;

    // high length
    if (lengthbmp > hight) {
      convertHighet = (int) hight - 20;
      bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
              convertHighet, true);
    }

    // high width
    if (widthbmp > width) {
      convertWidth = (int) width - 20;
      bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
              convertHighet, true);
    }

    return bitmap;
  }


  public void onBackPressed() {
    activity.onBackPressed();
  }
}
