package com.lesforest.apps.mirrapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.desmond.squarecamera.CameraActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.lesforest.apps.mirrapp.ClaimDAO;
import com.lesforest.apps.mirrapp.R;
import com.lesforest.apps.mirrapp.ThisApp;
import com.lesforest.apps.mirrapp.components.AlbumPresenter;
import com.lesforest.apps.mirrapp.components.PhotoAlbum;
import com.lesforest.apps.mirrapp.components.PhotoAlbumViewer;
import com.lesforest.apps.mirrapp.model.Claim;
import com.lesforest.apps.mirrapp.ui.main.MainActivity;
import com.zhihu.matisse.Matisse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lesforest.apps.mirrapp.Cv.REQUEST_CAMERA_PHOTOALBUM;
import static com.lesforest.apps.mirrapp.Cv.REQUEST_GALLERY;

public class ClaimActivity extends AppCompatActivity {

    private Claim currentClaim;

    @BindView(R.id.imageView)
    ImageView imageView;


    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.et_claim_name)
    EditText etName;


    @BindView(R.id.et_price)
    EditText etPrice;

    @BindView(R.id.et_prepay)
    EditText etPrepay;

    @BindView(R.id.et_remain)
    EditText etRemain;

    @BindView(R.id.button_finish)
    Button buttonFinish;


    @BindView(R.id.et_advance)
    EditText etAdvance;

    @BindView(R.id.et_surcharge)
    EditText etSurcharge;

    @BindView(R.id.et_cash)
    EditText tvCash;

    @BindView(R.id.photo_album)
    PhotoAlbum photoAlbum;

    @BindView(R.id.viewer)
    PhotoAlbumViewer viewer;

    private AlbumPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_claim);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String action = intent.getAction();

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

    @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
    private void prepareViews() {

        etName.setText(currentClaim.getName());
        etPrice.setText(String.valueOf(currentClaim.getPrice()));
        etPrepay.setText(String.valueOf(currentClaim.getPrepay()));
        tvCash.setText(String.valueOf(currentClaim.getCash()));
        etAdvance.setText(String.valueOf(currentClaim.getAdvance()));
        etSurcharge.setText(String.valueOf(currentClaim.getSurcharge()));

        calculateRemain();
        calculateProfit();




        etName.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (etName.getRight() - etName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    etName.setText("");
                    return true;
                }
            }
            return false;
        });

        RxTextView.textChanges(etName)
                .skipInitialValue()
                .filter(cS -> cS.length() > 2)
                .map(CharSequence::toString)
                .subscribe(currentClaim::setName);

        //цена
        RxTextView.textChanges(etPrice)
                .skipInitialValue()
                .filter(charSequence -> charSequence.length() > 0)
                .map(CharSequence::toString)
                .map(Double::parseDouble)
                .subscribe(price -> {
                    currentClaim.setPrice(price);
                    calculateProfit();
                    calculateRemain();
                }, throwable -> {
                    throwable.printStackTrace();
//                    currentClaim.setPrice(0);
                });

        //предоплата
        RxTextView.textChanges(etPrepay)
                .skipInitialValue()
                .filter(charSequence -> charSequence.length() > 0)
                .map(CharSequence::toString)
                .map(Double::parseDouble)
                .subscribe(prepay -> {
                    currentClaim.setPrepay(prepay);
                    calculateRemain();

                }, throwable -> {
                    throwable.printStackTrace();
                    currentClaim.setSurcharge(0);
                });

        //advance
        RxTextView.textChanges(etAdvance)
                .skipInitialValue()
                .filter(charSequence -> charSequence.length() > 0)
                .map(CharSequence::toString)
                .map(Double::parseDouble)
                .subscribe(avance -> {
                    currentClaim.setAdvance(avance);
                    calculateProfit();

                }, throwable -> {
                    throwable.printStackTrace();
                    currentClaim.setAdvance(0);
                });


        RxTextView.textChanges(etSurcharge)
                .skipInitialValue()
                .filter(charSequence -> charSequence.length() > 0)
                .map(CharSequence::toString)
                .map(Double::parseDouble)
                .subscribe(s -> {
                    currentClaim.setSurcharge(s);
                    calculateProfit();

                }, throwable -> {
                    throwable.printStackTrace();
                    currentClaim.setAdvance(0);
                });




        RxView.clicks(buttonFinish)
                .subscribe(o -> {
                    currentClaim.finishClaim(o);
                    onBackPressed();
                });


        if (currentClaim.isFinish()){
            getSupportActionBar().setTitle("Завершена");
        } else {
            getSupportActionBar().setTitle("В работе");
        }


        presenter = new AlbumPresenter(this,photoAlbum);

        photoAlbum.setAlbumPresenter(presenter);
        photoAlbum.setData(currentClaim.getImageLinks());
        photoAlbum.showAlbum();

    }

    private void calculateRemain() {

        double prepay = currentClaim.getPrepay();
        double price = currentClaim.getPrice();
        double remain = price - prepay;
        currentClaim.setRemain(remain);
        etRemain.setText(String.valueOf(remain));
    }

    private void calculateProfit() {

        double advance = currentClaim.getAdvance();
        double surcharge = currentClaim.getSurcharge();
        double price = currentClaim.getPrice();

        double v = advance + surcharge;

        double cash = price - v;

        tvCash.setText(String.valueOf(cash));
        currentClaim.setCash(cash);

    }

//    private void onImageClick(View view) {
//
//
//        if (null != currentClaim.getImageLink()) {
//            openImageViewer();
//        } else {
//            requestForCameraPermission(view);
//        }
//
//    }

//    private void initImageView() {
//
//        imageView.setOnClickListener(this::onImageClick);
//
//        String imageLink = currentClaim.getImageLink();
//
//        if (imageLink != null) {
//
//            GlideApp.with(this)
//                    .load(imageLink)
//                    .placeholder(R.drawable.ic_arrow_back_black_24dp)
//                    .fitCenter()
//                    .into(imageView);
//
//
//
//        }
//    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_claim, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA_PHOTOALBUM) {

            System.out.println(data);

            Uri photoUri = data.getData();

            savePhotoLink(photoUri);

            photoAlbum.setData(currentClaim.getImageLinks());
            photoAlbum.showAlbum();
        }

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            List<Uri> uris = Matisse.obtainResult(data);

            savePhotoLink(uris);
            photoAlbum.showAlbum();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePhotoLink(List<Uri> uris) {
        for (Uri uri : uris) {

            currentClaim.addImagelink(uri.toString());
        }

    }

    private void savePhotoLink(Uri photoUri) {

//        String path = photoUri.getPath();
//        currentClaim.addImagelink(path);
        currentClaim.addImagelink(photoUri.toString());
    }


    private static final int REQUEST_CAMERA = 0;

    // Check for camera permission in MashMallow
    public void requestForCameraPermission(View view) {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            } else {
//                ActivityCompat.requestPermissions(ClaimActivity.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            takePhoto();
        }
    }

    private void openInImageViewer(View v) {
        presenter.openInImageViewer(v);

    }

    private void showClaim() {

        tvDate.setText(currentClaim.getTimestamp());

//        initImageView();

    }

    @Override
    public void onBackPressed() {

        if (viewer.getVisibility()==View.VISIBLE){
            viewer.setVisibility(View.GONE);
        } else {
            saveClaim();
            startMainActivity();
        }
    }

    private void saveClaim() {
        ClaimDAO claimDAO = ThisApp.get(this).getDb().claimDAO();
        claimDAO.insertAll(currentClaim);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public PhotoAlbumViewer getAlbumViewer() {
        return viewer;
    }

    public void showImage(View v) {
        getAlbumViewer().setVisibility(View.VISIBLE);

    }


    public void hideActionBar() {

    }

    public void setAlbumPresenter(AlbumPresenter albumPresenter) {
        presenter = albumPresenter;
    }

    public List<String> getImageLinks() {
        return currentClaim.getImageLinks();
    }
}
