package serverest.tests;

import io.restassured.RestAssured;
import org.testng.annotations.Test;
import serverest.model.Produto;
import serverest.util.TokenHolder;
import serverest.util.ProdutoHelper;

import static org.hamcrest.Matchers.*;
import static serverest.util.Mensagens.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProdutoTest {

    static {
        RestAssured.baseURI = "https://serverest.dev";
    }

    String produtoId;
    Produto produtoCriado = ProdutoHelper.gerarProdutoAleatorio();

    @Test(
            priority = 1,
            description = "Deve cadastrar um produto aleatório na base de dados",
            groups = {"produto", "sucesso"}
    )
    public void cadastrarProduto() {
        produtoId = given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + TokenHolder.token)
            .log().all()
            .body(produtoCriado)
        .when()
            .post("/produtos")
        .then()
            .log().all()
            .statusCode(201)
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .body("_id", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/produto/cadastrar-produto-schema.json"))
            .extract()
            .path("_id");
    }

    @Test(
            priority = 2,
            description = "NÃO deve cadastrar um produto já existente na base de dados",
            dependsOnMethods = "cadastrarProduto",
            groups = {"produto", "exceção"}
    )
    public void cadastrarProdutoExistente() {
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + TokenHolder.token)
                .log().all()
                .body(produtoCriado)
                .when()
                .post("/produtos")
                .then()
                //.log().ifValidationFails()
                .log().all()
                .statusCode(400)
                .body("message", equalTo(MSG_PRODUTO_EXISTENTE))
                .body(matchesJsonSchemaInClasspath("schemas/produto/cadastrar-produto-cadastrado-schema.json"));
    }

    @Test(
            priority = 3,
            description = "Deve listar o produto cadastrado pelo id",
            dependsOnMethods = "cadastrarProduto",
            groups = {"produto", "sucesso"}
    )
    public void listarProdutoPorId() {
        given()
            .header("Authorization", "Bearer " + TokenHolder.token)
            .pathParam("id", produtoId)
            .log().all()
        .when()
            .get("/produtos/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("_id", equalTo(produtoId))
            .body("nome", equalTo(produtoCriado.getNome()))
            .body("preco", equalTo(produtoCriado.getPreco()))
            .body("descricao", equalTo(produtoCriado.getDescricao()))
            .body("quantidade", equalTo(produtoCriado.getQuantidade()))
            .body(matchesJsonSchemaInClasspath("schemas/produto/listar-produto-schema.json"));
    }

    @Test(
            priority = 4,
            description = "Deve pesquisar produto cadastrado pelo nome",
            dependsOnMethods = "cadastrarProduto",
            groups = {"produto", "sucesso"}
    )
    public void pesquisarProdutoPorNome() {
        given()
            .header("Authorization", "Bearer " + TokenHolder.token)
            .queryParam("nome", produtoCriado.getNome())
            .log().all()
        .when()
            .get("/produtos")
        .then()
            .log().all()
            .statusCode(200)
            //.body("produtos[0]._id", equalTo(produtoId))
            .body("produtos._id", hasItem(produtoId))
            .body("produtos.nome", hasItem(produtoCriado.getNome()))
            .body("produtos.preco", hasItem(produtoCriado.getPreco()))
            .body("produtos.descricao", hasItem(produtoCriado.getDescricao()))
            .body("produtos.quantidade", hasItem(produtoCriado.getQuantidade()))
            .body(matchesJsonSchemaInClasspath("schemas/produto/pesquisar-produto-schema.json"));
    }

    @Test(
            priority = 5,
            description = "Deve editar o produto já cadastrado",
            dependsOnMethods = "listarProdutoPorId",
            groups = {"produto", "sucesso"}
    )
    public void editarProduto() {
        // cria um novo objeto com dados atualizados
        Produto produtoEditado = new Produto(
        produtoCriado.getNome() + " - Edição",
        produtoCriado.getPreco() + 50,
        produtoCriado.getDescricao() + " (atualizado)",
        produtoCriado.getQuantidade() + 10
        );

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + TokenHolder.token)
            .pathParam("id", produtoId)
            .log().all()
            .body(produtoEditado)
        .when()
            .put("/produtos/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("message", equalTo(MSG_REGISTRO_ALTERADO))
            .body(matchesJsonSchemaInClasspath("schemas/produto/editar-produto-schema.json"));
    }

    @Test(
            priority = 6,
            description = "Deve excluir o produto cadastrado pelo id",
            dependsOnMethods = "editarProduto",
            groups = {"produto", "sucesso"}
    )
    public void excluirProduto() {
        given()
            .header("Authorization", "Bearer " + TokenHolder.token)
            .pathParam("id", produtoId)
            .log().all()
        .when()
            .delete("/produtos/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("message", equalTo(MSG_REGISTRO_EXCLUIDO))
            .body(matchesJsonSchemaInClasspath("schemas/produto/excluir-produto-schema.json"));
    }

    /**
     * CENÁRIOS DE EXCEÇÃO
     */

    @Test(
            priority = 7,
            description = "NÃO deve encontrar produto já excluído",
            dependsOnMethods = "excluirProduto",
            groups = {"produto", "exceção"}
    )
    public void listarProdutoExcluido() {
        given()
            .header("Authorization", "Bearer " + TokenHolder.token)
            .pathParam("id", produtoId)
            .log().all()
        .when()
            .get("/produtos/{id}")
        .then()
            .log().all()
            .statusCode(400)
            .body("message", equalTo(MSG_PRODUTO_NAO_ENCONTRADO))
            .body(matchesJsonSchemaInClasspath("schemas/produto/listar-produto-excluido-schema.json"));
    }

    @Test(
            priority = 8,
            description = "NÃO deve cadastrar um produto sem token de autenticação",
            dependsOnMethods = "cadastrarProduto",
            groups = {"produto", "exceção"}
    )
    public void cadastrarProdutoSemToken() {
        given()
            .contentType("application/json")
            //.header("Authorization", "Bearer " + TokenHolder.token)
            .log().all()
            .body(produtoCriado)
            .when()
            .post("/produtos")
            .then()
            //.log().ifValidationFails()
            .log().all()
            .statusCode(401)
            .body("message", equalTo(MSG_TOKEN_INVALIDO))
            .body(matchesJsonSchemaInClasspath("schemas/produto/cadastrar-produto-sem-token-schema.json"));
    }

}
