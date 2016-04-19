package file.protection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Protect {
	private static final String CURRENT_DIRECTORY = System.getProperty("user.dir") + "\\src\\file\\protection\\Q1\\";
	private static final String PASSWORDS_LIST = "passwords_list";
	private static final String ENCRYPTED_PASSWORDS_LIST = "encrypted_passwords_list.txt";

	private static List<Credential> credentials = new ArrayList<Credential>();

	public static void main(String[] args) {
		String mode = args[2];
		String fileName = args[3];
		String password = args[4];

		initialisePlainTextCredentials();

		if (mode.equals("-e")) {
			try {
				// Encrypt the plain text file
				String plainText = readFile(CURRENT_DIRECTORY + fileName);
				CipherResult result = encrypt(password, plainText);

				// Write the cipher text to a file with extension ".enc"
				try (PrintWriter writer = new PrintWriter(CURRENT_DIRECTORY + fileName + ".enc", "UTF-8")) {
					writer.println(result.getCipherText());
					writer.println();
					String signature = sign(result.getSecretKey(), result.getCipherText());
					writer.print(signature);
				}

				// Write the encrypted AES key to the text file
				try (PrintWriter writer = new PrintWriter(CURRENT_DIRECTORY + ENCRYPTED_PASSWORDS_LIST, "UTF-8")) {
					writer.println("#file\t#secret key\t\t\t#initial vector");
					writer.print(fileName + "\t" + result.getSecretKeyString() + "\t" + result.getIv());
				}

				System.out.println("done");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (mode.equals("-d")) {
			String iv = null;
			String secretKey = null;
			String cipherText = null;

			// Read secret key and iv
			try (BufferedReader reader = new BufferedReader(
					new FileReader(CURRENT_DIRECTORY + ENCRYPTED_PASSWORDS_LIST))) {
				// The first line is heading, which is ignored
				reader.readLine();

				String line = reader.readLine();
				String[] columns = line.split("\t");

				secretKey = columns[1];
				iv = columns[2];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Read cipher text
			try (BufferedReader reader = new BufferedReader(new FileReader(CURRENT_DIRECTORY + fileName))) {
				cipherText = reader.readLine();
				reader.readLine();
				System.out.println("Signature: " + reader.readLine());
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
		}
	}

	/**
	 * This method reads the plain text credentials from the passwords list text
	 * file before it is deleted.
	 */
	private static void initialisePlainTextCredentials() {
		try (BufferedReader reader = new BufferedReader(new FileReader(CURRENT_DIRECTORY + PASSWORDS_LIST))) {
			reader.readLine(); // the first line is heading, which is ignored

			String line = reader.readLine();
			while (line != null) {
				String[] columns = line.split("\t");
				credentials.add(new Credential(columns[0], columns[1], columns[3]));
				line = reader.readLine();
			}
		} catch (IOException e) {
			// Do nothing if the file is not found, as it has been read before
			// and has been deleted
		}
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
	private static String readFile(String filePath) throws FileNotFoundException, IOException {
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

	private static String sign(SecretKey secretKey, String cipherText) throws GeneralSecurityException {
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(secretKey);
		byte[] result = mac.doFinal(cipherText.getBytes());

		return Base64.getEncoder().encodeToString(result);
	}
}