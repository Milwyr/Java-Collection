package university.exam.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
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

			for (Student s : students) {
				System.out.println(s + "\n");
			}

			System.out.println("Module Information");
			for (Module m : modules) {
				System.out.println(m);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void readStudentsFromJson() throws FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(STUDENTS_DATA_FILE_PATH));
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(reader).getAsJsonObject();

		for (JsonElement rootElement : jsonObject.get("students").getAsJsonArray()) {
			String id = rootElement.getAsJsonObject().get("id").getAsString();

			JsonElement nameElement = rootElement.getAsJsonObject().get("name");
			String firstName = nameElement.getAsJsonObject().get("firstName").getAsString();

			String middleName = null;
			if (nameElement.getAsJsonObject().get("middleName") != null) {
				middleName = nameElement.getAsJsonObject().get("middleName").getAsString();
			}

			String lastName = nameElement.getAsJsonObject().get("lastName").getAsString();

			int stage = rootElement.getAsJsonObject().get("stage").getAsInt();

			Type mapType = new TypeToken<Map<String, Integer>>() {
			}.getType();
			JsonElement moduleMarkElement = rootElement.getAsJsonObject().get("moduleMarks");
			Map<String, Integer> moduleMarks = new Gson().fromJson(moduleMarkElement, mapType);
			
			String emailAddress = rootElement.getAsJsonObject().get("emailAddress").getAsString();
			
			students.add(new Student(id, firstName, middleName, lastName, emailAddress, stage, moduleMarks));
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