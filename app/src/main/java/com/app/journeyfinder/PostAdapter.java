package com.app.journeyfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final List<PostItem> postList;

    public PostAdapter(Context context, List<PostItem> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    //view the xml
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem post = postList.get(position);

        holder.username.setText(post.getUsername());
        holder.timePosted.setText(post.getTimePosted());
        holder.caption.setText(post.getCaption());

        //profile image
        Glide.with(context)
                .load(post.getUserProfileUrl())
                .placeholder(R.drawable.journeyfinder_logo)
                .circleCrop()
                .into(holder.userProfileImage);

        //Load post
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.journeyfinder_logo)
                    .centerCrop()
                    .into(holder.image);

            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, image;
        TextView username, timePosted, caption;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            timePosted = itemView.findViewById(R.id.timePosted);
            caption = itemView.findViewById(R.id.postCaption);
        }
    }
}
