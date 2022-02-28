import account.AccountServiceApplication;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.UnexpectedError;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.request.HttpRequest;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hyperskill.hstest.common.JsonUtils.getJson;
import static org.hyperskill.hstest.common.JsonUtils.getPrettyJson;
import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.*;

class TestReq {

  private Map<String, Object> properties = new LinkedHashMap<>();

  // Deep copy
  public TestReq(TestReq another) {
    this.properties = another.properties.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public TestReq() {
  }

  public String toJson() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    try {
      return mapper.writeValueAsString(this.properties);
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  public TestReq setProps(String key, Object value) {
    properties.put(key, value);
    return this;
  }

}
public class AccountServiceTest extends SpringTest {

  private  final String signUpApi = "/api/auth/signup";
  private  final String changePassApi = "/api/auth/changepass";
  private  final String getEmployeePaymentApi = "/api/empl/payment";
  private final String postPaymentApi = "/api/acct/payments";
  private final String putRoleApi = "/api/admin/user/role";
  private final String putAccessApi = "/api/admin/user/access";
  private final String adminApi = "/api/admin/user/";
  private final String auditorApi = "/api/security/events/";

  static String[] breachedPass= new String[]{"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch",
          "PasswordForApril", "PasswordForMay", "PasswordForJune",
          "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember",
          "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

  List<Integer> userIdList = new ArrayList<>();

  private final TestReq ivanIvanov = new TestReq().setProps("name", "Ivan")
          .setProps("lastname", "Ivanov")
          .setProps("email", "IvanIvanov@acme.com")
          .setProps("password", "rXoa4CvqpLxW");
  private final TestReq petrPetrov = new TestReq().setProps("name", "Petr")
          .setProps("lastname", "Petrov")
          .setProps("email", "PetrPetrov@acme.com")
          .setProps("password", "nWza98hjkLPE");
  private final TestReq johnDoe = new TestReq().setProps("name", "John")
          .setProps("lastname", "Doe")
          .setProps("email", "JohnDoe@acme.com")
          .setProps("password", "oMoa3VvqnLxW");
  private final TestReq maxMus = new TestReq().setProps("name", "Max")
          .setProps("lastname", "Mustermann")
          .setProps("email", "MaxMustermann@acme.com")
          .setProps("password", "ai0y9bMvyF6G");
  private final TestReq captainNemo = new TestReq().setProps("name", "Captain")
          .setProps("lastname", "Nemo")
          .setProps("email", "nautilus@pompilius.com")
          .setProps("password", "wings");

  private final String ivanIvanovCorrectUser = ivanIvanov.toJson();
  private final String petrPetrovCorrectUser = petrPetrov.toJson();
  private final String jDCorrectUser = johnDoe.toJson();
  private final String maxMusCorrectUser = maxMus.toJson();
  private final String maxMusLower = new TestReq(maxMus).setProps("email", "maxmustermann@acme.com").toJson();
  private final String maxMusWrongPassword = new TestReq(maxMus).setProps("password", "none").toJson();
  private final String maxMusWrongEmail = new TestReq(maxMus).setProps("email", "maxmustermann@google.com").toJson();
  private final String captainNemoWrongUser = captainNemo.toJson();
  private final String jDNewPass = new TestReq(johnDoe).setProps("password", "aNob5VvqzRtb").toJson();
  private final String jDPass = new TestReq().setProps("new_password", "aNob5VvqzRtb").toJson();

  private final String firstResponseAdminApi = convert(new String[]{
          new TestReq().setProps("id", 1).setProps("name", "John").setProps("lastname", "Doe")
                  .setProps("email", "johndoe@acme.com").setProps("roles", new String[] {"ROLE_ADMINISTRATOR"}).toJson(),
          new TestReq().setProps("id", 2).setProps("name", "Ivan").setProps("lastname", "Ivanov")
                  .setProps("email", "ivanivanov@acme.com")
                  .setProps("roles", new String[] {"ROLE_AUDITOR", "ROLE_USER"}).toJson(),
          new TestReq().setProps("id", 3).setProps("name", "Max").setProps("lastname", "Mustermann")
                  .setProps("email", "maxmustermann@acme.com").setProps("roles", new String[] {"ROLE_USER"}).toJson()
  });

  private final String auditorResponseApi = convert(new String[]{
          new TestReq().setProps("action", "CREATE_USER").setProps("subject", "Anonymous")
                  .setProps("object", "johndoe@acme.com").setProps("path", "/api/auth/signup").toJson(),
          new TestReq().setProps("action", "CREATE_USER").setProps("subject", "Anonymous")
                  .setProps("object", "ivanivanov@acme.com").setProps("path", "/api/auth/signup").toJson(),
          new TestReq().setProps("action", "GRANT_ROLE").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "Grant role AUDITOR to ivanivanov@acme.com").setProps("path", "/api/admin/user/role").toJson(),
          new TestReq().setProps("action", "CREATE_USER").setProps("subject", "Anonymous")
                  .setProps("object", "maxmustermann@acme.com").setProps("path", "/api/auth/signup").toJson(),
          new TestReq().setProps("action", "CREATE_USER").setProps("subject", "Anonymous")
                  .setProps("object", "petrpetrov@acme.com").setProps("path", "/api/auth/signup").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@google.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "nautilus@pompilius.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "GRANT_ROLE").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "Grant role ACCOUNTANT to petrpetrov@acme.com").setProps("path", "/api/admin/user/role").toJson(),
          new TestReq().setProps("action", "REMOVE_ROLE").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "Remove role ACCOUNTANT from petrpetrov@acme.com").setProps("path", "/api/admin/user/role").toJson(),
          new TestReq().setProps("action", "DELETE_USER").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "petrpetrov@acme.com").setProps("path", "/api/admin/user").toJson(),
          new TestReq().setProps("action", "CHANGE_PASSWORD").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "johndoe@acme.com").setProps("path", "/api/auth/changepass").toJson(),
          new TestReq().setProps("action", "ACCESS_DENIED").setProps("subject", "ivanivanov@acme.com")
                  .setProps("object", "/api/admin/user/role").setProps("path", "/api/admin/user/role").toJson(),
          new TestReq().setProps("action", "ACCESS_DENIED").setProps("subject", "ivanivanov@acme.com")
                  .setProps("object", "/api/admin/user").setProps("path", "/api/admin/user").toJson(),
          new TestReq().setProps("action", "ACCESS_DENIED").setProps("subject", "ivanivanov@acme.com")
                  .setProps("object", "/api/admin/user").setProps("path", "/api/admin/user").toJson(),
          new TestReq().setProps("action", "ACCESS_DENIED").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "/api/acct/payments").setProps("path", "/api/acct/payments").toJson(),
          new TestReq().setProps("action", "ACCESS_DENIED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/acct/payments").setProps("path", "/api/acct/payments").toJson(),
          new TestReq().setProps("action", "ACCESS_DENIED").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "ACCESS_DENIED").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "/api/security/events").setProps("path", "/api/security/events").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "BRUTE_FORCE").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOCK_USER").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "Lock user maxmustermann@acme.com").setProps("path", "/api/admin/user/access").toJson(), // api !!!
          new TestReq().setProps("action", "UNLOCK_USER").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "Unlock user maxmustermann@acme.com").setProps("path", "/api/admin/user/access").toJson(), // api !!!
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "LOGIN_FAILED").setProps("subject", "maxmustermann@acme.com")
                  .setProps("object", "/api/empl/payment").setProps("path", "/api/empl/payment").toJson(),
          new TestReq().setProps("action", "UNLOCK_USER").setProps("subject", "johndoe@acme.com")
                  .setProps("object", "Unlock user maxmustermann@acme.com").setProps("path", "/api/admin/user/access").toJson() // api !!!
  });

  public AccountServiceTest() {
    super(AccountServiceApplication.class, "../service_db.mv.db");
  }

  private String convert(String[] trs) {
    JsonArray  jsonArray = new JsonArray();
    for (String tr : trs) {
      JsonElement jsonObject = JsonParser.parseString(tr);
      jsonArray.add(jsonObject);
    }
    return jsonArray.toString();
  }

  CheckResult testApi(String user, String body, int status, String api, String method, String message) {

    HttpResponse response = checkResponseStatus(user, body, status, api, method, message);


    return CheckResult.correct();
  }

  /**
   * Method for checking response on Post request for signup API
   *
   * @param body string representation of body content in JSON format (String)
   * @param status required http status for response (int)
   * @return instance of CheckResult class containing result of checks (CheckResult)
   */
  CheckResult testPostSignUpResponse(String body, int status, String[] role) {

    HttpResponse response = checkResponseStatus(null, body, status, signUpApi, "POST", "");

    JsonObject rightResponse = getJson(body).getAsJsonObject();
    rightResponse.remove("password");

    // Check is it JSON in response or something else
    if (!response.getJson().isJsonObject()) {
      return CheckResult.wrong("Wrong object in response, expected JSON but was \n" +
              response.getContent().getClass());

    }

    JsonObject jsonResponse = response.getJson().getAsJsonObject();

    // Check if password is presence in response
    if (jsonResponse.get("password") != null) {
      return CheckResult.wrong("You must remove password from response\n" +
              jsonResponse);
    }

    if (!jsonResponse.get("email").getAsString().endsWith("@acme.com")) {
      return CheckResult.wrong("Service must accept only corporate emails that end with @acme.com\n" +
              jsonResponse);
    }

    if (jsonResponse.get("id") == null) {
      return CheckResult.wrong("Response must contain user ID\n" +
              "Received response:\n" +
              jsonResponse);
    }

    if (userIdList.contains(jsonResponse.get("id").getAsInt())) {
      return CheckResult.wrong("User ID must be unique!\n" +
              "Received response:\n" +
              jsonResponse);
    }
    rightResponse.addProperty("id", jsonResponse.get("id").toString());
    // Check JSON in response
    expect(response.getContent()).asJson().check(
            isObject()
                    .value("id", isInteger())
                    .value("name", rightResponse.get("name").getAsString())
                    .value("lastname", rightResponse.get("lastname").getAsString())
                    .value("email", rightResponse.get("email").getAsString().toLowerCase())
                    .value("roles", role));
    userIdList.add(jsonResponse.get("id").getAsInt());
    return CheckResult.correct();
  }

  /**
   * Method for restarting application
   *
   */
  private CheckResult restartApplication() {
    try {
      reloadSpring();
    } catch (Exception ex) {
      throw new UnexpectedError(ex.getMessage());
    }
    return CheckResult.correct();
  }

  /**
   * Method for checking authentication
   *
   * @param user string representation of user information in JSON format (String)
   * @param status required http status for response (int)
   * @param message hint about reason of error (String)
   * @return instance of CheckResult class containing result of checks (CheckResult)
   */
  private CheckResult testUserRegistration(String user, int status, String message) {

    HttpResponse response = checkResponseStatus(user, "", status, getEmployeePaymentApi, "GET", message);

    return CheckResult.correct();
  }

  CheckResult testChangePassword(String api, String body, int status, String user) {
    JsonObject userJson = getJson(user).getAsJsonObject();

    HttpResponse response = checkResponseStatus(user, body, status, api, "POST", "");

    // Check JSON in response
    if (status == 200) {
      expect(response.getContent()).asJson().check(
              isObject()
                      .value("email", userJson.get("email").getAsString().toLowerCase())
                      .value("status", "The password has been updated successfully"));
    }
    return CheckResult.correct();
  }

  private CheckResult testGetAdminApi(String api, int status, String user, String answer, String message) {

    HttpResponse response = checkResponseStatus(user, "", status, api, "GET", message);

    JsonArray correctJson = getJson(answer).getAsJsonArray();
    JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

    if (responseJson.size() == 0)  {
      return CheckResult.wrong("No data in response body" + "\n"
              + "in response " + getPrettyJson(responseJson)  + "\n"
              + "must be " + getPrettyJson(correctJson));
    }

    // Check JSON in response
    if (response.getStatusCode() == 200) {
      for (int i = 0; i < responseJson.size(); i++) {

        String[] roles = new String[correctJson.get(i).getAsJsonObject().getAsJsonArray("roles").size()];
        for(int j=0; j<correctJson.get(i).getAsJsonObject().getAsJsonArray("roles").size(); j++) {
          roles[j]=correctJson.get(i).getAsJsonObject().getAsJsonArray("roles").get(j).getAsString();
        }

        expect(responseJson.get(i).getAsJsonObject().toString()).asJson()
                .check(isObject()
                        .value("id", isInteger())
                        .value("name", correctJson.get(i).getAsJsonObject().get("name").getAsString())
                        .value("lastname", correctJson.get(i).getAsJsonObject().get("lastname").getAsString())
                        .value("email", correctJson.get(i).getAsJsonObject().get("email").getAsString())
                        .value("roles", isArray( roles )));
      }
    }
    return CheckResult.correct();
  }

  CheckResult testPutAdminApi(String api, HttpStatus status, String user, String reqUser,
                              String role, String operation, String[] respRoles, String message) {

    JsonObject jsonUser = getJson(reqUser).getAsJsonObject();
    JsonObject request = new JsonObject();
    request.addProperty("user", jsonUser.get("email").getAsString());
    request.addProperty("operation", operation);
    request.addProperty("role", role);

    HttpResponse response = checkResponseStatus(user, request.toString(), status.value(), api, "PUT", message);

    // Check JSON in response
    if (response.getStatusCode() == 200) {
      expect(response.getContent()).asJson()
              .check(isObject()
                      .value("id", isInteger())
                      .value("name", jsonUser.get("name").getAsString())
                      .value("lastname", jsonUser.get("lastname").getAsString())
                      .value("email", jsonUser.get("email").getAsString().toLowerCase())
                      .value("roles", isArray(respRoles)));
    }

    if (response.getStatusCode() != 200) {
      expect(response.getContent()).asJson().check(
              isObject()
                      .value("error", status.getReasonPhrase())
                      .value("path", api)
                      .value("status", status.value())
                      .value("message", respRoles[0])
                      .anyOtherValues());
    }
    return CheckResult.correct();
  }

  CheckResult testPutAccessApi(String api, HttpStatus status, String user, String reqUser,
                               String operation, String answer, String message) {

    JsonObject jsonUser = getJson(reqUser).getAsJsonObject();
    JsonObject request = new JsonObject();
    request.addProperty("user", jsonUser.get("email").getAsString());
    request.addProperty("operation", operation);

    HttpResponse response = checkResponseStatus(user, request.toString(), status.value(), api, "PUT", message);

    // Check JSON in response

    if (response.getStatusCode() == 200) {
      expect(response.getContent()).asJson()
              .check(isObject()
                      .value("status", answer));
    } else {
      expect(response.getContent()).asJson()
              .check(isObject()
                      .value("error", status.getReasonPhrase())
                      .value("path", api)
                      .value("status", status.value())
                      .value("message", answer)
                      .anyOtherValues());
    }


    return CheckResult.correct();
  }

  CheckResult testLocking(String api, HttpStatus status, String user, String answer, String message) {

    HttpResponse response = checkResponseStatus(user, "", status.value(), api, "GET", message);

    // Check JSON in response
    if (response.getStatusCode() != 200) {
      expect(response.getContent()).asJson().check(
              isObject()
                      .value("error", status.getReasonPhrase())
                      .value("path", api)
                      .value("status", status.value())
                      .value("message", answer)
                      .anyOtherValues());
    }
    return CheckResult.correct();
  }

  CheckResult testRoleModelNegative(String api, String method, HttpStatus status, String user, String body, String message) {

    HttpResponse response = checkResponseStatus(user, body, status.value(), api, method.toUpperCase(), message);

    // Check JSON in response
    if (response.getStatusCode() != 200) {
      expect(response.getContent()).asJson().check(
              isObject()
                      .value("error", status.getReasonPhrase())
                      .value("path", api)
                      .value("status", status.value())
                      .value("message", "Access Denied!")
                      .anyOtherValues());
    }
    return CheckResult.correct();
  }

  private CheckResult testAuditorApi(String api, int status, String user, String answer,
                                     int position, String message) {

    HttpResponse response = checkResponseStatus(user, "", status, api, "GET", message);


    JsonArray correctJson = getJson(answer).getAsJsonArray();
    JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();
    if (responseJson.size() == 0) {
      throw new WrongAnswer("Empty array in response!");
    }



    // Check JSON in response
    if (response.getStatusCode() == 200) {
      expect(responseJson.get(position).toString()).asJson().check(
              isObject()
                      .value("action", correctJson.get(position).getAsJsonObject().get("action").getAsString())
                      .value("subject", correctJson.get(position).getAsJsonObject().get("subject").getAsString())
                      .value("object", isString( o -> o.contains(correctJson.get(position).getAsJsonObject().get("object").getAsString())))
                      .value("path", isString())
                      .anyOtherValues());

    }
    return CheckResult.correct();
  }

  CheckResult testDeleteAdminApi(String api, HttpStatus status, String user, String param,
                                 String answer, String message) {

    HttpResponse response = checkResponseStatus(user, "", status.value(),
            api + param, "DELETE", message);

    // Check JSON in response
    if (response.getStatusCode() == 200) {
      expect(response.getContent()).asJson().check(
              isObject()
                      .value("user", param.toLowerCase())
                      .value("status", answer));
    }

    if (response.getStatusCode() != 200) {
      expect(response.getContent()).asJson().check(
              isObject()
                      .value("error", status.getReasonPhrase())
                      .value("path", api + param)
                      .value("status", status.value())
                      .value("message", answer)
                      .anyOtherValues());
    }
    return CheckResult.correct();
  }


  /**
   * Method for testing api response
   *
   * @param user string representation of user information in JSON format (String)
   * @param body request body (String)
   * @param status expected response status (int)
   * @param api testing api (String)
   * @param method method for api (String)
   * @param message test hints for student (String)
   * @return response (HttpResponse)
   */
  private HttpResponse checkResponseStatus(String user, String body,
                                           int status, String api, String method, String message) {
    HttpRequest request = null;
    switch (method) {
      case "GET":
        request = get(api);
        break;
      case "POST":
        request = post(api, body);
        break;
      case "PUT":
        request = put(api, body);
        break;
      case "DELETE":
        request = delete(api);
        break;
    }

    if (user != null) {
      JsonObject userJson = getJson(user).getAsJsonObject();
      String password = userJson.get("password").getAsString();
      String login = userJson.get("email").getAsString().toLowerCase();
      request = request.basicAuth(login, password);
    }
    HttpResponse response = request.send();

    if (response.getStatusCode() != status) {
      throw new WrongAnswer(method + " " + api  + " should respond with "
              + "status code " + status + ", responded: " + response.getStatusCode() + "\n"
              + message + "\n"
              + "Response body:\n" + response.getContent() + "\n");
    }
    return response;
  }

  @DynamicTest
  DynamicTesting[] dt = new DynamicTesting[] {

          // Create administrator and auditor
          () -> testPostSignUpResponse(jDCorrectUser, 200, new String[] {"ROLE_ADMINISTRATOR"}),
          () -> testPostSignUpResponse(ivanIvanovCorrectUser, 200, new String[] {"ROLE_USER"}),
          () -> testPutAdminApi(putRoleApi, HttpStatus.OK, jDCorrectUser,
                  ivanIvanovCorrectUser, "AUDITOR", "GRANT",
                  new String[] {"ROLE_AUDITOR", "ROLE_USER"}, ""),

          // Testing user registration positive tests
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 0,"'CREATE_USER' security event missing"), // 3
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 1,"'CREATE_USER' security event missing"), // 3
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 2,"'GRANT_ROLE' security event missing"), // 3
          () -> testPostSignUpResponse(maxMusLower, 200, new String[] {"ROLE_USER"}),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 3,"'CREATE_USER' security event missing"), // 4
          () -> testPostSignUpResponse(petrPetrovCorrectUser, 200, new String[] {"ROLE_USER"}),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 4,"'CREATE_USER' security event missing"), // 5

//
//          // Test authentication, positive tests
          () -> testUserRegistration(maxMusLower, 200, "User must login!"),
          () -> testUserRegistration(maxMusCorrectUser, 200, "Login case insensitive!"),
//
//          // Test authentication, negative tests
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 5,"'LOGIN_FAILED' security event missing"), // 6
          () -> testUserRegistration(maxMusWrongEmail, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 6,"'LOGIN_FAILED' security event missing"), // 7
          () -> testUserRegistration(captainNemoWrongUser, 401, "Wrong user"),
          () -> testApi(null, "", 401, getEmployeePaymentApi, "GET",
                  "This api only for authenticated user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 7,"'LOGIN_FAILED' security event missing"), // 8

//          // Testing persistence
          () -> restartApplication(),
          () -> testUserRegistration(maxMusCorrectUser, 200, "User must login, after restarting!" +
                  " Check persistence."),

          // Changing roles
          () -> testPutAdminApi(putRoleApi, HttpStatus.BAD_REQUEST, jDCorrectUser,
                  jDCorrectUser, "AUDITOR", "GRANT",
                  new String[] {"The user cannot combine administrative and business roles!"},
                  "Trying add administrative role to business user!"),
          () -> testPutAdminApi(putRoleApi, HttpStatus.OK, jDCorrectUser,
                  petrPetrovCorrectUser, "ACCOUNTANT", "GRANT",
                  new String[] {"ROLE_ACCOUNTANT", "ROLE_USER"}, "Trying to add role ACCOUNTANT to user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 8,"'GRANT_ROLE' security event missing"), // 9
          () -> testPutAdminApi(putRoleApi, HttpStatus.OK, jDCorrectUser,
                  petrPetrovCorrectUser, "ACCOUNTANT", "REMOVE",
                  new String[] {"ROLE_USER"}, "Trying to remove role ACCOUNTANT from user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 9,"'REMOVE_ROLE' security event missing"), // 10

          //Delete user
          () -> testDeleteAdminApi("/api/admin/user/", HttpStatus.OK, jDCorrectUser,
                  "petrpetrov@acme.com", "Deleted successfully!", "Trying to delete user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 10,"'DELETE_USER' security event missing"), // 11

          // Change password
          () -> testChangePassword(changePassApi, jDPass, 200, jDCorrectUser),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 11,"'CHANGE_PASSWORD' security event missing"), // 12

          // Testing role model negative case
          () -> testRoleModelNegative(putRoleApi, "PUT", HttpStatus.FORBIDDEN, ivanIvanovCorrectUser,
                  "", "Trying to access administrative endpoint with business user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 12,"'ACCESS_DENIED' security event missing"), // 13
          () -> testRoleModelNegative(adminApi, "GET", HttpStatus.FORBIDDEN, ivanIvanovCorrectUser,
                  "", "Trying to access administrative endpoint with business user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 13,"'ACCESS_DENIED' security event missing"), //14
          () -> testRoleModelNegative(adminApi, "DELETE", HttpStatus.FORBIDDEN, ivanIvanovCorrectUser,
                  "", "Trying to access administrative endpoint with business user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 14,"'ACCESS_DENIED' security event missing"), // 15
          () -> testRoleModelNegative(postPaymentApi, "POST", HttpStatus.FORBIDDEN, jDNewPass,
                  "", "Trying to access business endpoint with administrative user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 15,"'ACCESS_DENIED' security event missing"), // 16
          () -> testRoleModelNegative(postPaymentApi, "POST", HttpStatus.FORBIDDEN, maxMusCorrectUser,
                  "", "Trying to access endpoint with wrong role"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 16,"'ACCESS_DENIED' security event missing"), // 17
          () -> testRoleModelNegative(getEmployeePaymentApi, "GET", HttpStatus.FORBIDDEN, jDNewPass,
                  "", "Trying to access business endpoint with administrative user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 17,"'ACCESS_DENIED' security event missing"), // 18

          () -> testRoleModelNegative(auditorApi, "GET", HttpStatus.FORBIDDEN, jDNewPass,
                  "", "Trying to access business endpoint with administrative user"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 18,"'ACCESS_DENIED' security event missing"), // 19

          // Testing locking user
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 19,"'LOGIN_FAILED' security event missing"), // 20
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 20,"'LOGIN_FAILED' security event missing"), // 21
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 21,"'LOGIN_FAILED' security event missing"), // 22
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 22,"'LOGIN_FAILED' security event missing"), // 23
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 23,"'LOGIN_FAILED' security event missing"), // 24
          () -> testLocking(getEmployeePaymentApi, HttpStatus.UNAUTHORIZED, maxMusCorrectUser,
                  "User account is locked", "User must be locked after 5 attempts with wrong password"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 24,"'BRUTE_FORCE' security event missing"), // 25
          () -> testPutAccessApi(putAccessApi, HttpStatus.OK, jDNewPass,
                  maxMusCorrectUser,"UNLOCK",
                  "User maxmustermann@acme.com unlocked!", "User must be unlocked through admin endpoint"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 25,"'LOCK_USER' security event missing"), // 25
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 26,"'UNLOCK_USER' security event missing"), // 26
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 27,"'LOGIN_FAILED' security event missing"), // 27
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 28,"'LOGIN_FAILED' security event missing"), // 28
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 29,"'LOGIN_FAILED' security event missing"),
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 30,"'LOGIN_FAILED' security event missing"),
          () -> testUserRegistration(maxMusCorrectUser, 200, "User must login!"),
          () -> testUserRegistration(maxMusWrongPassword, 401, "Wrong password!"),
          () -> testUserRegistration(maxMusCorrectUser, 200,
                  "Counter of failed login attempts must be reset after successfully login!"),
          () -> testPutAccessApi(putAccessApi, HttpStatus.OK, jDNewPass,
                  maxMusCorrectUser,"LOCK",
                  "User maxmustermann@acme.com locked!", ""),
          () -> testAuditorApi(auditorApi, 200, ivanIvanovCorrectUser,
                  auditorResponseApi, 31,"'LOCK_USER' security event missing"),
          () -> testLocking(getEmployeePaymentApi, HttpStatus.UNAUTHORIZED, maxMusCorrectUser,
                  "User account is locked", "User must be locked through admin endpoint"),
          () -> testPutAccessApi(putAccessApi, HttpStatus.BAD_REQUEST, jDNewPass,
                  jDCorrectUser,"LOCK",
                  "Can't lock the ADMINISTRATOR!", ""),
          () -> testGetAdminApi(adminApi, 200, jDNewPass,
                  firstResponseAdminApi, "Api must be available to admin user"),

  };

}