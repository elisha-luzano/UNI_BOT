package ai.api.uni.Model;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 4/2/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Subject {
    private Integer id;
    private String name;
    private String date_time;
    private String location;
    private Integer absences;

    public Subject() {}

    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setAbsences(Integer absences) {
        this.absences = absences;
    }
    public void addOneAbsent() {
        this.absences ++;
    }

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getDate_time() {
        return this.date_time;
    }
    public String getLocation() {
        return this.location;
    }
    public Integer getAbsences() {
        return this.absences;
    }

    public static void insert(String name, String date_time, String location, Integer absences, DBHelper database) {
        database.insertSubject(name, date_time, location, absences);
    }

    public static void addAbsence(Integer id, Integer absences, DBHelper database) {
        database.addSubjectAbsent(id, absences);
    }

    public static void updateAbsence(Integer id, Integer new_amount, DBHelper database) {
        database.updateSubjectAbsent(id, new_amount);
    }

    public static ArrayList<Subject> getAll(DBHelper database) {
        return database.getAllSubjects();
    }

    public static void deleteAll(DBHelper database) {
        database.deleteAllSubjects();
    }
}
