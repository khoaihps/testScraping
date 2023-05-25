import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Person {
    public String name;
    public ArrayList<String> birth, death;
    public ArrayList<Pair> infoList;
    public ArrayList<String> description;

    public Person() {
        this.name = new String();
        this.birth = new ArrayList<>();
        this.death = new ArrayList<>();
        this.infoList = new ArrayList<>();
        this.description = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public ArrayList<String> getBirth() {
        return birth;
    }
    public ArrayList<String> getDeath() {
        return death;
    }
    public ArrayList<Pair> getInfoList() {
        return infoList;
    }
    public ArrayList<String> getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setBirth(ArrayList<String> birth) {
        this.birth = birth;
    }
    public void setDeath(ArrayList<String> death) {
        this.death = death;
    }
    public void setInfoList(ArrayList<Pair> infoList) {
        this.infoList = infoList;
    }
    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public static final Gson gson = new Gson();

    public static void writePersonToJson(Person person) {
        String json = gson.toJson(person);
        System.out.println(json);
    }

    public static void writePersonToJsonFile(Person person, String filename) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(person);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
        writer.write(json);
        writer.close();
    }

}
