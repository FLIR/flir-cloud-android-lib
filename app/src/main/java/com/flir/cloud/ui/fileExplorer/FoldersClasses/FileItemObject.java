package com.flir.cloud.ui.fileExplorer.FoldersClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Moti on 01-Aug-17.
 */

@SuppressWarnings("id")
public class FileItemObject implements Parcelable {

    private String fileId;
    private String volume;
    private String fileName;
    private String created;
    private String modified;
    private int size;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FileItemObject(String fileId, String volume, String fileName, String created, String modified, int size) {
        this.fileId = fileId;
        this.volume = volume;
        this.fileName = fileName;
        this.created = created;
        this.modified = modified;
        this.size = size;
    }

    public FileItemObject(Parcel in) {
        this.fileId = in.readString();
        this.volume = in.readString();
        this.fileName = in.readString();
        this.created = in.readString();
        this.modified = in.readString();
        this.size = in.readInt();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        // TODO Auto-generated method stub

        dest.writeString(fileId);
        dest.writeString(volume);
        dest.writeString(fileName);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeInt(size);
    }

    public static final Parcelable.Creator<FileItemObject> CREATOR = new Parcelable.Creator<FileItemObject>() {
        public FileItemObject createFromParcel(Parcel in) {
            return new FileItemObject(in);
        }

        public FileItemObject[] newArray(int size) {
            return new FileItemObject[size];
        }
    };
}