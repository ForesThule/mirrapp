package com.lesforest.apps.mirrapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.cameraview.CameraView;
import com.lesforest.apps.mirrapp.R;
import com.lesforest.apps.mirrapp.model.Claim;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Claim> mDataset;
    private MainActivity context;

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
    public MyAdapter(MainActivity context, List<Claim> myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        Claim claim = mDataset.get(position);

        holder.tvTimestamp.setText(claim.getTimestamp().toString());
        holder.tvDesc.setText(claim.getDescription());
        holder.tvName.setText(claim.getName());

        ImageView imageView = holder.imageView;

//        imageView.setOnClickListener(view -> {
//            context.setCurrentClaim(mDataset.get(position));
//            context.showImage(view);
//        });

        if (null != claim.getImageLink()){
            Glide.with(context).load(claim.getImageLink()).into(imageView);
        }else {
            Glide.with(context).load("http://doorofperception.com/wp-content/uploads/doorofperception.com-moebius-color-8.jpg").into(imageView);
        }

        holder.frame.setOnClickListener(v ->{


            Context context = v.getContext();
            Intent intent = new Intent(context,ClaimActivity.class);
            intent.setAction(claim.getTimestamp());
            context.startActivity(intent);


        });


//        CameraView cameraView = holder.cameraView;
//        cameraView.start();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size()  ;
    }

    public void setData(List<Claim> data){
        mDataset = data;
        notifyDataSetChanged();
    }
}


