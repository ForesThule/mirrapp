package com.lesforest.apps.mirrapp.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lesforest.apps.mirrapp.R;
import com.lesforest.apps.mirrapp.model.Claim;
import com.lesforest.apps.mirrapp.ui.ClaimActivity;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<Claim> mDataset;
    private MainActivity context;
    private AlertDialog alertDialog;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        @BindView(R.id.mainFrame)
        LinearLayout frame;

        @BindView(R.id.tv_claim_name)
        TextView tvName;
        @BindView(R.id.tv_claim_descr)
        TextView tvDesc;
        @BindView(R.id.tv_claim_timestamp)
        TextView tvTimestamp;

        @BindView(R.id.tv_status)
        TextView tvStatus;
//
        @BindColor(R.color.main_adapter_finish)
        int finishColor;
        @BindColor(R.color.main_adapter_inwork)
        int inWorkColor;

        @BindView(R.id.image)
        ImageView imageView;

        public ViewHolder(View v) {

            super(v);
            ButterKnife.bind(this, itemView);
//            frame.setOnClickListener(this);
//            mTextView = v;
//            imageView = iv;

        }

        @Override
        public void onClick(View v) {

            Context context = v.getContext();
            Intent intent = new Intent(context,ClaimActivity.class);
            context.startActivity(intent);

            Toast.makeText(v.getContext(),"CLICK",Toast.LENGTH_SHORT);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainAdapter(MainActivity context, List<Claim> myDataset) {
        this.context = context;
        mDataset = myDataset;



    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
//        View v = LayoutInflater.from(parent.getContext())
//                               .inflate(R.layout.my_text_view, parent, false);
//        // set the view's size, margins, paddings and layout parameters
//        TextView tv = (TextView) parent.findViewById(R.id.tv_claim_name);
////        tv.setText("SSSSSSSSSSSSSSSssss");
////        CameraView cv = (CameraView) parent.findViewById(R.id.camera);
//
//
//        ImageView imageView = (ImageView) parent.findViewById(R.id.image);
//
//
//        ViewHolder vh = new ViewHolder(new TextView(v.getContext()),imageView);
//        return vh;


        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false));

    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        Claim claim = mDataset.get(position);

        holder.tvTimestamp.setText(claim.getTimestamp().toString());
        holder.tvDesc.setText(claim.getDescription());
        holder.tvName.setText(claim.getName());
        holder.tvName.setTextColor(R.color.black);


        initDialog(context,claim);

        ImageView imageView = holder.imageView;

//        imageView.setOnClickListener(view -> {
//            context.setCurrentClaim(mDataset.get(position));
//            context.showImage(view);
//        });

        Timber.i("CLAIM: %s",claim);

        if (claim.isFinish()){
            holder.tvStatus.setText("завершено");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.frame.setBackgroundColor(holder.inWorkColor);

        }else {
            holder.tvStatus.setText("в работе");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorInWork));

            holder.frame.setBackgroundColor(holder.finishColor);

        }

        if (null != claim.getImageLinks()&&claim.getImageLinks().size()>0){
            Glide.with(context).load(claim.getImageLinks().get(0)).into(imageView);
        }else {
            Glide.with(context).load("http://doorofperception.com/wp-content/uploads/doorofperception.com-moebius-color-8.jpg").into(imageView);
        }

        holder.frame.setOnClickListener(v ->{

            Context context = v.getContext();
            Intent intent = new Intent(context,ClaimActivity.class);
            intent.setAction(claim.getTimestamp());
            context.startActivity(intent);

        });

        holder.frame.setOnLongClickListener(v -> {

            showDialog(claim.getName());

            return false;
        });

    }

    private void showDialog(String s){
     alertDialog.setMessage(String.format("Заявку %s",s));
     alertDialog.show();
    }

    private void initDialog(MainActivity context, Claim claim) {
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(String.format("Удалить заявку"))
                .setPositiveButton("Да",(dialog, which) -> {
                    context.removeClaim(claim);
                })
                .create();
//
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size()  ;
    }

    public void setData(List<Claim> data){
        mDataset = data;
        Collections.sort(mDataset, (o1, o2) -> {
            int result=0;
            if (o1.isFinish() && o2.isFinish()){
                result = 0;
            }else if(!o1.isFinish() && !o2.isFinish()){
                result = 0;
            }else if(!o1.isFinish() && o2.isFinish()){
                result = -1;
            }else if(o1.isFinish() && !o2.isFinish()){
                result = 1;
            }
            return result;
        });
        notifyDataSetChanged();
    }
}


