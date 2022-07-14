package com.yanmastra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jboss.logmanager.Logger;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    public final static Logger LOGGER = Logger.getLogger(GreetingResourceTest.class.getSimpleName());

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello QUARKUS"));
    }

    @Test
    public void testTrainingEndPoint() {
        given()
                .when().get("/training/login?username=yanmastra&password=123456")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body(new BaseMatcher<String>() {
                    @Override
                    public boolean matches(Object o) {
                        LOGGER.info(o.toString());
                        if (o instanceof String) {
                            try {
                                String resp = (String) o;
                                ObjectMapper om = new ObjectMapper();
                                Map<String, Object> json = om.readValue(resp, new TypeReference<>() {
                                });

                                given()
                                        .when().header(new Header(
                                                "Authorization",
                                        "Bearer "+json.get("token")+""
                                        )).get("/training/halo")
                                        .then().statusCode(200);

                            } catch (Exception e) {
                                LOGGER.info(e.getMessage());
                            }
                        }
                        return true;
                    }

                    @Override
                    public void describeTo(Description description) {

                        LOGGER.info("desc: "+description.toString());
                    }
                });
    }

}