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

	public SecretKey getSecretKey() {
		return this.secretKey;
	}
	
	public String getSecretKeyString() {
		return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
	}

	public byte[] getIv() {
		return this.iv;
	}
	
	public String getIvString() {
		return Base64.getEncoder().encodeToString(this.iv);
	}
	
	public byte[] getCipherText() {
		return this.cipherText;
	}
	
	public String getCipherTextString() {
		return Base64.getEncoder().encodeToString(this.cipherText);
	}
}