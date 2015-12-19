package university.exam.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Launcher {

	private static final String STUDENTS_DATA_FILE_PATH = "src\\university\\exam\\resources\\students.json";
	private static final String MODULES_DATA_FILE_PATH = "src\\university\\exam\\resources\\modules.json";
	
	private static List<Student> students = new ArrayList<Student>();
	private static List<Module> modules = new ArrayList<Module>();

	public static void main(String[] args) {
		try {
			readStudentsFromJson();
			readModulesFromJson();
			students.get(0).addModule("CSC2021", 76);
			students.get(0).addModule("CSC2022", 78);
			students.get(0).addModule("CSC2023", 90);
			
			for (Student s: students) {
				System.out.println(s + "\n");
			}
			
			System.out.println("Module Information");
			for (Module m: modules) {
				System.out.println(m);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void readStudentsFromJson() throws FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(STUDENTS_DATA_FILE_PATH));
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
		
		for (JsonElement e : jsonObject.get("students").getAsJsonArray()) {
			String id = e.getAsJsonObject().get("id").getAsString();
			String name = e.getAsJsonObject().get("name").getAsString();
			int stage = e.getAsJsonObject().get("stage").getAsInt();
			students.add(new Student(id, name, stage));
		}
	}
	
	private static void readModulesFromJson() throws FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(MODULES_DATA_FILE_PATH));
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
		
		for (JsonElement e : jsonObject.get("modules").getAsJsonArray()) {
			String code = e.getAsJsonObject().get("code").getAsString();
			String name = e.getAsJsonObject().get("name").getAsString();
			int stage = e.getAsJsonObject().get("stage").getAsInt();
			int credits = e.getAsJsonObject().get("credits").getAsInt();
			modules.add(new Module(code, name, stage, credits));
		}
	}
}
