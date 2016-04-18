package file.protection;

import java.util.Base64;

import javax.crypto.SecretKey;

public class CipherResult {
	private SecretKey secretKey;
	private byte[] iv;
	private byte[] cipherText;

	public CipherResult(SecretKey secretKey, byte[] iv, byte[] cipherText) {
		this.secretKey = secretKey;
		this.iv = iv;
		this.cipherText = cipherText;
	}

	public String getSecretKey() {
		return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
	}

	public String getIv() {
		return Base64.getEncoder().encodeToString(this.iv);
	}
	
	public String getCipherText() {
		return Base64.getEncoder().encodeToString(this.cipherText);
	}
}