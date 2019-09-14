/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartcode.SmartEntry.DBHelper;

import static com.smartcode.SmartEntry.DBHelper.Visitor_Table.response;
import com.smartcode.SmartEntry.Notification.SmartNotify;
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
import org.json.JSONArray;
import org.json.JSONObject;

public class Student_Table {

    static BufferedReader reader;
    static String line;
    static String response;
    private static HttpURLConnection conn;
    int resCode;
    String name, program, message;
    String amount;
    JSONObject json = new JSONObject();
    public int studentPosition;

    public static void main(String[] args) {
        // System.out.println(verifyCscode("PC006", "1"));

    }

    public HashMap search(String student_id, String status) {
        String parent_mobile="", parent_email="";
          String names = "";
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
                            parent_mobile = stdDetails.getString("parent_mobile");
                            parent_email = stdDetails.getString("parent_email");
                            names = stdDetails.getString("first_name")+"-"+stdDetails.getString("last_name")+"-"+stdDetails.getString("matric_no")+"-"+stdDetails.getString("parent_email");
                            return UpdateStudentStatus(studentPosition, status, student_id,parent_mobile, parent_email,names);
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
        return UpdateStudentStatus(studentPosition, status, student_id, parent_mobile, parent_email,names);
    }

    public String isUserAuthentic(String username, String password) throws NoSuchAlgorithmException, MalformedURLException, ProtocolException, IOException {
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

    public HashMap UpdateStudentStatus(int i, String status, String stud,String parent_mobile,String parent_email,String names) {
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
                String name [] = names.split("-");
                if (status.equals("0")) {
                    // Notify the parent
                    SmartNotify sn = new SmartNotify ();
                    String message = "Dear Guardian, \nyour ward "+ name[0].toUpperCase() +" "+name[1].toUpperCase()+", with Matriculation number "+name[2]+" got SIGNED-OUT of the school at exactly " + json.getString("Time")+". \n Notificaion is for you to keep track of your ward's  movement in and out of the school.\n\nBOWEN UNIVERSITY, \nIWO  OSUN STATE. ";
                    sn.sendEmail( name[3],"EXIT NOTIFICATION",message);
                } else {
                    // Notify the parent
                    SmartNotify sn = new SmartNotify ();
                    String message = "Dear Guardian, \nyour ward "+name[0].toUpperCase() +" "+name[1].toUpperCase()+", with Matriculation number "+name[2]+" got SIGNED into the school at exactly " + json.getString("Time")+". \n Notificaion is for you to keep track of your ward's  movement in and out of the school.\n\nBOWEN UNIVERSITY, \nIWO  OSUN STATE. ";
                    sn.sendEmail( name[3],"ENTRY NOTIFICATION",message);

                }
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

    public String allDeptStudentWith(String dept_id) {
        String output = "{";
        String DeptStudent = "\"student_list\":[";
        int count = 0;
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
                        String dept = stdDetails.getString("Department");
                        if (dept.equals(dept_id)) {

                            if (count != 0) {
                                DeptStudent = DeptStudent + ",\n";
                            }
                            count++;

                            DeptStudent = DeptStudent + studentdetails.getJSONObject(i).toString();

                        }

                    }
                    DeptStudent = DeptStudent + "]\n";
                    output = output + DeptStudent;
                    output = output + ",\n";
                    output = output + "\"count\":\"" + count + "\"\n}";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return output;
    }

    public String allFactStudentWith(String fac_id) {
        String output = "{";
        String DeptStudent = "\"student_list\":[";
        int count = 0;
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
                        String fac = stdDetails.getString("Faculty");
                        if (fac.equals(fac_id)) {

                            if (count != 0) {
                                DeptStudent = DeptStudent + ",\n";
                            }
                            count++;

                            DeptStudent = DeptStudent + studentdetails.getJSONObject(i).toString();

                        }

                    }
                    DeptStudent = DeptStudent + "]\n";
                    output = output + DeptStudent;
                    output = output + ",\n";
                    output = output + "\"count\":\"" + count + "\"\n}";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return output;
    }

    public String allstudent() {
        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/student_details.json");
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
                    return response;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
        sb.append(" \"message\" ").append(":").append("\"Service Timed out....Kindly Re-try\"\n");
        sb.append("}]");

        return sb.toString();
    }

    public HashMap registerStudent(HashMap<String, String> req) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        // JSON DATA TO POST
        json.put("Department", req.get("Department"));
        json.put("Faculty", req.get("Faculty"));
        json.put("Status", "present");
        json.put("first_name", req.get("first_name"));
        json.put("last_name", req.get("last_name"));
        json.put("matric_no", req.get("matric_no"));
        json.put("Time", df.format(new Date()));
        json.put("sex", req.get("sex"));
        json.put("parent_mobile", req.get("parent_mobile"));
        json.put("parent_email", req.get("parent_email"));
         json.put("student_level", req.get("student_level"));
        int totalStudent = getTotalStudent();

        try {
            URL url = new URL("https://smartentry-e8e2c.firebaseio.com/student_details/" + totalStudent + ".json");
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
                // Notify the parent
                SmartNotify sn = new SmartNotify ();
                String message = "Dear Guardian, \nyour ward "+ json.getString("first_name").toUpperCase() +" "+json.getString("last_name").toUpperCase()+", with Matriculation number "+json.getString("matric_no")+" got SIGNED into the school at exactly " + json.getString("Time")+". \n Notificaion is for you to keep track of your ward's  movement in and out of the school.\n\nBOWEN UNIVERSITY, \nIWO  OSUN STATE. ";
                sn.sendEmail(json.getString("parent_email"),"ENTRY NOTIFICATION",message);
                HashMap<String, String> resp = new HashMap<>();
                resp.put("Student_name", req.get("first_name"));
                resp.put("success", "true");
                resp.put("status", "present");
                resp.put("message", "Registration Successful");
                resp.put("time", json.getString("Time"));
                in.close();
                conn.disconnect();
                return resp;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        HashMap<String, String> resp = new HashMap<>();
        resp.put("success", "false");
        resp.put("message", "Registration failed");
        return resp;
    }

    public int getTotalStudent() {
        int count = 0;
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
                    JSONArray students = new JSONArray(response);

                    for (int i = 0; i < students.length(); i++) {
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

    public int getTotalStudents() {
        return getTotalStudent();
    }

    public int getTotalStudentsPresent() {
        int count = 0;
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
                    JSONArray students = new JSONArray(response);

                    for (int i = 0; i < students.length(); i++) {
                        JSONObject student = students.getJSONObject(i);
                        String status = student.getString("Status");

                        if (status.equals("present")) {
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

    public int getTotalStudentsAbsent() {
        int count = 0;
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
                    JSONArray students = new JSONArray(response);

                    for (int i = 0; i < students.length(); i++) {
                        JSONObject student = students.getJSONObject(i);
                        String status = student.getString("Status");

                        if (status.equals("absent")) {
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
