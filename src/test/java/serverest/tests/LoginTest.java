package serverest.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import serverest.util.TokenHolder;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static serverest.util.Mensagens.*;

public class LoginTest {

    static {
        RestAssured.baseURI = "https://serverest.dev";
    }

    @Test(
            priority = 1,
            description = "Deve logar usuário administrador com credenciais válidas",
            dependsOnMethods = "cadastrarUsuarioAdmin",
            groups = {"login", "sucesso"}
    )
    public void realizarLoginUsuarioAdmin() {
        String body = "{ \"email\": \"" + TokenHolder.email + "\", \"password\": \"" + TokenHolder.password + "\" }";

        Response response = given()
            .contentType("application/json")
            .log().all()
            .body(body)
        .when()
            .post("/login")
        .then()
            .log().all()
            .statusCode(200)
            .body("message", equalTo(MSG_LOGIN_SUCESSO))
            .body(matchesJsonSchemaInClasspath("schemas/login/realizar-login-schema.json"))
            .extract().response();

        TokenHolder.token = response.jsonPath().getString("authorization").replace("Bearer ", "");
    }

    @Test(
            priority = 2,
            description = "NÃO deve logar usuário com credenciais inválidas",
            dependsOnMethods = "cadastrarUsuarioAdmin",
            groups = {"login", "exceção"}
    )
    public void realizarLoginInvalido() {
        String body = "{ \"email\": \"email@invalido.com\", \"password\": \"1234\" }";

        given()
            .contentType("application/json")
            .log().all()
            .body(body)
        .when()
            .post("/login")
        .then()
            .log().all()
            .statusCode(401)
            .body("message", equalTo(MSG_LOGIN_ERRADO))
            .body(matchesJsonSchemaInClasspath("schemas/login/realizar-login-errado-schema.json"));
    }

    @Test(
            priority = 3,
            description = "NÃO deve logar usuário sem e-mail",
            dependsOnMethods = "cadastrarUsuarioAdmin",
            groups = {"login", "exceção"}
    )
    public void realizarLoginSemEmail() {
        String body = "{ \"email\": \"\", \"password\": \"1234\" }";

        given()
            .contentType("application/json")
            .log().all()
            .body(body)
        .when()
            .post("/login")
        .then()
            .log().all()
            .statusCode(400)
            .body("email", equalTo(MSG_LOGIN_SEM_EMAIL))
            .body(matchesJsonSchemaInClasspath("schemas/login/realizar-login-sem-email-schema.json"));
    }

    @Test(
            priority = 4,
            description = "NÃO deve logar usuário sem senha",
            dependsOnMethods = "cadastrarUsuarioAdmin",
            groups = {"login", "exceção"}
    )
    public void realizarLoginSemSenha() {
        String body = "{ \"email\": \"email@invalido.com\", \"password\": \"\" }";

        given()
            .contentType("application/json")
            .log().all()
            .body(body)
        .when()
            .post("/login")
        .then()
            .log().all()
            .statusCode(400)
            .body("password", equalTo(MSG_LOGIN_SEM_SENHA))
            .body(matchesJsonSchemaInClasspath("schemas/login/realizar-login-sem-senha-schema.json"));
    }

    @Test(
            priority = 4,
            description = "NÃO deve logar usuário sem senha",
            dependsOnMethods = "cadastrarUsuarioAdmin",
            groups = {"login", "exceção"}
    )
    public void realizarLoginSemEmailSenha() {
        String body = "{ \"email\": \"\", \"password\": \"\" }";

        given()
            .contentType("application/json")
            .log().all()
            .body(body)
        .when()
            .post("/login")
        .then()
            .log().all()
            .statusCode(400)
            .body("email", equalTo(MSG_LOGIN_SEM_EMAIL))
            .body("password", equalTo(MSG_LOGIN_SEM_SENHA))
            .body(matchesJsonSchemaInClasspath("schemas/login/realizar-login-sem-email-senha-schema.json"));
    }

}
