package com.example.androidfinal;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Guns> mGuns;
    private SettingsSingleton singleton;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<Guns> guns)
    {
        this.mContext = context;
        this.mGuns = guns;
        singleton = SettingsSingleton.getInstance();
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.catalog_item, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        Guns gunCurrent = mGuns.get(i);

        Picasso.with(mContext).load(gunCurrent
                .getImageUrl())
                .fit()
                .centerInside()
                .into(imageViewHolder.mImage);

        imageViewHolder.mName.setText(gunCurrent.getName());
        imageViewHolder.mClass.append(gunCurrent.getWeaponClass());
        imageViewHolder.mFiringMode.append(gunCurrent.getFiringMode());
        imageViewHolder.mWeight.append(gunCurrent.getWeight() + "kg");

        if(singleton.getCurrency() == 1)
        {
            imageViewHolder.mPrice.setText("RM " + (gunCurrent.getPrice() * 4));
        }
        else
        {
            imageViewHolder.mPrice.setText("$ " + gunCurrent.getPrice());
        }

        imageViewHolder.mDamage.append(gunCurrent.getDamage() + "HP");
    }

    @Override
    public int getItemCount() {
        return mGuns.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener

    {
        public ImageView mImage;
        public TextView mName, mClass, mFiringMode, mWeight, mPrice, mDamage;

        public ImageViewHolder(View itemView)
        {
            super(itemView);

            mImage = itemView.findViewById(R.id.iv_weapon);
            mName = itemView.findViewById(R.id.tv_name);
            mClass = itemView.findViewById(R.id.tv_class);
            mFiringMode = itemView.findViewById(R.id.tv_firingMode);
            mWeight = itemView.findViewById(R.id.tv_weight);
            mPrice = itemView.findViewById(R.id.tv_price);
            mDamage = itemView.findViewById(R.id.tv_damage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener != null)
            {
                int pos = getAdapterPosition();

                if(pos != RecyclerView.NO_POSITION)
                {
                    mListener.onItemClick(pos);
                }
            }
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }
}
