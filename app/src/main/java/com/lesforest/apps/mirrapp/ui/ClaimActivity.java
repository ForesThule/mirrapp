package com.lesforest.apps.mirrapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.desmond.squarecamera.CameraActivity;
import com.desmond.squarecamera.SquareImageView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.lesforest.apps.mirrapp.ClaimDAO;
import com.lesforest.apps.mirrapp.R;
import com.lesforest.apps.mirrapp.ThisApp;
import com.lesforest.apps.mirrapp.components.AlbumPresenter;
import com.lesforest.apps.mirrapp.components.GlideApp;
import com.lesforest.apps.mirrapp.components.PhotoAlbumViewer;
import com.lesforest.apps.mirrapp.model.Claim;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClaimActivity extends AppCompatActivity {

    private Claim currentClaim;
    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.viewer)
    PhotoAlbumViewer photoAlbumViewer;
    private AlbumPresenter presenter;

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.et_claim_name)
    EditText etName;

    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.et_real_price)
    EditText etRealPrice;

    @BindView(R.id.et_profit)
    TextView tvProfit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_claim);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String action = intent.getAction();

        System.out.println(action);


        getclaim(action);

        prepareViews();
        showClaim();
    }

    private void getclaim(String action) {
        ClaimDAO claimDAO = ThisApp.get(this).getDb().claimDAO();

        for (Claim claim : claimDAO.getAll()) {
            if (claim.getTimestamp().equals(action)) {
                currentClaim = claim;
            }
        }
    }

    private void prepareViews() {

        etName.setText(currentClaim.getName());
        etPrice.setText(String.valueOf(currentClaim.getPrice()));
        etRealPrice.setText(String.valueOf(currentClaim.getReal_price()));
        tvProfit.setText(String.valueOf(currentClaim.getProfit()));

        RxTextView.textChanges(etName)
                .skipInitialValue()
                .filter(cS -> cS.length() > 2)
                .map(CharSequence::toString)
                .subscribe(currentClaim::setName);

        RxTextView.textChanges(etPrice)
                .skipInitialValue()
                .filter(charSequence -> charSequence.length() > 0)
                .map(CharSequence::toString)
                .map(Double::parseDouble)
                .subscribe(price -> {
                    currentClaim.setPrice(price);
                    calculateProfit();
                }, throwable -> {
                    throwable.printStackTrace();
//                    currentClaim.setPrice(0);
                });

        RxTextView.textChanges(etRealPrice)
                .skipInitialValue()
                .filter(charSequence -> charSequence.length() > 0)
                .map(CharSequence::toString)
                .subscribe(real_price -> {

                    double v = 0;
                    try {
                         v = Double.parseDouble(real_price);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    currentClaim.setReal_price(v);
                    calculateProfit();
                }, throwable -> {
                    throwable.printStackTrace();
//                    currentClaim.setReal_price(0);
                });
    }

    private void calculateProfit() {

        double price = currentClaim.getPrice();
        double real_price = currentClaim.getReal_price();


        tvProfit.setText(String.valueOf(price - real_price));

    }

    private void onImageClick(View view) {


        if (null != currentClaim.getImageLink()) {
            openImageViewer();
        } else {
            requestForCameraPermission(view);
        }

    }

    private void initImageView() {

        imageView.setOnClickListener(this::onImageClick);

        String imageLink = currentClaim.getImageLink();

        if (imageLink != null) {

            GlideApp.with(this)
                    .load(imageLink)
                    .placeholder(R.drawable.ic_arrow_back_black_24dp)
                    .fitCenter()
                    .into(imageView);



        }
    }

    private void showPhoto() {

        Glide.with(this)
                .load(currentClaim.getImageLink())
                .into(imageView);
    }

    private void takePhoto() {

        Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();

            savePhotoLink(photoUri);
            showPhoto();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePhotoLink(Uri photoUri) {

        String path = photoUri.getPath();
        currentClaim.setImageLink(path);
    }


    private static final int REQUEST_CAMERA = 0;

    // Check for camera permission in MashMallow
    public void requestForCameraPermission(View view) {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Show permission rationale
            } else {
                // Handle the result in Activity#onRequestPermissionResult(int, String[], int[])
//                ActivityCompat.requestPermissions(ClaimActivity.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            // Start CameraActivity
            takePhoto();
        }
    }

    private void openImageViewer() {

    }

    private void showClaim() {

        tvDate.setText(currentClaim.getTimestamp());

        initImageView();

    }

    @Override
    public void onBackPressed() {
        saveClaim();
        startMainActivity();
    }

    private void saveClaim() {
        ClaimDAO claimDAO = ThisApp.get(this).getDb().claimDAO();
        claimDAO.insertAll(currentClaim);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public PhotoAlbumViewer getAlbumViewer() {
        return photoAlbumViewer;
    }

    public void showImage(View v) {
        getAlbumViewer().setVisibility(View.VISIBLE);

    }


    public void hideActionBar() {

    }

    public void setAlbumPresenter(AlbumPresenter albumPresenter) {
        presenter = albumPresenter;
    }
}
