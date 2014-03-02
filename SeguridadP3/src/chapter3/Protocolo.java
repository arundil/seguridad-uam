package chapter3;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import chapter4.Utils;

public class Protocolo {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		 	SecureRandom	random = new SecureRandom();
	        Cipher	         cipher_rsa = Cipher.getInstance("RSA/None/NoPadding", "BC");
	        Cipher		     cipher_sim = Cipher.getInstance("AES/CTR/NoPadding","BC");
	        MessageDigest   hash = MessageDigest.getInstance("SHA1", "BC");
	        String          mensaje = "Transfer 0000100 to AC 1234-5678";
	        IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
	        Key             key_sim = Utils.createKeyForAES(256, random);
	        
	        
	        
	        System.out.println("Mensaje Incicial :\n " + Utils.toHex(Utils.toByteArray(mensaje)) + " bytes: " + Utils.toByteArray(mensaje).length);
	        
	        
	        //Clave Publica y Privada de A
	        
	        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
	        
	        generator.initialize(256, random);

	        KeyPair          pair = generator.generateKeyPair();
	        Key              pubKey = pair.getPublic();
	        Key              privKey = pair.getPrivate();

	        
	        //Configurar el cifrado para el hash
	        cipher_rsa.init(Cipher.ENCRYPT_MODE,privKey);
	        
	        //Mensaje cifrado = Ciframos el HASH
	        
	        byte [] hash_original = hash.digest();
	        byte[] cipherText_HASH = new byte[cipher_rsa.getOutputSize(hash.getDigestLength())];
	        cipherText_HASH = cipher_rsa.doFinal(hash_original);
	        
	        System.out.println("ESTADO INICIAL");
	        System.out.println("Mensaje Incicial :\n " + Utils.toHex(Utils.toByteArray(mensaje)) + " bytes: " + Utils.toByteArray(mensaje).length);
	        System.out.println("HASH : " + Utils.toHex(hash_original));
	        System.out.println("HASH_Cifrado : " + Utils.toHex(cipherText_HASH));
	        
	        
	       //Una vez cifrado el HASH se cifra el mensaje
	        
	        byte[] mensaje_cifrado =chapter3.Utils.toByteArray(mensaje);
	       
	        cipher_sim.init(Cipher.ENCRYPT_MODE, key_sim, ivSpec);
	       
	        byte[] cipherText_sim = new byte[cipher_sim.getOutputSize(mensaje_cifrado.length + cipherText_HASH.length)];
	        
	        int ctLength_sim = cipher_sim.update(mensaje_cifrado, 0, mensaje_cifrado.length, cipherText_sim, 0);
	        
	        ctLength_sim += cipher_sim.doFinal(cipherText_HASH, 0, cipherText_HASH.length, cipherText_sim, ctLength_sim);
	        
	        
	       
	        //  CANAL INSEGURO
	        System.out.println("=========================================");
	        System.out.println("ENTRANDO EN CANAL INSEGURO");
	        //System.out.println("HASH Cifrado");
	        //System.out.println(Utils.toHex(mensaje_cifrado));
	        System.out.println("Mensaje Cifrado:\n " + Utils.toHex(cipherText_sim) + " bytes: " + ctLength_sim);
	        System.out.println("SALIENDO DE CANAL INSEGURO");
	        
	        System.out.println("=========================================");
	        //  CANAL SEGURO
	        
	        //desencriptado simetrico
	        	        
	        cipher_sim.init(Cipher.DECRYPT_MODE, key_sim,ivSpec);

	        byte[] plainText_sim = cipher_sim.doFinal(cipherText_sim,0,ctLength_sim);
	        
	        int    messageLength = plainText_sim.length - cipherText_HASH.length;
	        
	        
	        byte[] messageHash = new byte[messageLength];
	        
	        for (int i = 0 ; i < messageLength; i++)
	        {
	        	messageHash[i] = plainText_sim[messageLength+i];
	        }
	        
	        System.arraycopy(plainText_sim, messageLength, messageHash, 0, messageHash.length);
	        
	        
	        //mensaje simetrico desencriptado
	        System.out.println("DESENCRIPTANDO EL MENSAJE");
	        System.out.println("Texto Plano desencriptado : " + Utils.toHex(plainText_sim) + " bytes: " + plainText_sim.length);
	        System.out.println("HASH para desencriptar : " + Utils.toHex(messageHash) + " bytes: " + messageHash.length);
	        

	        //Ahora desencriptamos el hash
	        
	        cipher_rsa.init(Cipher.DECRYPT_MODE, pubKey);

	        byte[] plainText_hash = cipher_rsa.doFinal(messageHash);
	        
	        System.out.println("HASH desencriptado :" + Utils.toHex(plainText_hash) + " bytes: " + plainText_hash.length);
	        
	        System.out.println("¿ES EL ARCHIVO ORIGINAL?" + MessageDigest.isEqual(hash_original, plainText_hash));
  
	}

}
