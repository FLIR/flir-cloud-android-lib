package com.flir.cloud.ui.fileExplorer.FoldersClasses.FolderRecyclerViewfiles;

/**
 * Created by Moti on 20-Jul-17.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.cloud.R;

public class FolderRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView volumeName;
    public ImageView volumeIconFolder;
    private Context mContext;
    public View itemView;

    public FolderRecyclerViewHolders(Context context, View itemView) {
        super(itemView);
        mContext = context;
        this.itemView = itemView;
        this.itemView.setOnClickListener(this);
     //   initDialog();
        volumeName = (TextView)itemView.findViewById(R.id.folder_name);
        volumeIconFolder = (ImageView)itemView.findViewById(R.id.folder_photo);
    }

    @Override
    public void onClick(View view) {
       /* if(getAdapterPosition() == VolumeMainPageActivity.listItemSize -1){
            showUpdateDialog();// =================open relevant value====================
        }*/
    }
}