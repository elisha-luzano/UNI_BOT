package ai.api.uni.Model;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 3/9/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Folder {
    private Integer id;
    private String name;

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static Boolean insert(String name, DBHelper database) {
        return database.insertFolder(name);
    }

    public static Folder getOne(String name, DBHelper database) {
        return database.getOneFolder(name);
    }

    public static Folder getOne(Integer id, DBHelper database) {
        return database.getOneFolder(id);
    }

    public static ArrayList<Folder> getAll(DBHelper database) {
        return database.getAllFolders();
    }

    public static void delete(Integer id, DBHelper database) {
        database.deleteFolder(id);
    }

    public static Boolean delete(String name, DBHelper database) {
        return database.deleteFolder(name);
    }

    public static Boolean update(Integer id, String name, DBHelper database) {
        return database.updateFolder(id, name);
    }

    public static Boolean update(String old_name, String new_name, DBHelper database) {
        return database.updateFolder(old_name, new_name);
    }

    public static void deleteAll(DBHelper database) {
        database.deleteAllFolders();
    }

}
