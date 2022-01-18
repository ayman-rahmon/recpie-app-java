package com.tatsujin.recipe.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.tatsujin.recipe.R;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title ;
    public TextView publisher ;
    public AppCompatImageView image ;
    public TextView socialScore ;
    private onRecipeListener onRecipeListener ;

    public RecipeViewHolder(@NonNull View itemView, com.tatsujin.recipe.adapters.onRecipeListener mOnRecipeListener) {
        super(itemView);
        this.onRecipeListener = mOnRecipeListener ;
        title = itemView.findViewById(R.id.recipe_title);
        publisher = itemView.findViewById(R.id.recipe_publisher);
        image = itemView.findViewById(R.id.recipe_image);
        socialScore = itemView.findViewById(R.id.recipe_social_score);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        onRecipeListener.onRecipeClick(getAbsoluteAdapterPosition());


    }

}
