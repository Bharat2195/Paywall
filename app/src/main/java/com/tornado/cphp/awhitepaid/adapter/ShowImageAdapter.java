package com.tornado.cphp.awhitepaid.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.vendorpanel.VendorProductShowImage;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by bharat on 21-Sep-16.
 */
public class ShowImageAdapter extends RecyclerView.Adapter<ShowImageAdapter.ViewHolder> {


    ArrayList<String> listEntryDate;
    ArrayList<String> listImageText;
    ArrayList<String> listImagePath;
    ArrayList<String> listId;
    Context context;
    private static final String TAG=ShowImageAdapter.class.getSimpleName();
    Bitmap bitImagePath;
    private int position;
    private int i;

    public ShowImageAdapter(Context context, ArrayList<String> listImageText, ArrayList<String> listEntryDate, ArrayList<String> listImagePath, ArrayList<String> listId) {
        super();
        this.context = context;
        this.listEntryDate = listEntryDate;
        this.listImageText = listImageText;
        this.listImagePath=listImagePath;
        this.listId=listId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.txtItemName.setText(listImageText.get(i));

//        Bitmap decodeImage=decodeFromBase64ToBitmap(listImagePath.get(i));
//        Log.d(TAG, "decode image: "+decodeImage);
//        viewHolder.imgThumbnail.setImageBitmap(listImagePath.get(i));
//        bitImagePath=listImagePath.get(i);
//        Picasso.with(context).load(listImagePath.get(i)).into(viewHolder.imgThumbnail);

//        Glide.with(context).load(listImagePath.get(i)).placeholder(R.drawable.img_loding).asGif().into(viewHolder.imgThumbnail);
        Picasso.with(context).load(listImagePath.get(i)).placeholder(R.drawable.ic_default).into(viewHolder.imgThumbnail);
        viewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    Intent intent = new Intent(context, VendorProductShowImage.class);
                    intent.putExtra("strImagePath",listImagePath.get(i));
                    intent.putExtra("strId", listId.get(i));
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
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




