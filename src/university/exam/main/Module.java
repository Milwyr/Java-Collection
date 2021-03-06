package university.exam.main;

public class Module {

	private String code;
	private String name;
	private int stage;
	private int credits;

	/**
	 * Constructor
	 * 
	 * @param code
	 *            Module code
	 * @param name
	 *            Module name
	 */
	public Module(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * Constructor
	 * 
	 * @param code
	 *            Module code
	 * @param name
	 *            Module name
	 * @param credits
	 *            Credits of the module
	 */
	public Module(String code, String name, int credits) {
		this(code, name);
		this.credits = credits;
	}

	/**
	 * Constructor
	 * 
	 * @param code
	 *            Module code
	 * @param name
	 *            Module name
	 * @param stage
	 *            Stage of the module
	 * @param credits
	 *            Credits of the module
	 */
	public Module(String code, String name, int stage, int credits) {
		this(code, name, credits);
		this.stage = stage;
	}

	/**
	 * This getter method returns the current module code.
	 * 
	 * @return The current module code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * This getter method returns name of the current module.
	 * 
	 * @return Name of the current module
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This getter method returns stage of the current module.
	 * 
	 * @return Stage of the current module
	 */
	public int getStage() {
		return this.stage;
	}

	/**
	 * This getter method returns the credits of the current module.
	 * 
	 * @return The credits of the current module
	 */
	public int getCredits() {
		return this.credits;
	}

	/**
	 * This method updates the module name.
	 * 
	 * @param name
	 *            The updated module name
	 */
	public void updateName(String name) {
		this.name = name;
	}

	/**
	 * This method updates stage of the current module.
	 * 
	 * @param stage
	 *            Stage of the current module
	 */
	public void updateStage(int stage) {
		this.stage = stage;
	}

	/**
	 * This method updates the module credits.
	 * 
	 * @param credits
	 *            The updated module credits
	 */
	public void updateCredit(int credits) {
		this.credits = credits;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Module code: " + this.code + ", Module name: " + this.name + ", Module stage: " + this.stage
				+ ", Credits: " + this.credits);

		return builder.toString();

	}
}