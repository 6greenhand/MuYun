package net.ximatai.muyun.test.plaform;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import net.ximatai.muyun.core.config.MuYunConfig;
import net.ximatai.muyun.platform.PlatformConst;
import net.ximatai.muyun.test.testcontainers.PostgresTestResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
@QuarkusTestResource(value = PostgresTestResource.class)
public class TestUserAndRole {

    @Inject
    MuYunConfig config;

    String base = PlatformConst.BASE_PATH;

    @Test
    void test() {
        String userID = given()
            .header("userID", config.superUserId())
            .contentType("application/json")
            .body(Map.of(
                "v_name", "测试xx",
                "dict_user_gender", "0"
            ))
            .when()
            .post("/api%s/userinfo/create".formatted(base))
            .then()
            .statusCode(200)
            .extract()
            .asString();

        // 设置用户
        given()
            .header("userID", config.superUserId())
            .contentType("application/json")
            .body(Map.of(
                "v_username", "test_1",
                "v_password", "pw",
                "v_password2", "pw"
            ))
            .when()
            .post("/api%s/userinfo/setUser/%s".formatted(base, userID))
            .then()
            .statusCode(200)
            .extract()
            .asString();

        String roleID = given()
            .header("userID", config.superUserId())
            .contentType("application/json")
            .body(Map.of(
                "v_name", "测试"
            ))
            .when()
            .post("/api%s/role/create".formatted(base))
            .then()
            .statusCode(200)
            .extract()
            .asString();

        given()
            .header("userID", config.superUserId())
            .get("/api%s/role/assign/%s/to/%s".formatted(base, roleID, userID))
            .then()
            .statusCode(200)
            .extract();

        List<String> roles = given()
            .header("userID", config.superUserId())
            .get("/api%s/userinfo/roles/%s".formatted(base, userID))
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {
                }
            );

        assertTrue(roles.contains(roleID));

        // 已分配人员不允许删除
        given()
            .header("userID", config.superUserId())
            .get("/api%s/role/delete/%s".formatted(base, roleID))
            .then()
            .statusCode(500);

        given()
            .header("userID", config.superUserId())
            .get("/api%s/role/revoke/%s/to/%s".formatted(base, roleID, userID))
            .then()
            .statusCode(200)
            .extract();

        given()
            .header("userID", config.superUserId())
            .get("/api%s/role/delete/%s".formatted(base, roleID))
            .then()
            .statusCode(200);

        List<String> roles2 = given()
            .header("userID", config.superUserId())
            .get("/api%s/userinfo/roles/%s".formatted(base, userID))
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {
                }
            );

        assertFalse(roles2.contains(roleID));

        String role1 = given()
            .header("userID", config.superUserId())
            .contentType("application/json")
            .body(Map.of(
                "v_name", "测试"
            ))
            .when()
            .post("/api%s/role/create".formatted(base))
            .then()
            .statusCode(200)
            .extract()
            .asString();

        String role2 = given()
            .header("userID", config.superUserId())
            .contentType("application/json")
            .body(Map.of(
                "v_name", "测试"
            ))
            .when()
            .post("/api%s/role/create".formatted(base))
            .then()
            .statusCode(200)
            .extract()
            .asString();

        String count = given()
            .header("userID", config.superUserId())
            .contentType("application/json")
            .body(List.of(role1, role2))
            .post("/api%s/userinfo/setRoles/%s".formatted(base, userID))
            .then()
            .statusCode(200)
            .extract()
            .asString();

        Assertions.assertEquals("2", count);

        List<String> roles3 = given()
            .header("userID", config.superUserId())
            .get("/api%s/userinfo/roles/%s".formatted(base, userID))
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {
                }
            );

        Assertions.assertTrue(roles3.contains(role1));
        Assertions.assertTrue(roles3.contains(role2));
    }

}
