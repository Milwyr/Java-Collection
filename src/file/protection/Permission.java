package file.protection;

import java.util.List;

/**
 * This class includes the role, password, as well as all the files the role can
 * read and write.
 * 
 * @author Milton
 *
 */
public class Permission {
	private String role;
	private String password;
	private List<String> readFiles;
	private List<String> writeFiles;

	public Permission(String role, String password, List<String> readFiles, List<String> writeFiles) {
		this.role = role;
		this.password = password;
		this.readFiles = readFiles;
		this.writeFiles = writeFiles;
	}

	public String getRole() {
		return this.role;
	}

	public String getPassword() {
		return this.password;
	}

	public List<String> getReadFiles() {
		return this.readFiles;
	}

	public List<String> getWriteFiles() {
		return this.writeFiles;
	}
}
