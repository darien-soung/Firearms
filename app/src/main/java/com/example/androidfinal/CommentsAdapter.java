package com.example.androidfinal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private Context mContext;
    private List<Comments> mCommentList;

    public CommentsAdapter(Context context, List<Comments> comments)
    {
        mContext = context;
        mCommentList = comments;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.comments_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Comments curComments = mCommentList.get(i);
        viewHolder.mComment.setText(curComments.getComment());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public void clear() {
        final int size = mCommentList.size();
        mCommentList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mComment;

        public ViewHolder(View itemView)
        {
            super(itemView);

            mComment = itemView.findViewById(R.id.tv_comment);
        }
    }

}
