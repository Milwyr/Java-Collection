package file.protection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Protect {
	private static final String TASK1_DIRECTORY = System.getProperty("user.dir") + "\\src\\file\\protection\\Q1\\";
	private static final String TASK2_DIRECTORY = System.getProperty("user.dir") + "\\src\\file\\protection\\Q2\\";
	private static final String SIGNATURE_DOCUMENT_NAME = "digital signature.enc";
	private static final String PUBLIC_KEY_DOCUMENT_NAME = "public key.enc";

	private static List<Credential> credentials = new ArrayList<Credential>();
	private static List<Permission> permissions = new ArrayList<Permission>();

	public static void main(String[] args) {
		String mode = args[2];
		initialiseCredentials();
		initialiseRolePermissions();

		if (mode.equals("-c")) {
			check(TASK1_DIRECTORY);
		} else {
			String fileName = args[3];
			String password = args[4];

			if (mode.equals("-e")) {
				if (isCorrectWritePasswordForTask1(fileName, password)) {
					encryptExtract(TASK1_DIRECTORY, fileName, password);
				} else {
					if (isCorrectWritePasswordForTask2(fileName, password)) {
						encryptExtract(TASK2_DIRECTORY, fileName, password);
					} else {
						System.out.println("Invalid write password or wrong role-based access control.");
					}
				}
			} else if (mode.equals("-d")) {
				if (isCorrectReadPasswordForTask1(fileName, password)) {
					decryptExtract(TASK1_DIRECTORY, fileName, password);
				} else {
					if (isCorrectReadPasswordForTask2(fileName, password)) {
						decryptExtract(TASK2_DIRECTORY, fileName, password);
					} else {
						System.out.println("Invalid write password or wrong role-based access control.");
					}
				}
			}
		}
	}

	/**
	 * This method adds hard coded credentials.
	 */
	private static void initialiseCredentials() {
		credentials.add(new Credential("BS13", "UCL", "Usability"));
		credentials.add(new Credential("FCR", "Rule1", "Rule2"));
		credentials.add(new Credential("SVS", "NotASpy", "RealltyNotASpy"));
		credentials.add(new Credential("TA", "Alan", "AlonzoChurch"));
	}

	/**
	 * This method adds hard coded permission for each role.
	 */
	private static void initialiseRolePermissions() {
		String[] readFileArrayForManager = { "BS13.enc", "FCR.enc", "SVS.enc", "TA.enc" };
		List<String> readFiles = Arrays.asList(readFileArrayForManager);
		permissions.add(new Permission("manager", "universal", readFiles, new ArrayList<String>()));

		String[] readFileArrayForRookie = { "BS13.enc", "SVS.enc" };
		String[] writeFileArrayForRookie = { "SVS" };
		permissions.add(new Permission("rookie", "kiddo", Arrays.asList(readFileArrayForRookie),
				Arrays.asList(writeFileArrayForRookie)));

		String[] readFileArrayForAnalyst = { "BS13.enc", "SVS.enc", "TA.enc" };
		String[] writeFileArrayForAnalyst = { "BS13", "SVS" };
		permissions.add(new Permission("analyst", "hunter2", Arrays.asList(readFileArrayForAnalyst),
				Arrays.asList(writeFileArrayForAnalyst)));
	}

	/**
	 * Extract the code to encrypt the file from the main method.
	 * 
	 * @param directory
	 * @param fileName
	 * @param password
	 */
	private static void encryptExtract(String directory, String fileName, String password) {
		try {
			// Encrypt the plain text file
			String plainText = readPlainTextFile(directory + fileName);
			CipherResult result = encrypt(password, plainText);

			// Encrypt the file with extension ".enc"
			try (PrintWriter writer = new PrintWriter(directory + fileName + ".enc", "UTF-8")) {
				// Write the cipher text to a file
				writer.println(result.getCipherText());
				writer.println();

				// Save the AES key and initial vector in the file
				writer.println(result.getSecretKeyString() + "\t" + result.getIv());
				writer.println();
			}

			// Delete the plain text file
			File plainTextFile = new File(directory + fileName);
			if (plainTextFile.exists()) {
				plainTextFile.delete();
			}

			// Save the digital signature and public key in two
			// different documents
			signDocument(directory, fileName + ".enc");

			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extract the code to decrypt the file from the main method.
	 * 
	 * @param directory
	 * @param fileName
	 * @param password
	 */
	private static void decryptExtract(String directory, String fileName, String password) {
		String iv = null;
		String secretKey = null;
		String cipherText = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(directory + fileName))) {
			// Read cipher text
			cipherText = reader.readLine();
			reader.readLine();

			// Read secret key and iv
			String line = reader.readLine();
			String[] columns = line.split("\t");
			secretKey = columns[0];
			iv = columns[1];
			reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean signatureIsValid = verifySignature(directory, fileName);
		if (!signatureIsValid) {
			System.out.println("Signature is wrong!");
		} else {
			// Remove extension .enc
			String outputFileName = fileName.substring(0, fileName.length() - 4);

			// Decrypt the cipher text and output it to a text file
			try (PrintWriter writer = new PrintWriter(directory + outputFileName, "UTF-8")) {
				String recoveredText = decrypt(secretKey, iv, password, cipherText);

				writer.print(recoveredText);

				File encryptedTextFile = new File(directory + fileName);
				if (encryptedTextFile.exists()) {
					encryptedTextFile.delete();
				}

				System.out.println("done");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	private static CipherResult encrypt(String password, String plainText) throws GeneralSecurityException {
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
	private static CipherResult encrypt(String password, byte[] plainTextBytes) throws GeneralSecurityException {
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

	private static void check(String directoryName) {
		List<String> originalFileNames = credentials.stream().map(x -> x.getFileName()).collect(Collectors.toList());

		List<String> fileNames = new ArrayList<String>();

		// Remove the file separator '\\' at last
		File directory = new File(directoryName.substring(0, directoryName.length() - 1));
		File[] files = directory.listFiles();

		for (File file : files) {
			boolean isDeleted = false;

			// All encrypted files should end with ".enc", i.e. length >= 4
			if (file.getName().length() < 4) {
				isDeleted = file.delete();
			}

			if (!isDeleted) {
				// Remove the .enc extension (e.g. BS13.enc becomes BS13)
				String actualFileName = file.getName().substring(0, file.getName().length() - 4);

				// Delete the file if it is not encrypted or in the original
				// folder, and it is not signature or public key file
				if (!file.getName().endsWith(".enc") || !originalFileNames.contains(actualFileName)) {
					if (!file.getPath().equals(directoryName + SIGNATURE_DOCUMENT_NAME)
							&& !file.getPath().equals(directoryName + PUBLIC_KEY_DOCUMENT_NAME)) {
						isDeleted = file.delete();
					}
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
		System.out.println("done");
	}

	/**
	 * This method signs the document with the given document name. A generated
	 * digital signature is generated and stored in a file, while the public key
	 * is stored in another file.
	 * 
	 * @param documentName
	 *            Name of the document to be signed (ends with .enc)
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static void signDocument(String directoryName, String documentName)
			throws GeneralSecurityException, IOException {
		// Create a key pair generator
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyPairGenerator.initialize(1024, secureRandom);

		// Generate private and public key
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		// Initialise the signature object with a generated private key
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
		dsa.initSign(privateKey);

		// Supply the signature object the data to be signed
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(directoryName + documentName))) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = bufferedInputStream.read(buffer)) >= 0) {
				dsa.update(buffer, 0, length);
			}
		}

		// Save the digital signature in a file
		Map<String, String> signatureMap = readKeyPairValue(directoryName + SIGNATURE_DOCUMENT_NAME);
		try (PrintWriter writer = new PrintWriter(directoryName + SIGNATURE_DOCUMENT_NAME, "UTF-8")) {
			for (Entry<String, String> entry : signatureMap.entrySet()) {
				if (documentName.equals(entry.getKey())) {
					// Save the newly generated signature
					writer.println(entry.getKey() + "\t" + Base64.getEncoder().encodeToString(dsa.sign()));
				} else {
					// Save the original signature
					writer.println(entry.getKey() + "\t" + entry.getValue());
				}
			}

			// Save the signature if it has not been saved before
			if (signatureMap.size() == 0 || !signatureMap.keySet().contains(documentName)) {
				writer.println(documentName + "\t" + Base64.getEncoder().encodeToString(dsa.sign()));
			}
		}

		// Save the public key in another file
		Map<String, String> publicKeyMap = readKeyPairValue(directoryName + PUBLIC_KEY_DOCUMENT_NAME);
		try (PrintWriter writer = new PrintWriter(directoryName + PUBLIC_KEY_DOCUMENT_NAME, "UTF-8")) {
			for (Entry<String, String> entry : publicKeyMap.entrySet()) {
				if (documentName.equals(entry.getKey())) {
					// Save the newly generated public key
					writer.println(entry.getKey() + "\t" + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
				} else {
					// Save the original signature
					writer.println(entry.getKey() + "\t" + entry.getValue());
				}
			}

			// Save the public key if it has not been saved before
			if (publicKeyMap.size() == 0 || !publicKeyMap.keySet().contains(documentName)) {
				writer.println(documentName + "\t" + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
			}
		}
	}

	/**
	 * This method verifies the signature.
	 * 
	 * @param documentName
	 *            Name of the document to verify (ends with .enc)
	 * @return True if the signature is valid
	 */
	private static boolean verifySignature(String directoryName, String documentName) {
		// Read signature
		String publicKeyString = readKeyPairValue(directoryName + PUBLIC_KEY_DOCUMENT_NAME).get(documentName);
		byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

		try {
			// Create a public key
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			// Read the signature from the file created when encrypting the
			// plain text
			String signatureToVerifyString = readKeyPairValue(directoryName + SIGNATURE_DOCUMENT_NAME)
					.get(documentName);
			byte[] signatureToVerify = Base64.getDecoder().decode(signatureToVerifyString);

			// Generate a signature with the public key
			Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
			sig.initVerify(publicKey);

			// Supply the signature object the data to be signed
			try (BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream(directoryName + documentName))) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = bufferedInputStream.read(buffer)) >= 0) {
					sig.update(buffer, 0, length);
				}
			}

			return sig.verify(signatureToVerify);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Key is file name, and value is the signature.
	 * 
	 * @param filePath
	 *            Path of the file to read from
	 * @return A key value pair map
	 */
	private static Map<String, String> readKeyPairValue(String filePath) {
		Map<String, String> signatures = new TreeMap<String, String>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// [0] is the file name, [1] is the signature
				String[] columns = line.split("\t");
				signatures.put(columns[0], columns[1]);
			}
			return signatures;
		} catch (IOException e) {
			return new TreeMap<String, String>();
		}
	}

	private static boolean isCorrectWritePasswordForTask1(String fileName, String password) {
		Optional<Credential> credential = credentials.stream().filter(x -> x.getFileName().equals(fileName))
				.findFirst();

		if (credential.isPresent()) {
			return credential.get().getWritePassword().equals(password);
		}

		return false;
	}

	private static boolean isCorrectWritePasswordForTask2(String fileName, String password) {
		Optional<Permission> permission = permissions.stream().filter(x -> x.getPassword().equals(password))
				.findFirst();

		if (permission.isPresent()) {
			return permission.get().getWriteFiles().contains(fileName);
		}

		return false;
	}

	private static boolean isCorrectReadPasswordForTask1(String fileName, String password) {
		// Remove the .enc extension
		String actualFileName = fileName.substring(0, fileName.length() - 4);

		Optional<Credential> credential = credentials.stream().filter(x -> x.getFileName().equals(actualFileName))
				.findFirst();

		if (credential.isPresent()) {
			return credential.get().getReadPassword().equals(password);
		}

		return false;
	}

	private static boolean isCorrectReadPasswordForTask2(String fileName, String password) {
		Optional<Permission> permission = permissions.stream().filter(x -> x.getPassword().equals(password))
				.findFirst();

		if (permission.isPresent()) {
			return permission.get().getReadFiles().contains(fileName);
		}

		return false;
	}

}