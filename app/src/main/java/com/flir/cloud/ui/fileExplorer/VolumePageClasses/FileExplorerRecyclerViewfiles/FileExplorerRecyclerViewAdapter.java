package com.flir.cloud.ui.fileExplorer.VolumePageClasses.FileExplorerRecyclerViewfiles;

/**
 * Created by Moti on 20-Jul-17.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.R;
import com.flir.cloud.ui.fileExplorer.FoldersClasses.FolderActivity;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.UpdateVolumeDialogView.UpdateVolumeDialogView;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.VolumeMainPageActivity;

import java.util.List;

public class FileExplorerRecyclerViewAdapter extends RecyclerView.Adapter<FileExplorerRecyclerViewHolders> {

    private List<VolumeMainPageActivity.VolumeItemObject> itemList;
    private Context context;
    private int indexItemSelected = -1;
    private LinearLayout mLlToolBar;
    private ViewGroup mParent;
    private MaterialDialog.Builder updateVolumeDialog;


    public FileExplorerRecyclerViewAdapter(Context context, List<VolumeMainPageActivity.VolumeItemObject> itemList, LinearLayout aLlToolBar) {
        this.itemList = itemList;
        this.context = context;
        mLlToolBar = aLlToolBar;
    }

    @Override
    public FileExplorerRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        mParent = parent;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_explorer_main_volume_grid_item, null);
        layoutView.setBackgroundColor(Color.TRANSPARENT);

        return new FileExplorerRecyclerViewHolders(parent.getContext(), layoutView);
    }

    @Override
    public void onBindViewHolder(FileExplorerRecyclerViewHolders holder, int position) {
        holder.volumeName.setText(itemList.get(position).getName());
        holder.volumeIconFolder.setImageResource(itemList.get(position).getPhoto());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!itemList.get(position).getName().equals(context.getResources().getString(R.string.cloud_storage_lambda_add_new_folder))) {
                    indexItemSelected = position;
                    initToolBarOnClick(indexItemSelected, holder);
                    showEditToolBar();
                    notifyDataSetChanged();
                }
                return true;
            }
        });

        if (indexItemSelected == position) {
            changeItemToSelectedMode(holder,true);
        } else {
            changeItemToSelectedMode(holder,false);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemList.get(position).getName().equals(context.getResources().getString(R.string.cloud_storage_lambda_add_new_folder))) {
                    Intent i = new Intent(context, FolderActivity.class);
                    i.putExtra(LambdaConstant.FILE_EXPLORER_VOLUME_ID, itemList.get(position).getId());
                    i.putExtra(LambdaConstant.FILE_EXPLORER_FILES_FOLDER_NAME, itemList.get(position).getName());
                    context.startActivity(i);
                }else{
                    ((VolumeMainPageActivity) mParent.getContext()).showAddFolderDialog();
                }
            }
        });


    }

    private void changeItemToSelectedMode(FileExplorerRecyclerViewHolders holder,boolean isSelected) {
        if(isSelected) {
            holder.itemView.findViewById(R.id.folder_photo).setAlpha(0.5f);
        }else{
            holder.itemView.findViewById(R.id.folder_photo).setAlpha(1);
        }
    }

    private void showEditToolBar() {
        mLlToolBar.findViewById(R.id.ll_tool_bar_add_volume).setVisibility(View.GONE);
        mLlToolBar.findViewById(R.id.ll_edit_tool_bar).setVisibility(View.VISIBLE);
    }

    private void showAddVolumeToolBar() {
        mLlToolBar.findViewById(R.id.ll_tool_bar_add_volume).setVisibility(View.GONE);
        mLlToolBar.findViewById(R.id.ll_edit_tool_bar).setVisibility(View.GONE);

    }

    private void initToolBarOnClick(int aItemIndex, FileExplorerRecyclerViewHolders holder) {
        //tool bar exit button
        mLlToolBar.findViewById(R.id.tool_bar_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddVolumeToolBar();

                indexItemSelected = -1;
                notifyDataSetChanged();
            }
        });
        //tool bar delete volume
        mLlToolBar.findViewById(R.id.tool_bar_delete_volume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteVolumeDialogMassage(itemList.get(aItemIndex).getId(), holder);
                changeItemToSelectedMode(holder,false);
            }
        });

        mLlToolBar.findViewById(R.id.tool_bar_rename_volume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateVolumeDialogView updateVolumeDialogView = new UpdateVolumeDialogView(context, itemList.get(aItemIndex).getId() , ((VolumeMainPageActivity) mParent.getContext()).getVolumeMainPagePresenter(), ((VolumeMainPageActivity) mParent.getContext()));

                updateVolumeDialog = new MaterialDialog.Builder(context)
                        .title(context.getResources().getString(R.string.file_explorer_update_volume_item))
                        .customView(updateVolumeDialogView, false)
                        .positiveText(R.string.dialog_done);
                updateVolumeDialog.show();

                showAddVolumeToolBar();
                indexItemSelected = -1;

            }
        });


    }

    private void showDeleteVolumeDialogMassage(String serial, FileExplorerRecyclerViewHolders holder) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    ((VolumeMainPageActivity) mParent.getContext()).getVolumeMainPagePresenter().deleteVolumeResource(serial);
                    showAddVolumeToolBar();
                    indexItemSelected = -1;
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    changeItemToSelectedMode(holder,true);
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.dialog_massage_massage)).setPositiveButton(context.getResources().getString(R.string.dialog_massage_yes), dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.dialog_massage_no), dialogClickListener).show();
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}