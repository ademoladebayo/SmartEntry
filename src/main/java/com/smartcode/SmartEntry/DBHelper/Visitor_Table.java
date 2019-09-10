/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartcode.SmartEntry.DBHelper;

import static com.smartcode.SmartEntry.DBHelper.Student_Table.response;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

public class Visitor_Table {

    static BufferedReader reader;
    static String line;
    static String response;
    private static HttpURLConnection conn;
    int resCode;
    String name, program, message;
    String amount;
    JSONObject json = new JSONObject();
    public int studentPosition;
     public int visitorPosition;

    public static void main(String[] args) {
        // System.out.println(verifyCscode("PC006", "1"));

    }

    public HashMap search(String student_id, String status) {
        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/student_details.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    // JSONObject details = new JSONObject(response);
                    JSONArray studentdetails = new JSONArray(response);
                    System.out.println("ARRAY 1 :" + studentdetails);
                    for (int i = 0; i < studentdetails.length(); i++) {
                        JSONObject stdDetails = studentdetails.getJSONObject(i);
                        System.out.println("PROTOCOL 1 :" + stdDetails);
                        String student = stdDetails.getString("matric_no");
                        if (student.equals(student_id)) {
                            studentPosition = i;

                            return UpdateStudentStatus(studentPosition, status, student_id);
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
        return UpdateStudentStatus(studentPosition, status, student_id);
    }

    public String saveReqest(String username, String password) throws NoSuchAlgorithmException, MalformedURLException, ProtocolException, IOException {
        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/student_details.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    // JSONObject details = new JSONObject(response);
                    JSONArray protocols = new JSONArray(response);
                    System.out.println("ARRAY 1 :" + protocols);
                    for (int i = 0; i < protocols.length(); i++) {
                        JSONObject protocol = protocols.getJSONObject(i);
                        System.out.println("PROTOCOL 1 :" + protocol);
                        String staffID = protocol.getString("staff_Id");
                        String pass = protocol.getString("password");
                        String db = SecureDetails.SHA256of(password);
                        System.out.println("DB PASS :" + pass);
                        System.out.println("entered PASS :" + db);

                        if (username.equals(staffID) && SecureDetails.SHA256of(password).matches(pass)) {

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

    public HashMap UpdateStudentStatus(int i, String status, String stud) {
        String sta = status.equals("0") ? "absent" : "present";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        json.put("Time", df.format(new Date()));
        json.put("Status", sta);

        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/student_details/" + i + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            System.out.println(conn.getResponseCode());
            // read the response
            if (conn.getResponseCode() > 299) {
                InputStream in = new BufferedInputStream(conn.getErrorStream());
                String result = IOUtils.toString(in, "UTF-8");

                in.close();

            } else {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = IOUtils.toString(in, "UTF-8");
                JSONObject myResponse = new JSONObject(result);
                String time = myResponse.getString("Time");
                String st = myResponse.getString("Status");
                HashMap<String, String> resp = new HashMap<>();
                resp.put("student_id", stud);
                resp.put("success", "true");
                resp.put("message", "Status updated");
                resp.put("time", time);
                resp.put("status", sta);
                in.close();
                conn.disconnect();
                return resp;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        HashMap<String, String> resp = new HashMap<>();
        resp.put("success", "false");
        resp.put("message", "update failed");
        return resp;
    }

    public HashMap saveRequest(HashMap<String, String> req) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
       
        // JSON DATA TO POST
        json.put("Reason_for_meet", req.get("Reason_for_meet"));
        json.put("Who_to_meet", req.get("Who_to_meet"));
        json.put("address", req.get("address"));
        json.put("firstName", req.get("firstName"));
        json.put("lastName", req.get("lastName"));
        json.put("email", req.get("email"));
        json.put("mobile", req.get("mobile"));
        json.put("time_in", df.format(new Date()));
        json.put("time_out", " Not yet");
        json.put("id", req.get("mobile"));
        int totalVisitor = getTotalVisitor();

        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/visitor_table/"+ totalVisitor+".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            System.out.println(conn.getResponseCode());
            // read the response
            if (conn.getResponseCode() > 299) {
                InputStream in = new BufferedInputStream(conn.getErrorStream());
                String result = IOUtils.toString(in, "UTF-8");

                in.close();

            } else {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = IOUtils.toString(in, "UTF-8");
                JSONObject myResponse = new JSONObject(result);
                
                HashMap<String, String> resp = new HashMap<>();
                resp.put("visitor_name", req.get("firstName"));
                resp.put("id", req.get("mobile"));
                resp.put("success", "true");
                resp.put("message", "Request Saved");
                resp.put("time_in", json.getString("time_in"));
                in.close();
                conn.disconnect();
                return resp;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        HashMap<String, String> resp = new HashMap<>();
        resp.put("success", "false");
        resp.put("message", "Request not saved");
        return resp;
    }

    public int getTotalVisitor() {
        int count=0;
         try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/visitor_table.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    // JSONObject details = new JSONObject(response);
                    JSONArray visitor = new JSONArray(response);
                   
                    for (int i = 0; i < visitor.length(); i++) {
                      count++;
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
     
        
        return count;
    }

    public String allVisitors() {
        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/visitor_table.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
             System.out.println(resCode);
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    System.out.println(response);
                return    response;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StringBuilder sb= new StringBuilder();
        sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
        sb.append(" \"message\" ").append(":").append("\"Service Timed out....Kindly Re-try\"\n");
        sb.append("}]");
      
        return sb.toString();
    }

    public HashMap signOutVisitor(int p,String visitor_id) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        json.put("time_out", df.format(new Date()));
      

        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/visitor_table/" + p + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            System.out.println(conn.getResponseCode());
            // read the response
            if (conn.getResponseCode() > 299) {
                InputStream in = new BufferedInputStream(conn.getErrorStream());
                String result = IOUtils.toString(in, "UTF-8");

                in.close();

            } else {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = IOUtils.toString(in, "UTF-8");
                JSONObject myResponse = new JSONObject(result);
               
                HashMap<String, String> resp = new HashMap<>();
                resp.put("visitor_id", visitor_id);
                resp.put("success", "true");
                resp.put("message", "Visitor has been signed out");
                resp.put("time", json.getString("time_out"));
                in.close();
                conn.disconnect();
                return resp;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        HashMap<String, String> resp = new HashMap<>();
        resp.put("success", "false");
        resp.put("message", "signout failed");
        return resp;
    }

    public HashMap searchVisitor(String visitor_id) {
         try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/visitor_table.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    // JSONObject details = new JSONObject(response);
                    JSONArray visitors = new JSONArray(response);
                    System.out.println("ARRAY 1 :" + visitors);
                    for (int i = 0; i < visitors.length(); i++) {
                        JSONObject visitor = visitors.getJSONObject(i);
                        System.out.println("PROTOCOL 1 :" + visitor);
                        String id = visitor.getString("mobile");
                        if (id.equals(visitor_id)) {
                            visitorPosition  = i;
                            return signOutVisitor(visitorPosition,visitor_id);
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
        return signOutVisitor(studentPosition,visitor_id);
    }

    public int getTotalVisitors() {
      return  getTotalVisitor();
    }

    public int getTotalVisitorsYetToSignOut() {
        int count=0;
         try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/visitor_table.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    // JSONObject details = new JSONObject(response);
                    JSONArray visitors = new JSONArray(response);
                   
                    for (int i = 0; i < visitors.length(); i++) {
                        JSONObject visitor = visitors.getJSONObject(i);
                        String time_out = visitor.getString("time_out");
                        
                        if(time_out.equals("Not yet")){
                            System.out.println(time_out);
                         count++;
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
     
        
        return count;
    }

    public int getTotalVisitorsSignedOut() {
        int count=0;
         try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/visitor_table.json");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            resCode = conn.getResponseCode();
            if (resCode == 200) {
                try {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in);
                    // JSONObject details = new JSONObject(response);
                    JSONArray visitors = new JSONArray(response);
                   
                    for (int i = 0; i < visitors.length(); i++) {
                        JSONObject visitor = visitors.getJSONObject(i);
                        String time_out = visitor.getString("time_out");
                        
                        if(!time_out.equals("Not yet")){
                         count++;
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
     
        
        return count;
    }

}
