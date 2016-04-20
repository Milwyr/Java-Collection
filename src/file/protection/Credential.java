package file.protection;

public class Credential {
	private String fileName;
	private String readPassword;
	private String writePassword;

	/**
	 * Constructor
	 * 
	 * @param fileName
	 *            File name
	 * @param readPassword
	 *            Read password
	 * @param writePassword
	 *            Write password
	 */
	public Credential(String fileName, String readPassword, String writePassword) {
		this.fileName = fileName;
		this.readPassword = readPassword;
		this.writePassword = writePassword;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getReadPassword() {
		return this.readPassword;
	}

	public String getWritePassword() {
		return this.writePassword;
	}

	public void setReadPassword(String readPassword) {
		this.readPassword = readPassword;
	}

	public void setWritePassword(String writePassword) {
		this.writePassword = writePassword;
	}
}