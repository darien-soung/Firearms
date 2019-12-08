package com.example.androidfinal;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.androidfinal.Guns;
import com.example.androidfinal.ImageAdapter;
import com.example.androidfinal.R;
import com.example.androidfinal.SettingsSingleton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Guns> mGuns;
    private SettingsSingleton singleton;
    private OnItemClickListener mListener;

    public ListAdapter(Context context, List<Guns> guns)
    {
        mContext = context;
        mGuns = guns;
        singleton = SettingsSingleton.getInstance();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.lister_item, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        Guns curGuns = mGuns.get(i);
        imageViewHolder.mName.setText(curGuns.getName());

        if(singleton.getCurrency() == 1)
        {
            imageViewHolder.mPrice.setText("RM " + (curGuns.getPrice() * 4));
        }
        else
        {
            imageViewHolder.mPrice.setText("$ " + curGuns.getPrice());
        }

        Picasso.with(mContext)
                .load(curGuns.getImageUrl())
                .fit()
                .centerInside()
                .into(imageViewHolder.mImage);
    }

    @Override
    public int getItemCount() {
        return mGuns.size();
    }

    public void clear() {
        final int size = mGuns.size();
        mGuns.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView mName, mPrice;
        public ImageView mImage, mDelete;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.tv_name);
            mPrice = itemView.findViewById(R.id.tv_price);
            mImage = itemView.findViewById(R.id.iv_gun);
            mDelete = itemView.findViewById(R.id.iv_delete);

            itemView.setOnClickListener(this);
            mDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.iv_delete) //checks if the user clciked in the delete button
            {
                if(mListener != null)
                {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION)
                    {
                        mListener.onDeleteClick(position); //triggers delete function
                    }
                }
            }
            else //if user clicked on anywhere in the cardview except the delete button
            {
                if(mListener != null)
                {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION)
                    {
                        mListener.onItemClick(position); //trigger new intent function
                    }
                }
            }
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

}
