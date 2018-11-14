package com.journaldev.navigationviewexpandablelistview.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.journaldev.navigationviewexpandablelistview.MainActivity;
import com.journaldev.navigationviewexpandablelistview.Model.MainContent;
import com.journaldev.navigationviewexpandablelistview.R;

import java.util.List;

public class MainContentDataAdapter extends RecyclerView.Adapter<MainContentDataAdapter.MainContentViewHolder> {

    private LayoutInflater inflater;
    public List<MainContent> contents;

    public class MainContentViewHolder extends RecyclerView.ViewHolder {
        private TextView title, content, tag;
        private ImageView weather, image, bookmark;

        public MainContentViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.row_title);
            content = (TextView) view.findViewById(R.id.row_content);
            tag = (TextView) view.findViewById(R.id.row_tag);
            weather = (ImageView) view.findViewById(R.id.row_weather);
            image = (ImageView) view.findViewById(R.id.row_image);
            bookmark = (ImageView) view.findViewById(R.id.row_bookmark);
        }
    }

    public MainContentDataAdapter(Context context, List<MainContent> contents) {
        inflater = LayoutInflater.from(context);
        this.contents = contents;
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
        holder.content.setText(content.getContent());
        holder.tag.setText(content.getTag());
        holder.weather.setImageURI(Uri.parse(content.getWeather()));
        holder.image.setImageURI(Uri.parse(content.getImage()));
        if(content.getBookMark() == 0){
            //holder.bookmark.setImageDrawable(MainActivity.offBookMark);
        } else {
            holder.bookmark.setImageDrawable(MainActivity.onBookMark);
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

}
