package com.tornado.cphp.paywall.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tornado.cphp.paywall.R;

import java.util.ArrayList;

/**
 * Created by cphp on 21-Sep-16.
 */
public class ShowImageAdapter extends RecyclerView.Adapter<ShowImageAdapter.ViewHolder> {


    ArrayList<String> listEntryDate;
    ArrayList<String> listImageText;
    ArrayList<Bitmap> listImagePath;
    Context context;
    private static final String TAG=ShowImageAdapter.class.getSimpleName();


    public ShowImageAdapter(Context context, ArrayList<String> listEntryDate, ArrayList<String> listImageText, ArrayList<Bitmap> listImagePath) {
        super();
        this.context = context;
        this.listEntryDate = listEntryDate;
        this.listImageText = listImageText;
        this.listImagePath=listImagePath;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.txtItemName.setText(listImageText.get(i));

//        Bitmap decodeImage=decodeFromBase64ToBitmap(listImagePath.get(i));
//        Log.d(TAG, "decode image: "+decodeImage);
        viewHolder.imgThumbnail.setImageBitmap(listImagePath.get(i));

//        Glide.with(context).load(listImagePath.get(i)).into(viewHolder.imgThumbnail);

//        viewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, DashBoardProductDescriptionActivity.class);
//                intent.putExtra("strImagePath", strImagePath);
//                intent.putExtra("strPid",strPid);
//                intent.putExtra("strItemName",strItemName);
//                intent.putExtra("strPrice",strPrice);
//                intent.putExtra("strMpn",strMpn);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return listEntryDate.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView txtItemName;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            txtItemName = (TextView) itemView.findViewById(R.id.txtItemName);
        }



    }

    private Bitmap decodeFromBase64ToBitmap(String encodedImage)

    {

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;

    }




}




