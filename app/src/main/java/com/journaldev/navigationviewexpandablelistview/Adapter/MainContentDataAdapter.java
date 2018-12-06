package com.journaldev.navigationviewexpandablelistview.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.journaldev.navigationviewexpandablelistview.EditorActivity;
import com.journaldev.navigationviewexpandablelistview.MainActivity;
import com.journaldev.navigationviewexpandablelistview.Model.EditorModel;
import com.journaldev.navigationviewexpandablelistview.Model.MainContent;
import com.journaldev.navigationviewexpandablelistview.R;
import com.journaldev.navigationviewexpandablelistview.Show;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

public class MainContentDataAdapter extends RecyclerView.Adapter<MainContentDataAdapter.MainContentViewHolder> {

    private LayoutInflater inflater;
    public List<MainContent> contents;
    private int previousPosition = 0;
    private Context context;
    private RecyclerView recyclerView;

    int check = 0;

    public class MainContentViewHolder extends RecyclerView.ViewHolder {
        private TextView title, summary, tag;
        private ImageView weather, image;
        private ImageButton bookmark;
        private CardView contentMain = null;
        private String date;

        public MainContentViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.row_title);
            summary = (TextView) view.findViewById(R.id.row_content);
            tag = (TextView) view.findViewById(R.id.row_tag);
            weather = (ImageView) view.findViewById(R.id.row_weather);
            image = (ImageView) view.findViewById(R.id.row_image);
            bookmark = (ImageButton) view.findViewById(R.id.row_bookmark);
            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bookmark.getDrawable() == MainActivity.offBookMark){
                        bookmark.setImageDrawable(MainActivity.onBookMark);
                        MainActivity.dbHelper.update_bookMark(date, 1);
                        MainActivity.getDBList();
                    } else {
                        bookmark.setImageDrawable(MainActivity.offBookMark);
                        MainActivity.dbHelper.update_bookMark(date, 0);
                        MainActivity.getDBList();
                    }
                }
            });
            contentMain = (CardView) itemView.findViewById(R.id.content_main);
            contentMain.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    View view = null;

                    if (getAdapterPosition() == previousPosition) {
                        if(check == 0) {
                            //웹뷰가 선택된 곳의 행동처리 (이전과 같은 곳을 클릭할 경우)
                            String date = contents.get(recyclerView.findViewHolderForAdapterPosition(previousPosition).getAdapterPosition()).getDate();
                            String title = contents.get(recyclerView.findViewHolderForAdapterPosition(previousPosition).getAdapterPosition()).getTitle();
                            String weather = contents.get(recyclerView.findViewHolderForAdapterPosition(previousPosition).getAdapterPosition()).getWeather();
                            Intent intent = new Intent(context, Show.class);
                            intent.putExtra("date", date);
                            intent.putExtra("title", title);
                            intent.putExtra("weather",weather);
                            context.startActivity(intent);
                            previousPosition = getAdapterPosition();
                            check++;
                        } else {
                            check = 0;
                        }
                    } else {
                        previousPosition = getAdapterPosition();
                    }
                    return false;
                }
            });
        }
    }

    public MainContentDataAdapter(Context context, List<MainContent> contents, RecyclerView recyclerView) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.contents = contents;
        this.recyclerView = recyclerView;
    }

    public void removeItem(int position) {
        contents.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, contents.size());
    }
    public void restoreItem(MainContent mainContent, int position) {
        contents.add(position, mainContent);
        // notify item added by position
        notifyItemInserted(position);
    }

    @Override
    public MainContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_content_main, parent, false);

        return new MainContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainContentViewHolder holder, int position) {
        MainContent content = contents.get(position);
        holder.title.setText(content.getTitle());
        holder.summary.setText(content.getSummary());
        holder.tag.setText(content.getTag());
        holder.weather.setImageURI(Uri.parse(content.getWeather()));
        holder.image.setImageURI(Uri.parse(content.getImage()));
        holder.date = content.getDate();
        if(content.getBookMark() == 0){
            holder.bookmark.setImageDrawable(MainActivity.offBookMark);
        } else {
            holder.bookmark.setImageDrawable(MainActivity.onBookMark);
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

}
