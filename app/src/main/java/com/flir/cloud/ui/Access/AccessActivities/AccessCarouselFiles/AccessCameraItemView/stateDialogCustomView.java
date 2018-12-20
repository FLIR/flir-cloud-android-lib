package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flir.cloud.R;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.VerticalStateList.ExpandableRecyclerAdapter;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.VerticalStateList.Ingredient;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.VerticalStateList.Recipe;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.VerticalStateList.RecipeAdapter;
import com.flir.sdk.models.Device.GetDeviceStateResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moti on 11/5/2018.
 */

public class stateDialogCustomView  extends LinearLayout {

    private Context mContext;
    private View dialogView;
    private List<GetDeviceStateResponse> mResponse;
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private EditText mSearchItem;
    private List<Recipe> mRecipesList;
    private TextWatcher mTextWatcherChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            List<Recipe> currentSearch = getCurrentListSearch(s);
            mAdapter = new RecipeAdapter(mContext, currentSearch);
            mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                @UiThread
                @Override
                public void onParentExpanded(int parentPosition) {
                    //plus_list_view
                }

                @UiThread
                @Override
                public void onParentCollapsed(int parentPosition) {
                }
            });

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private List<Recipe> getCurrentListSearch(CharSequence s) {
        s = s.toString().toLowerCase();
        List<Recipe> ans = new ArrayList<>();
        for (int i = 0; i < mRecipesList.size(); i++) {
            if(mRecipesList.get(i).getName().toLowerCase().contains(s)){
                ans.add(mRecipesList.get(i));
            }
        }
        return ans;
    }

    public stateDialogCustomView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public stateDialogCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public stateDialogCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public stateDialogCustomView(Context context, List<GetDeviceStateResponse> response) {
        super(context);
        mContext = context;
        mResponse = response;
        init();
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialog_state_list_custom_view, null);
        mSearchItem = (EditText) dialogView.findViewById(R.id.et_search_in_state_list);

        mRecyclerView = (RecyclerView) dialogView.findViewById(R.id.rv_devices_state);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialogView.setLayoutParams(layoutParams);
        mRecipesList = new ArrayList<>();
        Ingredient channel = null;
        Ingredient childReportedValue = null;
        Ingredient childDesiredValue = null;
        Ingredient readOnly = null;
        List<Ingredient> ingredients = new ArrayList<>();
        Recipe header = null;
        for (int i = 0; i < mResponse.size(); i++) {
            GetDeviceStateResponse item = mResponse.get(i);
            if(item.channel != null && !item.channel.isEmpty()){
                channel = new Ingredient("channel = " + item.channel, true);
                ingredients.add(channel);
            }

            if(item.reportedValue != null && !item.reportedValue.isEmpty()){
                childReportedValue = new Ingredient("reportedValue = " + item.reportedValue, true);
                ingredients.add(childReportedValue);
            }

            if(item.desiredValue != null && !item.desiredValue.isEmpty()){
                childDesiredValue = new Ingredient("desiredValue = " + item.desiredValue, true);
                ingredients.add(childDesiredValue);
            }

            childDesiredValue = new Ingredient("readOnly = " + item.readOnly, true);
            ingredients.add(childDesiredValue);

            if(ingredients.size() != 0){
                header = new Recipe(item.key, ingredients);
            }
            if(header != null){
                mRecipesList.add(header);
            }
            putSavingSearchOnTheFirstItem();
            ingredients = new ArrayList<>();
            header = null;
        }


        mAdapter = new RecipeAdapter(mContext, mRecipesList);
        mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                //plus_list_view
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mSearchItem.addTextChangedListener(mTextWatcherChangedListener);
        addView(dialogView);
    }

    private void putSavingSearchOnTheFirstItem() {
        int searchPosition = 0;
        String savedItemName = LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_STATE_ITEM_SAVED_NAME, "");
        for (int i = 0; i < mRecipesList.size(); i++) {
            if(mRecipesList.get(i).getName().equals(savedItemName)){
                searchPosition = i;
            }
        }
        Recipe saveItem = mRecipesList.get(searchPosition);
        mRecipesList.remove(searchPosition);
        mRecipesList.add(0,saveItem);
    }

}