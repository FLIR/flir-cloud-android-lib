package com.flir.cloud.ui.Views.AddTagsCustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Moti on 29-Aug-17.
 */

public class AddTagsCustomView extends LinearLayout {

    private Context mContext;
    private TextView mAddNewTagTextView;
    private ImageButton mAddNewTagButton;
    private LinearLayout mTagDetailsLinearLayout;
    private List<String> tagsNames = new ArrayList<String>();
    private LayoutInflater mInflater;

    public AddTagsCustomView(Context context, JSONObject dataText) {
        super(context);
        mContext = context;
        initViews();
        initData(dataText);
    }

    public AddTagsCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    public AddTagsCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews();
    }



    public void initViews() {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = mInflater.inflate(R.layout.add_tag_custom_view, null);
        dialogView.setLayoutParams(new
                LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        mAddNewTagTextView = (TextView) dialogView.findViewById(R.id.tv_tap_to_add_tag);
        mAddNewTagTextView.setOnClickListener(addTagClicked);
        mAddNewTagButton = (ImageButton) dialogView.findViewById(R.id.btn_tap_to_add_tag);
        mAddNewTagButton.setOnClickListener(addTagClicked);

        mTagDetailsLinearLayout = (LinearLayout) dialogView.findViewById(R.id.ll_tag_details_view);

        addView(dialogView);
    }

    View.OnClickListener addTagClicked = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == mAddNewTagTextView || v == mAddNewTagButton) {
                addNewTagClicked();
            }
        }
    };


    private void addNewTagClicked() {
        new MaterialDialog.Builder(mContext)
                .title(R.string.tag_custom_view_add_new_tag_dialog_tag_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(mContext.getResources().getString(R.string.tag_custom_view_add_new_tag_dialog_tag_name_hint), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().isEmpty()) {
                            if (!tagsNames.contains(input.toString())) {
                                tagsNames.add(input.toString());
                                addNewTagChild(input.toString(),"");
                            } else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.tag_custom_view_add_new_tag_dialog_error_message), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }).show();
    }

    private void addNewTagChild(String aTagName, String values) {
        ImageButton addValueButton;
        ImageButton removeTagButton;
        TextView tagName;
        TextView valuesTextView;
        final View dialogView = mInflater.inflate(R.layout.add_tag_custom_view_new_tag_item, null);
        ((TextView) dialogView.findViewById(R.id.add_tag_custom_view_tag_name)).setText(aTagName);
        addValueButton = (ImageButton) dialogView.findViewById(R.id.add_tag_custom_view_add_value_button);
        removeTagButton = (ImageButton) dialogView.findViewById(R.id.add_tag_custom_view_remove_data_row);
        removeTagButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tagsNames.remove(((TextView) dialogView.findViewById(R.id.add_tag_custom_view_tag_name)).getText().toString());
                mTagDetailsLinearLayout.removeView(dialogView);
            }
        });
        valuesTextView = (TextView) dialogView.findViewById(R.id.add_tag_custom_view_tag_values);
        valuesTextView.setText(values);
        tagName = (TextView) dialogView.findViewById(R.id.add_tag_custom_view_tag_name);
        addValueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewValueClicked(tagName, valuesTextView);
            }
        });
        mTagDetailsLinearLayout.addView(dialogView);
    }

    private void addNewValueClicked(TextView tagName, TextView valuesTextView) {

        String title = mContext.getResources().getString(R.string.tag_custom_view_add_new_value_title) + tagName.getText() + "" + mContext.getResources().getString(R.string.tag_custom_view_add_new_value_tag_title);
        new MaterialDialog.Builder(mContext)
                .title(title)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(mContext.getResources().getString(R.string.tag_custom_view_add_new_value_hint), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (valuesTextView.getText().toString().isEmpty()) {
                            valuesTextView.setText(input);
                        } else {
                            valuesTextView.setText(valuesTextView.getText() + ", " + input);
                        }
                    }
                }).show();
    }


    private void initData(JSONObject dataText) {
        if(dataText != null) {
            Iterator<String> iter = dataText.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = dataText.get(key);
                    String d = value.toString().replaceAll("[{\"}]","");
                    d = d.replace("[","");
                    d = d.replace("]","");
                    addNewTagChild(key, d);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        }
    }

}
