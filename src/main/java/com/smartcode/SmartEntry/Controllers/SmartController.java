/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartcode.SmartEntry.Controllers;

import com.smartcode.SmartEntry.DBHelper.Department_Table;
import com.smartcode.SmartEntry.DBHelper.Faculty_Table;
import com.smartcode.SmartEntry.DBHelper.Protocol_Table;
import com.smartcode.SmartEntry.DBHelper.Student_Table;
import com.smartcode.SmartEntry.DBHelper.Visitor_Table;
import com.smartcode.SmartEntry.Security.SecureDetails;
import java.io.IOException;
import java.net.ProtocolException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADEBAYO ADEMOLA
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/smartentry")
public class SmartController {

    /*   @GetMapping(value="/signin/{name}" )
    public HashMap get(@PathVariable(value="name") String name) {
        HashMap<String, String> req = new HashMap<>();
        req.put(" name", name);
        req.put(" success", "true");
        req.put(" messge", "Happy to see you");
        
        return  req;
    }*/
    @PostMapping(value = "/signin")
    public HashMap post(@RequestBody HashMap<String, String> req) throws NoSuchAlgorithmException, ProtocolException, IOException {
        String id = req.get("id");
        String username = req.get("user_name");
        String password = req.get("password");
        HashMap<String, String> resp = new HashMap<>();

        if (id.equals("1")) {
            String token = SecureDetails.SHA256of("any");
            Protocol_Table PT = new Protocol_Table();
            String res = PT.isUserAuthentic(username, password);
            if (res.contains("not")) {
                resp.put("response_code", "56");
                resp.put("success", "false");
                resp.put("message", "Invalid Credentials");
            } else {
                resp.put("response_code", "00");
                resp.put("success", "true");
                resp.put("token", token);
                resp.put("message", "Welcome, Protocol Officer " + username);
            }

        } else if (id.equals("2")) {
            Department_Table DT = new Department_Table();
            String tok[] = username.toUpperCase().split("admin");
            String token = SecureDetails.SHA256of(tok[0]);
            String res = DT.isUserAuthentic(username, password);
            if (res.contains("not")) {
                resp.put("response_code", "56");
                resp.put("success", "false");
                resp.put("message", "Invalid Credentials");
            } else {
                resp.put("response_code", "00");
                resp.put("success", "true");
                resp.put("token", token);
                resp.put("message", "Welcome, Admin " + username);
            }

        } else if (id.equals("3")) {
            Faculty_Table FT = new Faculty_Table();
            String tok[] = username.toUpperCase().split("admin");
            String token = SecureDetails.SHA256of(tok[0]);
            String res = FT.isUserAuthentic(username, password);
            if (res.contains("not")) {
                resp.put("response_code", "56");
                resp.put("success", "false");
                resp.put("message", "Invalid Credentials");
            } else {
                resp.put("response_code", "00");
                resp.put("success", "true");
                resp.put("token", token);
                resp.put("message", "Welcome, Admin " + username);
            }
        } else {
            resp.put("response_code", "77");
            resp.put("success", "false");
            resp.put("message", "Invalid ID");
        }
        return resp;
    }

    //Resources
    //Sigin student
    @PostMapping(value = "/updatestudentstatus")
    public HashMap status(@RequestBody HashMap<String, String> req) throws NoSuchAlgorithmException, ProtocolException, IOException {
        HashMap<String, String> res = new HashMap<>();
        String student_id = req.get("student_id");
        String status = req.get("status");
        String token = req.get("token");
        String priviledge = SecureDetails.SHA256of("any");

        if (token.matches(priviledge)) {
            Student_Table ST = new Student_Table();

            return ST.search(student_id, status);

        } else {
            res.put("success", "false");
            res.put("message", "Priviledge Denied");

        }
        return res;
    }

    //Visitors Request
    @PostMapping(value = "/visitorrequest")
    public HashMap visitorRequest(@RequestBody HashMap<String, String> req) throws NoSuchAlgorithmException, ProtocolException, IOException {
        HashMap<String, String> res = new HashMap<>();
        String token = req.get("token");
        String priviledge = SecureDetails.SHA256of("any");

        if (token.matches(priviledge)) {
            Visitor_Table VT = new Visitor_Table();
            return VT.saveRequest(req);

        } else {
            res.put("success", "false");
            res.put("message", "Priviledge Denied");

        }
        return res;
    }

    //Student Registration
    @PostMapping(value = "/registerstudent")
    public HashMap studentRegistration(@RequestBody HashMap<String, String> req) throws NoSuchAlgorithmException, ProtocolException, IOException {
        HashMap<String, String> res = new HashMap<>();
        String token = req.get("token");
        String priviledge = SecureDetails.SHA256of("any");

        if (token.matches(priviledge)) {
            Student_Table ST = new Student_Table();
            return ST.registerStudent(req);

        } else {
            res.put("success", "false");
            res.put("message", "Priviledge Denied");

        }
        return res;
    }

    // All Visitors 
    @GetMapping(value = "/allvisitors/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String allVisitors(@PathVariable(value = "token") String token) throws NoSuchAlgorithmException, ProtocolException, IOException {

        String priviledge = SecureDetails.SHA256of("any");

        if (token.matches(priviledge)) {
            Visitor_Table VT = new Visitor_Table();
            return VT.allVisitors();

        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
            sb.append(" \"message\" ").append(":").append("\"Priviledge Denied\"\n");
            sb.append("}]");

            return sb.toString();
        }

    }

    // Get Dept student
    @PostMapping(value = "/deptmentstudent", produces = MediaType.APPLICATION_JSON_VALUE)
    public String DeptmentStudent(@RequestBody HashMap<String, String> req) throws NoSuchAlgorithmException, ProtocolException, IOException {
        String token = req.get("token");
        String dept_id = req.get("dept_id");
        dept_id = dept_id.toUpperCase();

        String priviledge = SecureDetails.SHA256of("any");
        String priviledge2 = SecureDetails.SHA256of(dept_id);

        if (token.matches(priviledge) || token.matches(priviledge2)) {
            Student_Table ST = new Student_Table();
            return ST.allDeptStudentWith(dept_id);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
            sb.append(" \"message\" ").append(":").append("\"Priviledge Denied\"\n");
            sb.append("}]");

            return sb.toString();
        }

    }

    // Get Faculty student
    @PostMapping(value = "/facultystudent", produces = MediaType.APPLICATION_JSON_VALUE)
    public String FacultytStudent(@RequestBody HashMap<String, String> req) throws NoSuchAlgorithmException, ProtocolException, IOException {
        String token = req.get("token");
        String fac_id = req.get("fac_id");
        fac_id = fac_id.toUpperCase();

        String priviledge = SecureDetails.SHA256of("any");
        String priviledge2 = SecureDetails.SHA256of(fac_id);

        if (token.matches(priviledge) || token.matches(priviledge2)) {
            Student_Table ST = new Student_Table();
            return ST.allFactStudentWith(fac_id);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
            sb.append(" \"message\" ").append(":").append("\"Priviledge Denied\"\n");
            sb.append("}]");

            return sb.toString();
        }

    }

    // All Students 
    @GetMapping(value = "/allstudent/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String allStudents(@PathVariable(value = "token") String token) throws NoSuchAlgorithmException, ProtocolException, IOException {

        String priviledge = SecureDetails.SHA256of("any");

        if (token.matches(priviledge)) {
            Student_Table ST = new Student_Table();
            return ST.allstudent();

        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
            sb.append(" \"message\" ").append(":").append("\"Priviledge Denied\"\n");
            sb.append("}]");

            return sb.toString();
        }
    }

    @PostMapping(value = "/signoutvisitor", produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap signOutVisitor(@RequestBody HashMap<String, String> req) throws NoSuchAlgorithmException, ProtocolException, IOException {
        HashMap<String, String> res = new HashMap<>();
        String token = req.get("token");
        String visitor_id = req.get("visitor_id");

        String priviledge = SecureDetails.SHA256of("any");

        if (token.matches(priviledge)) {
            Visitor_Table ST = new Visitor_Table();
            return ST.searchVisitor(visitor_id);
        } else {
            res.put("success", "false");
            res.put("message", "Priviledge Denied");

            return res;
        }

    }

    //Statistics Report
    // All Students present, abesent and visitors 
    @GetMapping(value = "/protocolstatistics/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String protocolstatistics(@PathVariable(value = "token") String token) throws NoSuchAlgorithmException, ProtocolException, IOException {

        String priviledge = SecureDetails.SHA256of("any");

        if (token.matches(priviledge)) {
            Student_Table ST = new Student_Table();
            int totstu = ST.getTotalStudents();
            int totstupre = ST.getTotalStudentsPresent();
            int totstuab = ST.getTotalStudentsAbsent();

            Visitor_Table VT = new Visitor_Table();
            int totvisi = VT.getTotalVisitors();
            int totstuNSO = VT.getTotalVisitorsYetToSignOut();
            int totstuSO = VT.getTotalVisitorsSignedOut();

            StringBuilder sb = new StringBuilder();
            sb.append("{").append("\"total_student\"").append(":").append("\"" + totstu + "\",\n");
            sb.append(" \"total_student_present\" ").append(":").append("\"" + totstupre + "\",\n");
            sb.append(" \"total_student_absent\" ").append(":").append("\"" + totstuab + "\",\n");
            sb.append(" \"total_visitor\" ").append(":").append("\"" + totvisi + "\",\n");
            sb.append(" \"total_visitor_not_sign_out\" ").append(":").append("\"" + totstuNSO + "\",\n");
            sb.append(" \"total_visitor_signed_out\" ").append(":").append("\"" + totstuSO + "\"\n");

            sb.append("}");

            return sb.toString();

        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
            sb.append(" \"message\" ").append(":").append("\"Priviledge Denied\"\n");
            sb.append("}]");

            return sb.toString();
        }
    }

 /*   @GetMapping(value = "/statistics/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String statistics(@PathVariable(value = "token") String token) throws NoSuchAlgorithmException, ProtocolException, IOException {

        String priviledge = SecureDetails.SHA256of("any");
        String priviledge2 = SecureDetails.SHA256of(token);

        if (token.matches(priviledge)) {
            // Go get all details required
            Student_Table ST = new Student_Table();
            int totstu = ST.getTotalStudents();
            int totstupre = ST.getTotalStudentsPresent();
            int totstuab = ST.getTotalStudentsAbsent();

            Visitor_Table VT = new Visitor_Table();
            int totvisi = VT.getTotalVisitors();
            int totstuNSO = VT.getTotalVisitorsYetToSignOut();
            int totstuSO = VT.getTotalVisitorsSignedOut();

            StringBuilder sb = new StringBuilder();
            sb.append("{").append("\"total_student\"").append(":").append("\"" + totstu + "\",\n");
            sb.append(" \"total_student_present\" ").append(":").append("\"" + totstupre + "\"\n");
            sb.append(" \"total_student_absent\" ").append(":").append("\"" + totstuab + "\"\n");
            sb.append(" \"total_visitor\" ").append(":").append("\"" + totvisi + "\"\n");
            sb.append(" \"total_visitor_not_sign_out\" ").append(":").append("\"" + totstuNSO + "\"\n");
            sb.append(" \"total_visitor_signed_out\" ").append(":").append("\"" + totstuSO + "\"\n");

            sb.append("}");
m
            return sb.toString();

        } else {
            //Check with token
            
            
            
            
            
            
            StringBuilder sb = new StringBuilder();
            sb.append("[{").append("\"success\"").append(":").append("\"false\",\n");
            sb.append(" \"message\" ").append(":").append("\"Priviledge Denied\"\n");
            sb.append("}]");

            return sb.toString();
        }
    }*/
}
