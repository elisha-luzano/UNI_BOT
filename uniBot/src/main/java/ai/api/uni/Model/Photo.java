package ai.api.uni.Model;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 3/8/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Photo {
    private Integer id;
    private String name;
    private Integer folder_id;

    public Photo() {}

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public Integer getFolderId() {
        return this.folder_id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFolder(Integer folder_id) {
        this.folder_id = folder_id;
    }

    public static void insert(String name, Integer folder, DBHelper database) {
        database.insertPhoto(name, folder);
    }

    public static ArrayList<Photo> getAll(DBHelper database) {
        return database.getAllPhotos();
    }

    public static ArrayList<Photo> getAllInFolder(Integer folder_id, DBHelper database) {
        return database.getAllPhotos(folder_id);
    }

    public static void delete(Integer id, DBHelper database) {
        database.deletePhoto(id);
    }

    public static void deleteAll(DBHelper database) {
        database.deleteAllPhotos();
    }

    public static void update(Integer id, Integer folder_id, DBHelper database) {
        database.updatePhoto(id, folder_id);
    }
    public static void update(Integer id, String name, DBHelper database) {
        database.updatePhoto(id, name);
    }
}
