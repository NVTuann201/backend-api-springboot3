package com.acme.backend.springboot.users.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("")
class UsersController {

    @GetMapping("/me")
    public Object me(ServletWebRequest request, Authentication authentication) {

        log.info("### Accessing {}", request.getRequest().getRequestURI());

        Object username = authentication.getName();

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello " + username);
        data.put("backend", "Spring Boot");
        data.put("datetime", Instant.now());
        return data;
    }

    @PostMapping("/global/vn/dktt/inquiry/account/list20/v1")
    public ResponseEntity<Object> getAccountDetails(ServletWebRequest servletWebRequest, @RequestHeader Map<String, String> headers, @RequestBody String request) {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            HashMap<String, Object> body = new ObjectMapper().readValue(request, HashMap.class);
           ;
            System.out.println( request.toString());
            System.out.println(body);

            responseHeaders.set("accessToken", headers.get("accesstoken"));
            responseHeaders.set("appCode", headers.get("appcode"));
            responseHeaders.set("channel", headers.get("channel"));
            responseHeaders.set("checksum", headers.get("checksum"));
            responseHeaders.set("requestID", headers.get("requestid"));
            responseHeaders.set("timestamp", headers.get("timestamp"));
            responseHeaders.set("errorcode", "200");
            responseHeaders.set("errordesc", "OK");
            responseHeaders.set("responseId", "20240130170823919");
            responseHeaders.set("timestamp", "20240130170823919");
            responseHeaders.set("timestamp", "20240130170823919");

            // Prepare the response body (account details)
            List<Map<String, String>> accountList = new ArrayList<>();
            Map<String, String> accountDetails = new HashMap<>();
            accountDetails.put("AcctNo", "8840020608");
            accountDetails.put("AcctType", "DDA");
            accountDetails.put("Branch", "222000");
            accountDetails.put("CurBal", "0");
            accountDetails.put("CurTyp", "VND");
            accountDetails.put("HoldBal", "0");
            accountDetails.put("IsJoinAccount", "N");
            accountDetails.put("MinBal", "50000");
            accountDetails.put("PrdCode", "1000");
            accountDetails.put("PrdLongDesc", "TGTT CA NHAN");
            accountDetails.put("PrdShortDesc", "TGTT CA NHAN");
            accountDetails.put("Status", "0");
            accountList.add(accountDetails);

            // Return the response
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(responseHeaders).body(accountList);
        } catch (Exception e) {
            // Handle the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}

