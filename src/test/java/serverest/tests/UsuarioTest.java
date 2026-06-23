package serverest.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import serverest.model.Usuario;
import serverest.util.TokenHolder;
import serverest.util.UsuarioHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static serverest.util.Mensagens.MSG_CADASTRO_SUCESSO;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

public class UsuarioTest {

    static {
        RestAssured.baseURI = "https://serverest.dev";
    }

    String usuarioId;
    String email = UsuarioHelper.gerarEmail();

    String senha = "1234";
    Usuario usuarioCriado = new Usuario("Admin QA", email, senha, "true");

    @Test()
    public void cadastrarUsuarioAdmin() {
        usuarioId = given()
            .contentType("application/json")
            .log().all()
            .body(usuarioCriado)
        .when()
            .post("/usuarios")
        .then()
            .log().all()
            .statusCode(201)
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .body("_id", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/usuario/cadastrar-usuario-schema.json"))
            .extract()
            .path("_id");

        TokenHolder.email = email;
        TokenHolder.password = senha;
    }

}
