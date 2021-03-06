package com.lesforest.apps.mirrapp.components;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lesforest.apps.mirrapp.R;
import com.lesforest.apps.mirrapp.ui.ClaimActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by root on 20.11.17.
 */

public class PhotoAlbum extends LinearLayout {

    ClaimActivity activity;
    View rootView;
    @BindViews({R.id.ph_0, R.id.ph_1, R.id.ph_2, R.id.ph_3})
    List<ImageView> album;


    private List<String> photoLinksData = new ArrayList<>();

    private AlbumPresenter albumPresenter;

    //private PhotoAlbumViewer albumViewer;

    public PhotoAlbum(ClaimActivity activity, PhotoAlbumViewer viewer) {
        super(activity);
        this.activity = activity;
        //albumViewer = viewer;
        init(activity);
    }

    public void setData(List<String> photoLinksData) {
        this.photoLinksData = photoLinksData;
    }

    public List<String> getData() {
        return photoLinksData;
    }


    public PhotoAlbum(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        activity = (ClaimActivity) context;
        init(context);
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.photo_album, this);
        ButterKnife.bind(this);

        //albumViewer = new PhotoAlbumViewer(context);


        for (ImageView imageView : album) {
            imageView.setOnClickListener(view -> {
                //albumPresenter.setCurrentData(photoLinksData);
                activity.setAlbumPresenter(albumPresenter);

                albumPresenter.openInImageViewer(view);
            });
        }
    }


    public void showAlbum() {

        List<String> photoLinks = activity.getImageLinks();


//    for (ImageView iv : album) {
//      iv.setImageResource(R.drawable.ic_crop_original_black_48dp);
//    }

        for (int i = 0; i < photoLinks.size(); i++) {

            if (i < album.size()) {
                ImageView target = album.get(i);

                String uriString = photoLinks.get(i);
                Uri link = Uri.parse(uriString);

                System.out.println(uriString);
                System.out.println(link);

                Picasso.with(activity)
                        .load(uriString)
                        .fit()
                        .centerCrop()
//                        .centerInside()
                        .placeholder(R.drawable.ic_crop_original_black_48dp)
                        .into(target);


//        GlideApp.with(this)
//                .load(link)
//                .placeholder(R.drawable.ic_arrow_back_black_24dp)
//                .fitCenter()
//                .into(target);

                target.invalidate();
            } else {
                Timber.i("Album index out of bound: album size = %d, index = %d", album.size(), i);
            }
        }
    }

    //public PhotoAlbumViewer getAlbumViewer() {
    //  return albumViewer;
    //}

    public void notifyDataSetChanged() {
        showAlbum();
    }

    public void setAlbumPresenter(AlbumPresenter albumPresenter) {
        this.albumPresenter = albumPresenter;
    }
}
