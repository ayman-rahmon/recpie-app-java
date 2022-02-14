package com.tatsujin.recipe.adapters;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.tatsujin.recipe.R;
import com.tatsujin.recipe.models.Recipe;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    TextView categoryTitle;
    CircleImageView categoryImage;
    onRecipeListener listener ;
    RequestManager requestManager ;

    public CategoryViewHolder(@NonNull View itemView , onRecipeListener listener , RequestManager requestManager) {
        super(itemView);
        this.listener = listener;
        this.requestManager = requestManager ;
        categoryImage = itemView.findViewById(R.id.category_image);
        categoryTitle = itemView.findViewById(R.id.category_title);
        itemView.setOnClickListener(this);
    }


    public void onBind(Recipe recipe){
        // TODO(2) fix the uri for displaying the images properly...
        Uri path = Uri.parse("android.resource://com.tatsujin.recipe/drawable/" + recipe.getImage_url());
        requestManager.load(path).into(categoryImage);
        categoryTitle.setText(recipe.getTitle());

    }

    @Override
    public void onClick(View view) {
        listener.onCategoryClick(categoryTitle.getText().toString());
    }
}
