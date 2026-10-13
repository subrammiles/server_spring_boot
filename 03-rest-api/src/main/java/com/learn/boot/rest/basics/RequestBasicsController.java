package com.learn.boot.rest.basics;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Every way an HTTP request carries data, one small endpoint each. */
@RestController
@RequestMapping("/basics")
public class RequestBasicsController {

    /** GET /basics/query?data=hi */
    @GetMapping("/query")
    public String oneQueryParam(@RequestParam String data) {
        return "You sent data = " + data;
    }

    /** GET /basics/query2?data=a&data2=b  (data2 optional, with default) */
    @GetMapping("/query2")
    public String twoQueryParams(@RequestParam String data, @RequestParam(defaultValue = "none") String data2) {
        return "data = " + data + ", data2 = " + data2;
    }

    /** All query params at once: GET /basics/all-params?a=1&b=2 */
    @GetMapping("/all-params")
    public Map<String, String> allParams(@RequestParam Map<String, String> params) {
        return params;
    }

    /** JSON body -> Java object: POST /basics/employees {"id":1,"name":"Subbu","salary":100} */
    @PostMapping("/employees")
    public String newEmployee(@RequestBody Employee employee) {
        return "Data of " + employee.name() + " is saved";
    }

    /** Body AND query parameter together. */
    @PostMapping("/employees-with-query")
    public String newEmployeeWithQuery(@RequestBody Employee employee, @RequestParam String department) {
        return "Data of " + employee.name() + " is saved to " + department;
    }

    /** Headers and cookies. */
    @GetMapping("/whoami")
    public Map<String, String> whoAmI(@RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
                                      @RequestHeader(value = "X-Tenant", defaultValue = "public") String tenant,
                                      @CookieValue(value = "session", defaultValue = "anonymous") String session) {
        return Map.of("userAgent", userAgent, "tenant", tenant, "session", session);
    }

    /** The long form: @GetMapping is shorthand for exactly this. */
    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public String balance(@RequestParam String userId) {
        return "For user ID = " + userId + " balance is 100";
    }

    /** Full control over status, headers and body. */
    @GetMapping("/custom-response")
    public ResponseEntity<String> customResponse() {
        return ResponseEntity.accepted()
                .header("X-Processing-Time", "42ms")
                .body("Accepted for processing");
    }
}
