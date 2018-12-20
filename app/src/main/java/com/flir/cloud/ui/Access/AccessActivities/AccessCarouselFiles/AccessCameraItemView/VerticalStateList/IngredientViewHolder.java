package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.VerticalStateList;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.flir.cloud.R;


public class IngredientViewHolder extends ChildViewHolder {

    private TextView mIngredientTextView;

    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        mIngredientTextView = (TextView) itemView.findViewById(R.id.ingredient_textview);
    }

    public void bind(@NonNull Ingredient ingredient) {
        mIngredientTextView.setText(ingredient.getName());
    }
}
