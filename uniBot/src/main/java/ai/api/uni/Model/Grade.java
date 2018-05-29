package ai.api.uni.Model;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 3/18/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Grade {
    String code;
    Float grade;
    String term;
    String title;
    Float units;

    public Grade(String code, Float grade, String term, String title, Float units) {
        this.code = code;
        this.grade = grade;
        this.term = term;
        this.title = title;
        this.units = units;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void setGrade(Float grade) {
        this.grade = grade;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUnits(Float units) {
        this.units = units;
    }


    public String getCode() {
        return this.code;
    }
    public Float getGrade() {
        return this.grade;
    }
    public String getTerm() {
        return this.term;
    }
    public String getTitle() {
        return this.title;
    }
    public Float getUnits() {
        return this.units;
    }

    public static void insert(String code, Float grade, String term, String title, Float units, DBHelper database) {
        database.insertGrade(code, grade, term, title, units);
    }

    public static ArrayList<Grade> getAll(DBHelper database) {
        return database.getAllGrades();
    }

    public static void deleteAll(DBHelper database) {
        database.deleteAllGrades();
    }
}
