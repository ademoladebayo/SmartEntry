/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartcode.SmartEntry.Security;

/**
 *
 * @author ADEBAYO ADEMOLA
 */
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecureDetails {

    public static void main(String[] args) throws NoSuchAlgorithmException {

        /*  String password = "citadmin";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

		// bytes to hex
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }*/
        System.out.println(SHA256of("cit").matches("97d232947baf0cadfde266570ccf31038662fcef6636e57979e10f1f0451ec40"));
       System.out.println(SHA256of("cit"));
      /* SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(df.format(new Date()));*/

    }

    public static String SHA256of(String string) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = md.digest(string.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    //fe3811fe21af748f53a05a169da84013d11253a53e3ef80355d20419fd89042e

}
