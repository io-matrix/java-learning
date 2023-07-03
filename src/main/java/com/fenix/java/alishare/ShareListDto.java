package com.fenix.java.alishare;

import java.util.List;

/**
 * @author feng
 * @desc 描述
 * @date 2022/12/21 10:09
 * @since v1
 */


class First_file {
    private boolean trashed;

    private String drive_id;

    private String file_id;

    private String created_at;

    private String encrypt_mode;

    private boolean hidden;

    private String name;

    private String parent_file_id;

    private boolean starred;

    private String status;

    private String type;

    private String updated_at;

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
    }

    public boolean getTrashed() {
        return this.trashed;
    }

    public void setDrive_id(String drive_id) {
        this.drive_id = drive_id;
    }

    public String getDrive_id() {
        return this.drive_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_id() {
        return this.file_id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setEncrypt_mode(String encrypt_mode) {
        this.encrypt_mode = encrypt_mode;
    }

    public String getEncrypt_mode() {
        return this.encrypt_mode;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean getHidden() {
        return this.hidden;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setParent_file_id(String parent_file_id) {
        this.parent_file_id = parent_file_id;
    }

    public String getParent_file_id() {
        return this.parent_file_id;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public boolean getStarred() {
        return this.starred;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }
}


class Items {
    private String category;

    private int popularity;

    private int browse_count;

    private String share_id;

    private String share_msg;

    private String share_name;

    private String description;

    private String expiration;

    private boolean expired;

    private String share_pwd;

    private String share_url;

    private String creator;

    private String drive_id;

    private String file_id;

    private List<String> file_id_list;

    private int preview_count;

    private int save_count;

    private int download_count;

    private String status;

    private String created_at;

    private String updated_at;

    private First_file first_file;

    private boolean enable_file_changed_notify;

    private String share_title;

    private String popularity_str;

    private String full_share_msg;

    private String share_subtitle;

    private String display_name;

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public void setBrowse_count(int browse_count) {
        this.browse_count = browse_count;
    }

    public int getBrowse_count() {
        return this.browse_count;
    }

    public void setShare_id(String share_id) {
        this.share_id = share_id;
    }

    public String getShare_id() {
        return this.share_id;
    }

    public void setShare_msg(String share_msg) {
        this.share_msg = share_msg;
    }

    public String getShare_msg() {
        return this.share_msg;
    }

    public void setShare_name(String share_name) {
        this.share_name = share_name;
    }

    public String getShare_name() {
        return this.share_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getExpiration() {
        return this.expiration;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean getExpired() {
        return this.expired;
    }

    public void setShare_pwd(String share_pwd) {
        this.share_pwd = share_pwd;
    }

    public String getShare_pwd() {
        return this.share_pwd;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_url() {
        return this.share_url;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setDrive_id(String drive_id) {
        this.drive_id = drive_id;
    }

    public String getDrive_id() {
        return this.drive_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_id() {
        return this.file_id;
    }

    public void setFile_id_list(List<String> file_id_list) {
        this.file_id_list = file_id_list;
    }

    public List<String> getFile_id_list() {
        return this.file_id_list;
    }

    public void setPreview_count(int preview_count) {
        this.preview_count = preview_count;
    }

    public int getPreview_count() {
        return this.preview_count;
    }

    public void setSave_count(int save_count) {
        this.save_count = save_count;
    }

    public int getSave_count() {
        return this.save_count;
    }

    public void setDownload_count(int download_count) {
        this.download_count = download_count;
    }

    public int getDownload_count() {
        return this.download_count;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }

    public void setFirst_file(First_file first_file) {
        this.first_file = first_file;
    }

    public First_file getFirst_file() {
        return this.first_file;
    }

    public void setEnable_file_changed_notify(boolean enable_file_changed_notify) {
        this.enable_file_changed_notify = enable_file_changed_notify;
    }

    public boolean getEnable_file_changed_notify() {
        return this.enable_file_changed_notify;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_title() {
        return this.share_title;
    }

    public void setPopularity_str(String popularity_str) {
        this.popularity_str = popularity_str;
    }

    public String getPopularity_str() {
        return this.popularity_str;
    }

    public void setFull_share_msg(String full_share_msg) {
        this.full_share_msg = full_share_msg;
    }

    public String getFull_share_msg() {
        return this.full_share_msg;
    }

    public void setShare_subtitle(String share_subtitle) {
        this.share_subtitle = share_subtitle;
    }

    public String getShare_subtitle() {
        return this.share_subtitle;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_name() {
        return this.display_name;
    }
}


public class ShareListDto {
    private List<Items> items;

    private String next_marker;

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public List<Items> getItems() {
        return this.items;
    }

    public void setNext_marker(String next_marker) {
        this.next_marker = next_marker;
    }

    public String getNext_marker() {
        return this.next_marker;
    }
}
