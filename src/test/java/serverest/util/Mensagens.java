package serverest.util;

public class Mensagens {

    public static final String MSG_CADASTRO_SUCESSO = "Cadastro realizado com sucesso";
    public static final String MSG_REGISTRO_ALTERADO = "Registro alterado com sucesso";
    public static final String MSG_REGISTRO_EXCLUIDO = "Registro excluído com sucesso";
    public static final String MSG_TOKEN_INVALIDO = "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais";

    // LOGIN
    public static final String MSG_LOGIN_SUCESSO = "Login realizado com sucesso";

    // PRODUTO
    public static final String MSG_PRODUTO_EXISTENTE = "Já existe produto com esse nome";
    public static final String MSG_PRODUTO_NAO_ENCONTRADO = "Produto não encontrado";

    private Mensagens() {
        // construtor privado para evitar instância
    }
}
