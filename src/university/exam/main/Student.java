package university.exam.main;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Student {

	private String id;
	private String name;
	private int stage;
	private Map<String, Integer> moduleMarks = new TreeMap<String, Integer>();

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Student id
	 * @param name
	 *            Student name
	 */
	public Student(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Student id
	 * @param name
	 *            Student name
	 * @param stage
	 *            Which stage is the student enrolled in
	 */
	public Student(String id, String name, int stage) {
		this(id, name);
		this.stage = stage;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Student id
	 * @param name
	 *            Student name
	 * @param stage
	 *            Which stage is the student enrolled in
	 * @param moduleMarks
	 *            Subject as key and the corresponding marks as value
	 */
	public Student(String id, String name, int stage, Map<String, Integer> moduleMarks) {
		this(id, name, stage);
		this.moduleMarks = moduleMarks;
	}

	/**
	 * This getter method returns the student id.
	 * 
	 * @return Student id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * This getter method returns the student name.
	 * 
	 * @return Student name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This getter method returns which stage the student is enrolled.
	 * 
	 * @return Which stage the student is enrolled
	 */
	public int getStage() {
		return this.stage;
	}

	/**
	 * This method returns a map where the key is the module id and value is the
	 * corresponding marks of that subject.
	 * 
	 * @return Subject as key and the corresponding marks as value
	 */
	public Map<String, Integer> getModuleMarks() {
		return this.moduleMarks;
	}

	/**
	 * This method adds a module where the student is registered.
	 * 
	 * @param code
	 *            Module mode
	 */
	public void addModule(String code) {
		this.moduleMarks.put(code, new Integer(0));
	}

	/**
	 * This method adds a module where the student is registered and the exam
	 * marks he got.
	 * 
	 * @param code
	 *            Module mode
	 * @param marks
	 *            Exam marks of the given module
	 */
	public void addModule(String code, int marks) {
		this.moduleMarks.put(code, marks);
	}

	/**
	 * This method updates the name of the current student.
	 * 
	 * @param name
	 *            Name of the current student
	 */
	public void updateName(String name) {
		this.name = name;
	}

	/**
	 * This method updates which stage is the current student registered.
	 * 
	 * @param stage
	 *            Which stage is the current student registered
	 */
	public void updateStage(int stage) {
		this.stage = stage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Profile\n-------\n");
		builder.append("Student id: " + this.id + ", Name: " + this.name + ", stage: " + this.stage);
		
		if (this.moduleMarks.size() > 0) {
			builder.append("\n\nExam results\n------------\n");

			for (Entry<String, Integer> s: this.moduleMarks.entrySet()) {
				builder.append(s.getKey() + ": " + s.getValue() + "\n");
			}
		}
		
		return builder.toString();
	}
}