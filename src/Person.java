
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Person {

    public ArrayList<Pair> infoList = new ArrayList<Pair>();

    public Person(ArrayList<Pair> infoList) {
        this.infoList = infoList;
    }

    public ArrayList<Pair> getInfoList() {
        return infoList;
    }

    public void setInfoList(ArrayList<Pair> infoList) {
        this.infoList = infoList;
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
