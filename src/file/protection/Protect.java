package file.protection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Protect {
	private static final String CURRENT_DIRECTORY = System.getProperty("user.dir") + "\\src\\file\\protection\\Q1\\";

	private static List<Credential> credentials = new ArrayList<Credential>();

	public static void main(String[] args) {
		String mode = args[2];
		initialisePlainTextCredentials();

		if (mode.equals("-c")) {
			check();
		} else {
			String fileName = args[3];
			String password = args[4];

			if (mode.equals("-e")) {
				if (isCorrectWritePassword(fileName, password)) {
					try {
						// Encrypt the plain text file
						String plainText = readPlainTextFile(CURRENT_DIRECTORY + fileName);
						CipherResult result = encrypt(password, plainText);

						// Encrypt the file with extension ".enc"
						try (PrintWriter writer = new PrintWriter(CURRENT_DIRECTORY + fileName + ".enc", "UTF-8")) {
							// Write the cipher text to a file
							writer.println(result.getCipherText());
							writer.println();
							
							// Write the AES key and initial vector to the text file
							writer.println(fileName + "\t" + result.getSecretKeyString() + "\t" + result.getIv());
							writer.println();
							
							// Write the signature to the text file
							writer.print(sign(result.getSecretKey(), result.getCipherText()));
						}

						File plainTextFile = new File(CURRENT_DIRECTORY + fileName);
						if (plainTextFile.exists())
							plainTextFile.delete();
						
						System.out.println("done");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Invalid write password");
				}
			} else if (mode.equals("-d")) {
				if (isCorrectReadPassword(fileName, password)) {
					String iv = null;
					String secretKey = null;
					String cipherText = null;
					String signature = null;

					try (BufferedReader reader = new BufferedReader(new FileReader(CURRENT_DIRECTORY + fileName))) {
						// Read cipher text
						cipherText = reader.readLine();
						reader.readLine();
						
						// Read secret key and iv
						String line = reader.readLine();
						String[] columns = line.split("\t");
						secretKey = columns[1];
						iv = columns[2];
						reader.readLine();
						
						// Read signature
						signature = reader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Remove extension .enc
					String outputFileName = fileName.substring(0, fileName.length() - 4);

					// Decrypt the cipher text and output it to a text file
					try (PrintWriter writer = new PrintWriter(CURRENT_DIRECTORY + outputFileName, "UTF-8")) {
						String recoveredText = decrypt(secretKey, iv, password, cipherText);

						writer.print(recoveredText);

						System.out.println("done");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("Invalid read password");
				}
			}
		}
	}

	/**
	 * This method reads the plain text credentials from the passwords list text
	 * file before it is deleted.
	 */
	private static void initialisePlainTextCredentials() {
		credentials.add(new Credential("BS13", "UCL", "Usability"));
		credentials.add(new Credential("FCR", "Rule1", "Rule2"));
		credentials.add(new Credential("SVS", "NotASpy", "RealltyNotASpy"));
		credentials.add(new Credential("TA", "Alan", "AlonzoChurch"));
	}

	/**
	 * This method reads the plain text file from the given path.
	 * 
	 * @param filePath
	 *            Absolute path of a plain text file
	 * @return The content of the plain text file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static String readPlainTextFile(String filePath) throws FileNotFoundException, IOException {
		StringBuilder builder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line = reader.readLine();

			while (line != null) {
				builder.append(line);
				line = reader.readLine();
			}
		}

		return builder.toString();
	}

	/**
	 * This method encrypts the given plain text.
	 * 
	 * @param password
	 *            The encryption password
	 * @param plainText
	 *            A given pain text
	 * @return A CipherResult object that consists of initial vector, secret key
	 *         and cipher text
	 * @throws Exception
	 */
	private static CipherResult encrypt(String password, String plainText) throws Exception {
		return encrypt(password, plainText.getBytes());
	}

	/**
	 * This method encrypts the given plain text.
	 * 
	 * @param password
	 *            The encryption password
	 * @param plainTextBytes
	 *            A byte array of plain text
	 * @return A CipherResult object that consists of initial vector, secret key
	 *         and cipher text
	 * @throws Exception
	 */
	private static CipherResult encrypt(String password, byte[] plainTextBytes) throws Exception {
		// Generate secret key
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(new SecureRandom());
		SecretKey secretKey = keyGenerator.generateKey();

		// Encrypt the plain text
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
		byte[] cipherTextBytes = cipher.doFinal(plainTextBytes);

		return new CipherResult(secretKey, iv, cipherTextBytes);
	}

	/**
	 * This method decrypts the given cipher text.
	 * 
	 * @param encodedSecretKey
	 *            A string of secret key
	 * @param iv
	 *            A string of initial vector
	 * @param password
	 *            Password of the text file
	 * @param cipherText
	 *            Cipher text
	 * @return Recovered text from the cipher text
	 * @throws Exception
	 */
	private static String decrypt(String encodedSecretKey, String iv, String password, String cipherText)
			throws Exception {
		byte[] ivBytes = Base64.getDecoder().decode(iv);
		byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText);

		byte[] secretKeyBytes = Base64.getDecoder().decode(encodedSecretKey);
		SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");

		byte[] decryptedBytes = decrypt(secretKey, ivBytes, password, cipherTextBytes);
		return new String(decryptedBytes, "UTF-8");
	}

	/**
	 * This method decrypts the given cipher text.
	 * 
	 * @param secretKey
	 *            An object of a secret key of type SecretKey
	 * @param ivBytes
	 *            A byte array of initial vector
	 * @param password
	 *            Password of the text file
	 * @param encryptedTextBytes
	 *            A byte array of cipher text
	 * @return A byte array of recovered text from the cipher text
	 * @throws Exception
	 */
	private static byte[] decrypt(SecretKey secretKey, byte[] ivBytes, String password, byte[] encryptedTextBytes)
			throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

		return cipher.doFinal(encryptedTextBytes);
	}

	private static void check() {
		List<String> originalFileNames = credentials.stream().map(x -> x.getFileName()).collect(Collectors.toList());

		List<String> fileNames = new ArrayList<String>();

		// Remove the file separator '\\' at last
		File directory = new File(CURRENT_DIRECTORY.substring(0, CURRENT_DIRECTORY.length() - 1));
		File[] files = directory.listFiles();

		for (File file : files) {
			boolean isDeleted = false;

			// All encrypted files should end with ".enc", i.e. length >= 4
			if (file.getName().length() < 4) {
				isDeleted = file.delete();
			}
			
			if (!isDeleted) {
				// Remove the .enc extension
				String actualFileName = file.getName().substring(0, file.getName().length() - 4);

				// Delete the file if it is not encrypted or in the original
				// folder
				if (!file.getName().endsWith(".enc") || !originalFileNames.contains(actualFileName)) {
					isDeleted = file.delete();
				}

				if (!isDeleted) {
					fileNames.add(actualFileName);
				}
			}
		}

		// List all the missing files
		for (String originalFileName : originalFileNames) {
			if (!fileNames.contains(originalFileName)) {
				System.out.println(originalFileName + " is missing.");
			}
		}
	}

	private static String sign(SecretKey secretKey, String cipherText) throws GeneralSecurityException {
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(secretKey);
		byte[] result = mac.doFinal(cipherText.getBytes());

		return Base64.getEncoder().encodeToString(result);
	}

	private static boolean isCorrectWritePassword(String fileName, String password) {
		Optional<Credential> credential = credentials.stream().filter(x -> x.getFileName().equals(fileName))
				.findFirst();

		if (credential.isPresent()) {
			return credential.get().getWritePassword().equals(password);
		}

		return false;
	}

	private static boolean isCorrectReadPassword(String fileName, String password) {
		// Remove the .enc extension
		String actualFileName = fileName.substring(0, fileName.length() - 4);

		Optional<Credential> credential = credentials.stream().filter(x -> x.getFileName().equals(actualFileName))
				.findFirst();

		if (credential.isPresent()) {
			return credential.get().getReadPassword().equals(password);
		}

		return false;
	}
}