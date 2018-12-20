package com.flir.cloud.ui.fileExplorer.FoldersClasses.FolderRecyclerViewfiles;

/**
 * Created by Moti on 20-Jul-17.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.R;
import com.flir.cloud.Utils.FileUtils;
import com.flir.cloud.ui.fileExplorer.FoldersClasses.FileItemObject;
import com.flir.cloud.ui.fileExplorer.FoldersClasses.FolderActivity;


import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewHolders> {
    public static final String IC_CLOUD_STORAGE_ICON_TYPE_PREFIX = "ic_cloud_storage_icon_type_";
    //files
    private ArrayList<FileItemObject> itemList;
    private Context context;
    private ViewGroup mParent;
    private LinearLayout mLlToolBar;
    int indexFileSelected = -1;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;


    public FolderRecyclerViewAdapter(Context context, ArrayList<FileItemObject> itemList, LinearLayout aLlToolBar) {
        this.itemList = itemList;
        this.context = context;
        mLlToolBar = aLlToolBar;
    }

    @Override
    public FolderRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        mParent = parent;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_explorer_main_volume_grid_item, null);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(context);
        layoutView.setBackgroundColor(Color.TRANSPARENT);
        //change folder icon etc
        return new FolderRecyclerViewHolders(parent.getContext(), layoutView);
    }

    @Override
    public void onBindViewHolder(FolderRecyclerViewHolders holder, int position) {
        String fileName = itemList.get(position).getFileName();
        setRelevantIcon(holder,fileName);

        holder.volumeName.setText(itemList.get(position).getFileName());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                indexFileSelected = position;
                initToolBarOnClick(indexFileSelected);
                showEditToolBar();
                notifyDataSetChanged();
                return true;
            }
        });

        if (indexFileSelected == position) {
            holder.itemView.findViewById(R.id.folder_photo).setAlpha(0.5f);
        } else {
            holder.itemView.findViewById(R.id.folder_photo).setAlpha(1);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FolderActivity) mParent.getContext()).getFolderPresenter().presignedDownloadUrl(itemList.get(position).getFileId(),itemList.get(position).getFileName(), itemList.get(position).hashCode());
            }
        });


    }

    private void setRelevantIcon(FolderRecyclerViewHolders holder, String fileName) {
        String filePrefix = FileUtils.getFileContentType(fileName);
        String iconName = IC_CLOUD_STORAGE_ICON_TYPE_PREFIX + filePrefix;

        int imageResource = context.getResources().getIdentifier(iconName, "drawable" , context.getPackageName());
        if(imageResource != 0) {
            holder.volumeIconFolder.setImageResource(imageResource);
        }else{
            holder.volumeIconFolder.setImageResource(R.drawable.ic_cloud_storage_icon_type_default);
        }
    }


    private void showEditToolBar() {
        mLlToolBar.findViewById(R.id.ll_tool_bar_add_folder).setVisibility(View.GONE);
        mLlToolBar.findViewById(R.id.ll_edit_tool_bar_folder).setVisibility(View.VISIBLE);
    }

    private void showAddFolderToolBar() {
        mLlToolBar.findViewById(R.id.ll_tool_bar_add_folder).setVisibility(View.VISIBLE);
        mLlToolBar.findViewById(R.id.ll_edit_tool_bar_folder).setVisibility(View.GONE);

    }

    private void initToolBarOnClick(int aItemIndex) {
        //tool bar exit button
        mLlToolBar.findViewById(R.id.tool_bar_exit_folder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFolderToolBar();

                indexFileSelected = -1;
                notifyDataSetChanged();
            }
        });
        //tool bar delete volume
        mLlToolBar.findViewById(R.id.tool_bar_delete_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteFileDialogMassage(itemList.get(aItemIndex).getFileId());
            }
        });

        mLlToolBar.findViewById(R.id.tool_bar_rename_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(context)
                        .title(R.string.file_explorer_rename_file_title)
                        .content(R.string.file_explorer_new_name_file)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("", itemList.get(aItemIndex).getFileName(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if(!input.toString().equals(itemList.get(aItemIndex).getFileName())) {
                                    ((FolderActivity) mParent.getContext()).getFolderPresenter().renameFile(itemList.get(aItemIndex).getFileId(), input.toString(),true);
                                    indexFileSelected = -1;

                                    mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                                            LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_RENAMED,
                                            LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, itemList.get(aItemIndex).getFileName() + "/" + input.toString());
                                }
                            }
                        }).show();

            }
        });

        mLlToolBar.findViewById(R.id.tool_bar_file_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.file_explorer_file_details_title);
                builder.setMessage(context.getResources().getString(R.string.file_explorer_file_details_file_name) + ": " + itemList.get(aItemIndex).getFileName() + "\n" + "\n" +
                                   context.getResources().getString(R.string.file_explorer_file_details_size) + ": " + itemList.get(aItemIndex).getSize() + " " +context.getResources().getString(R.string.file_explorer_file_details_bytes) + "\n" + "\n" +
                                   context.getResources().getString(R.string.file_explorer_file_details_created) + ": " +  itemList.get(aItemIndex).getCreated() + "\n" + "\n" +
                                   context.getResources().getString(R.string.file_explorer_file_details_modified) + ": "  +  itemList.get(aItemIndex).getModified() + "\n");
                builder.setNegativeButton(R.string.file_explorer_file_details_cancel, null);
                builder.show();

            }
        });

        mLlToolBar.findViewById(R.id.tool_bar_update_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FolderActivity) mParent.getContext()).showFileChooser(FolderActivity.UPDATE_FILE_SELECT_CODE, itemList.get(aItemIndex).getFileId());
                indexFileSelected = -1;
            }
        });


    }

    private void showDeleteFileDialogMassage(String fileId) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    ((FolderActivity) mParent.getContext()).getFolderPresenter().presignedDeleteUrl(fileId);
                    showAddFolderToolBar();
                    indexFileSelected = -1;

                    mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                            LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_DELETED,
                            LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, fileId);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.file_explorer_delete_file_item)).setPositiveButton(context.getResources().getString(R.string.dialog_massage_yes), dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.dialog_massage_no), dialogClickListener).show();
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}