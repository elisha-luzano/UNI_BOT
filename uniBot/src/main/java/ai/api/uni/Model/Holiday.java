package ai.api.uni.Model;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 5/8/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Holiday {
    public static void add(String name, String date, DBHelper database) {
        database.addHoliday(name, date);
    }

    public static String get(String date, DBHelper database) {
        return database.getHoliday(date);
    }

    public static void deleteAll(DBHelper database) {
        database.deleteAllHolidays();
    }
}
