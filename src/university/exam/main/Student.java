package university.exam.main;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Student extends Person {

	private int stage;
	private Map<String, Integer> moduleMarks;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identification of this person
	 * @param firstName
	 *            First name of this person
	 * @param lastName
	 *            Last name of this person
	 * @param stage
	 *            Which stage is the student enrolled in
	 */
	public Student(String id, String firstName, String lastName, int stage) {
		this(id, firstName, lastName, stage, new TreeMap<String, Integer>());
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identification of this person
	 * @param firstName
	 *            First name of this person
	 * @param lastName
	 *            Last name of this person
	 * @param dateOfBirth
	 *            Date of birth of this person
	 * @param stage
	 *            Which stage is the student enrolled in
	 * @param moduleMarks
	 *            Subject as key and the corresponding marks as value
	 */
	public Student(String id, String firstName, String lastName, int stage, Map<String, Integer> moduleMarks) {
		this(id, firstName, null, lastName, stage, moduleMarks);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identification of this person
	 * @param firstName
	 *            First name of this person
	 * @param middleName
	 *            Middle name of this person
	 * @param lastName
	 *            Last name of this person
	 * @param stage
	 *            Which stage is the student enrolled in
	 * @param moduleMarks
	 *            Subject as key and the corresponding marks as value
	 */
	public Student(String id, String firstName, String middleName, String lastName, int stage,
			Map<String, Integer> moduleMarks) {
		super(id, firstName, middleName, lastName);
		this.stage = stage;
		this.moduleMarks = moduleMarks;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identification of this person
	 * @param firstName
	 *            First name of this person
	 * @param middleName
	 *            Middle name of this person
	 * @param lastName
	 *            Last name of this person
	 * @param emailAddress
	 *            Email address of this person
	 * @param stage
	 *            Which stage is the student enrolled in
	 * @param moduleMarks
	 *            Subject as key and the corresponding marks as value
	 */
	public Student(String id, String firstName, String middleName, String lastName, String emailAddress, int stage,
			Map<String, Integer> moduleMarks) {
		super(id, firstName, middleName, lastName, emailAddress);
		this.stage = stage;
		this.moduleMarks = moduleMarks;
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
	 * This method sets a module where the student is registered.
	 * 
	 * @param code
	 *            Module mode
	 */
	public void setModule(String code) {
		this.moduleMarks.put(code, new Integer(0));
	}

	/**
	 * This method sets a module where the student is registered and the exam
	 * marks he got.
	 * 
	 * @param code
	 *            Module mode
	 * @param marks
	 *            Exam marks of the given module
	 */
	public void setModuleMarks(String code, int marks) {
		this.moduleMarks.put(code, marks);
	}

	/**
	 * This method sets which stage is the current student registered.
	 * 
	 * @param stage
	 *            Which stage is the current student registered
	 */
	public void setStage(int stage) {
		this.stage = stage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Profile\n-------\n");
		builder.append("Student id: " + super.getId() + ", Name: " + super.getName() + ", stage: " + this.stage
				+ ", email address: " + super.getEmailAddress());

		if (this.moduleMarks.size() > 0) {
			builder.append("\n\nExam results\n------------\n");

			for (Entry<String, Integer> s : this.moduleMarks.entrySet()) {
				builder.append(s.getKey() + ": " + s.getValue() + "\n");
			}
		}

		return builder.toString();
	}
}