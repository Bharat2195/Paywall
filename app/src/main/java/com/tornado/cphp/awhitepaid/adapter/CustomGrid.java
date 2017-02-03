package com.tornado.cphp.awhitepaid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tornado.cphp.awhitepaid.R;
import com.tornado.cphp.awhitepaid.memberpanel.MemberShowVendorAddressonMapActivity;
import com.tornado.cphp.awhitepaid.memberpanel.MemberShowVendorProductImageActivity;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by cphp on 21-Sep-16.
 */
public class CustomGrid extends BaseAdapter {


    //    private final int[] imageId;
    private static final String TAG = CustomGrid.class.getSimpleName();
    private Context mContext;
    private ArrayList<String> listImage;
    private ArrayList<String> listImageName;
    private ArrayList<String> listVendorId;


    public CustomGrid(Context mContext, ArrayList<String> listImage, ArrayList<String> listImageName, ArrayList<String> listVendorId) {
        this.mContext = mContext;
        this.listImage = listImage;
        this.listImageName = listImageName;
        this.listVendorId = listVendorId;
    }


    @Override
    public int getCount() {
        return listImage.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid;


        if (convertView == null) {

            grid = new View(mContext);
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = layoutInflater.inflate(R.layout.grid_single, null);

        } else {
            grid = (View) convertView;
        }

        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
        textView.setText(listImageName.get(position));
//            imageView.setImageResource(imageId[position]);
        Picasso.with(mContext).load(listImage.get(position)).placeholder(R.drawable.ic_default).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MemberShowVendorProductImageActivity.class);
                intent.putExtra("strImagePath", listImage.get(position));
                intent.putExtra("strId", listVendorId.get(position));
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
//                Intent intent = new Intent(mContext, MemberShowVendorAddressonMapActivity.class);
//                intent.putExtra("strVendorId", listVendorId.get(position));
//                mContext.startActivity(intent);
            }
        });

        return grid;
    }
}
