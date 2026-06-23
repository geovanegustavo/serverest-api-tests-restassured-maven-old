package serverest.util;

import java.util.UUID;
import serverest.model.Produto;

public class ProdutoHelper {

    public static Produto gerarProdutoAleatorio() {
        String nome = "Produto_" + UUID.randomUUID().toString().substring(0, 8);
        int preco = (int) (Math.random() * 500) + 50; // preço entre 50 e 550
        String descricao = "Descrição aleatória para " + nome;
        int quantidade = (int) (Math.random() * 100) + 1; // quantidade entre 1 e 100

        return new Produto(nome, preco, descricao, quantidade);
    }
}
