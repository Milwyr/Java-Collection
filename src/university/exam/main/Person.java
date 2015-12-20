package university.exam.main;

public class Person {

	private String id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String emailAddress;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identification of this person
	 * @param firstName
	 *            First name of this person
	 * @param lastName
	 *            Last name of this person
	 */
	public Person(String id, String firstName, String lastName) {
		this(id, firstName, null, lastName);
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
	 */
	public Person(String id, String firstName, String middleName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
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
	 */
	public Person(String id, String firstName, String middleName, String lastName, String emailAddress) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.emailAddress = emailAddress;
	}

	/**
	 * This method returns the id of this person.
	 * 
	 * @return id of this person
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * This method returns the first name of this person.
	 * 
	 * @return First name of this person
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * This getter method returns the middle name of this person.
	 * 
	 * @return Middle name of this person
	 */
	public String getMiddleName() {
		return this.middleName;
	}

	/**
	 * This method returns the last name of this person.
	 * 
	 * @return Last name of this person
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * This method returns the full name of this person.
	 * 
	 * @return Full name of this person
	 */
	public String getName() {
		if (this.lastName == null || this.lastName.isEmpty()) {
			return this.firstName + " " + this.lastName;
		}
		return this.firstName + " " + this.middleName + " " + this.lastName;
	}

	/**
	 * This method returns the email address of the person.
	 * 
	 * @return Email address of the person
	 */
	public String getEmailAddress() {
		return this.emailAddress;
	}

	/**
	 * This method sets the first name of this person.
	 * 
	 * @param firstName
	 *            A new first name
	 */
	protected void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * This method sets the middle name of this person.
	 * 
	 * @param middleName
	 *            A new middle name
	 */
	protected void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * This method sets the last name of this person.
	 * 
	 * @param firstName
	 *            A new last name
	 */
	protected void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * This method sets the email address of this person.
	 * 
	 * @param emailAddress
	 *            A new email address
	 */
	protected void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}