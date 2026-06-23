package serverest.tests;

import io.restassured.RestAssured;
import org.testng.annotations.Test;
import serverest.model.Usuario;
import serverest.util.TokenHolder;
import serverest.util.UsuarioHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import static serverest.util.IdHelper.gerarIdAleatorio;
import static serverest.util.Mensagens.*;

public class UsuarioTest {

    static {
        RestAssured.baseURI = "https://serverest.dev";
    }

    String usuarioId;
    String usuarioComumId;
    String usuarioInexistenteId;

    String email = UsuarioHelper.gerarEmail();
    String emailComum = UsuarioHelper.gerarEmail();

    String senha = "1234";

    Usuario usuarioCriado = new Usuario("Admin QA", email, senha, "true");
    Usuario usuarioComumCriado = new Usuario("Usuario QA",  emailComum, senha, "false");

    @Test(description = "Deve cadastrar um usuário Administrador com credenciais válidas")
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

    @Test(description = "Deve cadastrar um usuário comum com credenciais válidas")
    public void cadastrarUsuarioComum() {
        usuarioComumId = given()
            .contentType("application/json")
            .log().all()
            .body(usuarioComumCriado)
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
    }

    @Test(
            description = "Deve listar o usuário cadastrado pelo id",
            dependsOnMethods = "cadastrarUsuarioAdmin"
    )
    public void listarUsuarioPorId() {
        given()
            .pathParam("id", usuarioId)
            .log().all()
        .when()
            .get("/usuarios/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("_id", equalTo(usuarioId))
            .body("nome", equalTo(usuarioCriado.getNome()))
            .body("email", equalTo(usuarioCriado.getEmail()))
            .body("password", equalTo(usuarioCriado.getPassword()))
            .body("administrador", equalTo(usuarioCriado.getAdministrador()))
            .body(matchesJsonSchemaInClasspath("schemas/usuario/listar-usuario-schema.json"));
    }

    @Test(
            description = "Deve pesquisar usuario cadastrado pelo nome",
            dependsOnMethods = "cadastrarUsuarioAdmin"
    )
    public void pesquisarUsuarioPorNome() {
        given()
            .queryParam("nome", usuarioCriado.getNome())
            .log().all()
        .when()
            .get("/usuarios")
        .then()
            .log().all()
            .statusCode(200)
            .body("usuarios._id", hasItem(usuarioId))
            .body("usuarios.nome", hasItem(usuarioCriado.getNome()))
            .body("usuarios.email", hasItem(usuarioCriado.getEmail()))
            .body("usuarios.password", hasItem(usuarioCriado.getPassword()))
            .body("usuarios.administrador", hasItem(usuarioCriado.getAdministrador()))
            .body(matchesJsonSchemaInClasspath("schemas/usuario/pesquisar-usuario-schema.json"));
    }

    @Test(
            description = "Deve editar o usuário já cadastrado",
            dependsOnMethods = "cadastrarUsuarioComum"
    )
    public void editarUsuario() {
        // cria um novo objeto com dados atualizados
        Usuario usuarioEditado = new Usuario(
            usuarioComumCriado.getNome() + " - Edição",
            usuarioComumCriado.getEmail() + ".br",
            usuarioComumCriado.getPassword() + "5",
            "true"
        );

        given()
            .contentType("application/json")
            .pathParam("id", usuarioComumId)
            .log().all()
            .body(usuarioEditado)
        .when()
            .put("/usuarios/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("message", equalTo(MSG_REGISTRO_ALTERADO))
            .body(matchesJsonSchemaInClasspath("schemas/usuario/editar-usuario-schema.json"));
    }

    @Test(
            description = "Deve excluir o usuário cadastrado pelo id",
            dependsOnMethods = "editarUsuarioInexistente"
    )
    public void excluirUsuario() {
        given()
            .pathParam("id", usuarioInexistenteId) // Vide cenários de exceção
            .log().all()
        .when()
            .delete("/usuarios/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("message", equalTo(MSG_REGISTRO_EXCLUIDO))
            .body(matchesJsonSchemaInClasspath("schemas/usuario/excluir-usuario-schema.json"));
    }

    /**
     * CENÁRIOS DE EXCEÇÃO
     */

    @Test(description = "Deve cadastrar o usuário inexistente")
    public void editarUsuarioInexistente() {
        // cria um novo objeto com dados novos
        String nomeUsuarioInexistente = UsuarioHelper.gerarUsuario();
        String emailInexistente = UsuarioHelper.gerarEmail();
        Usuario usuarioInexistente = new Usuario(nomeUsuarioInexistente, emailInexistente, senha, "false");

        usuarioInexistenteId = given()
            .contentType("application/json")
            .pathParam("id", gerarIdAleatorio())
            .log().all()
            .body(usuarioInexistente)
        .when()
            .put("/usuarios/{id}")
        .then()
            .log().all()
            .statusCode(201)
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .body(matchesJsonSchemaInClasspath("schemas/usuario/cadastrar-usuario-schema.json"))
            .extract()
            .path("_id");
    }

}
