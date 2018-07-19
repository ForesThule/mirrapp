package com.lesforest.apps.mirrapp.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lesforest.apps.mirrapp.ClaimDAO;
import com.lesforest.apps.mirrapp.R;
import com.lesforest.apps.mirrapp.ThisApp;
import com.lesforest.apps.mirrapp.components.AlbumPresenter;
import com.lesforest.apps.mirrapp.model.Claim;
import com.lesforest.apps.mirrapp.ui.ClaimActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    RecyclerView recyclerView;
    List<Claim> dataSet = new ArrayList<>();
    private MainAdapter mainAdapter;
    private LinearLayoutManager mLayoutManager;

    SharedElementCallback sharedElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
        }
    };

    private ThisApp app;
    private Claim currentClaim;
    private AlbumPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("MainActivity.onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

            createClaim();
        });


        app = ThisApp.get(this);


        initRecycler();

    }

    private void getData() {

        ClaimDAO claimDAO = app.getDb().claimDAO();
        List<Claim> all = claimDAO.getAll();
        if (all != null) {
            dataSet = all;
        }


        mainAdapter.setData(dataSet);


//        recyclerView.smoothScrollToPosition(dataSet.size()-1);
    }

    private void initRecycler() {

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        mainAdapter = new MainAdapter(this, dataSet);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, 1, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,1));
        recyclerView.setAdapter(mainAdapter);
    }


    public void createClaim() {


        Date currentDate = new Date();

        String format = "dd.MM.yyyy hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String datetime = sdf.format(new Date(System.currentTimeMillis()));
        Claim claim = Claim.createClaim("", datetime);

        saveClaim(claim);

        Intent intent = new Intent(this, ClaimActivity.class);
        intent.setAction(datetime);
        startActivity(intent);


    }

    private void saveClaim(Claim claim) {

        app.getDb().claimDAO().insertAll(claim);
    }

    @Override
    protected void onResume() {
        System.out.println("MainActivity.onResume");
        super.onResume();

        getData();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
//            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            Toast.makeText(this, "Camera permission", Toast.LENGTH_SHORT);

//            ConfirmationDialogFragment
//                    .newInstance(R.string.camera_permission_confirmation,
//                            new String[]{Manifest.permission.CAMERA,
//                            REQUEST_CAMERA_PERMISSION})
//                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void saveSubData() {

    }

    public void hideActionBar() {
        getActionBar().hide();
    }

    public void setCurrentClaim(Claim claim) {
        currentClaim = claim;
    }


    public void removeClaim(Claim claim) {
        ClaimDAO claimDAO = app.getDb().claimDAO();
        claimDAO.delete(claim);
        getData();
    }
}
