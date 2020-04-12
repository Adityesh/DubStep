package com.example.dubstep.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubstep.Interface.ItemClickListener;
import com.example.dubstep.R;
import com.google.android.material.button.MaterialButton;

public class FoodItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mFoodItemName;
    //public ImageView mFoodImage;
    public TextView mFoodItemPrice;
    public MaterialButton mAddToCart;

    public ItemClickListener listener;

    public FoodItemViewHolder(@NonNull View itemView) {
        super(itemView);

        mFoodItemName = (TextView) itemView.findViewById(R.id.food_name);
        mFoodItemPrice = (TextView) itemView.findViewById(R.id.food_price);
        //mFoodImage = (ImageView) itemView.findViewById(R.id.food_image);
        mAddToCart = (MaterialButton) itemView.findViewById(R.id.addtocart_btn);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}