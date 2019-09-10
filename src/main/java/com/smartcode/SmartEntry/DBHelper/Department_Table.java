/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartcode.SmartEntry.DBHelper;

import com.smartcode.SmartEntry.Security.SecureDetails;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Department_Table {

    static BufferedReader reader;
    static String line;
    static String response;
    private static HttpURLConnection conn;
    static int resCode;
    static String json;
    static String name, program, message;
    static String amount;


    public static void main(String[] args) {
       // System.out.println(verifyCscode("PC006", "1"));

    }

    public String isUserAuthentic(String username, String password) throws NoSuchAlgorithmException, MalformedURLException, ProtocolException, IOException {
        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/Department_table.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    JSONArray protocols = new JSONArray(response);
                    for (int i = 0; i < protocols.length(); i++) {
                       JSONObject protocol =  protocols.getJSONObject(i);
                        String user = protocol.getString("user_name");
                        String pass = protocol.getString("password");
                        String db = SecureDetails.SHA256of(password);
                        if (username.equals(user) && SecureDetails.SHA256of(password).matches(pass)) {

                            return "User is Authentic";
                        }

                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "User is not Authentic";
    }
}
