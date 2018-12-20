package com.flir.cloud.ui.fileExplorer.VolumePageClasses.FileExplorerRecyclerViewfiles;

/**
 * Created by Moti on 20-Jul-17.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.cloud.R;

public class FileExplorerRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView volumeName;
    public ImageView volumeIconFolder;
    private Context mContext;
    public View itemView;

    public FileExplorerRecyclerViewHolders(Context context, View itemView) {
        super(itemView);
        mContext = context;
        this.itemView = itemView;
        this.itemView.setOnClickListener(this);
        volumeName = (TextView)itemView.findViewById(R.id.folder_name);
        volumeIconFolder = (ImageView)itemView.findViewById(R.id.folder_photo);
    }

    @Override
    public void onClick(View view) {

    }
}