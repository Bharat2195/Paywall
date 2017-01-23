//package com.tornado.cphp.paywall.memberpanel;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.ericliu.asyncexpandablelist.CollectionView;
//import com.ericliu.asyncexpandablelist.CollectionViewCallbacks;
//import com.tornado.cphp.paywall.R;
//import com.tornado.cphp.paywall.config.AsyncActivity;
//import com.tornado.cphp.paywall.config.News;
//
//public class MemberVendorShowInformationActivity extends AppCompatActivity  implements CollectionViewCallbacks<String,News> {
//
//
//    public static final String TAG = MemberVendorShowInformationActivity.class.getSimpleName();
//    private CollectionView<String, News> mCollectionView;
//    private CollectionView.Inventory<String, News> inventory;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_member_vendor_show_information);
//
//        mCollectionView = (CollectionView) findViewById(R.id.collectionView);
//        mCollectionView.setCollectionCallbacks(this);
//
//        inventory = CollectionView.Inventory.newInstance();
//
//        // groupOrdinal dictates the sequence of groups to be displayed in the list,
//        // the groups will be displayed in an ascending order on groupOrdinal
//        int groupOrdinal = 0;
//        CollectionView.InventoryGroup<String, News> group1 = inventory.newGroup(groupOrdinal);
//        News news;
//
//        group1.setHeaderItem(getString(R.string.news_header_top_stories));
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title1));
//        news.setNewsBody(getString(R.string.news_body1));
//        group1.addItem(news);
//
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title2));
//        news.setNewsBody(getString(R.string.news_body2));
//        group1.addItem(news);
//
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title_3));
//        news.setNewsBody(getString(R.string.news_body3));
//        group1.addItem(news);
//
//        CollectionView.InventoryGroup<String, News> group2 = inventory.newGroup(2);
//        group2.setHeaderItem(getString(R.string.news_header_world));
//
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title4));
//        news.setNewsBody(getString(R.string.news_body4));
//        group2.addItem(news);
//
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title5));
//        news.setNewsBody(getString(R.string.news_body5));
//        group2.addItem(news);
//
//
//
//        CollectionView.InventoryGroup<String, News> group3 = inventory.newGroup(3); // 2 is smaller than 10, displayed second
//        group3.setHeaderItem(getString(R.string.news_header_australia));
//
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title6));
//        news.setNewsBody(getString(R.string.news_body6));
//        group3.addItem(news);
//
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title7));
//        news.setNewsBody(getString(R.string.news_body7));
//        group3.addItem(news);
//
//
//        news = new News();
//        news.setNewsTitle(getString(R.string.news_title8));
//        news.setNewsBody(getString(R.string.news_body8));
//        group3.addItem(news);
//
//
//        mCollectionView.updateInventory(inventory);
//    }
//
//    @Override
//    public RecyclerView.ViewHolder newCollectionHeaderView(Context context, int groupOrdinal, ViewGroup parent) {
//        View v = LayoutInflater.from(context)
//                .inflate(R.layout.header_row_item, parent, false);
//        return new TitleHolder(v);
//    }
//
//    @Override
//    public RecyclerView.ViewHolder newCollectionItemView(Context context, int groupOrdinal, ViewGroup parent) {
//        View v = LayoutInflater.from(context)
//                .inflate(R.layout.text_row_item, parent, false);
//
//        return new NewsItemHolder(v);
//    }
//
//    @Override
//    public void bindCollectionHeaderView(Context context, RecyclerView.ViewHolder holder, int groupOrdinal, String headerItem) {
//        ((TitleHolder) holder).getTextView().setText((String) headerItem);
//
//    }
//
//    @Override
//    public void bindCollectionItemView(Context context, RecyclerView.ViewHolder holder, int groupOrdinal, News item) {
//        NewsItemHolder newsItemHolder = (NewsItemHolder) holder;
//        newsItemHolder.getTextViewTitle().setText(item.getNewsTitle());
//        newsItemHolder.getTextViewDescrption().setText(item.getNewsBody());
//
//    }
//
//    public static class TitleHolder extends RecyclerView.ViewHolder {
//
//        private final TextView textView;
//
//        public TitleHolder(View itemView) {
//            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.title);
//        }
//
//        public TextView getTextView() {
//            return textView;
//        }
//    }
//
//    public static class NewsItemHolder extends RecyclerView.ViewHolder {
//
//
//        private final TextView tvTitle;
//        private final TextView tvDescription;
//
//        public NewsItemHolder(View v) {
//            super(v);
//            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getPosition() + " clicked.");
//                }
//            });
//            tvTitle = (TextView) v.findViewById(R.id.title);
//            tvDescription = (TextView) v.findViewById(R.id.description);
//        }
//
//        public TextView getTextViewTitle() {
//            return tvTitle;
//        }
//
//        public TextView getTextViewDescrption() {
//            return tvDescription;
//        }
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.launch) {
//            Intent intent = new Intent(this, AsyncActivity.class);
//            startActivity(intent);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//}
