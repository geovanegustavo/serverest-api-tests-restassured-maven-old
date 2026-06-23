package serverest.util;

import java.security.SecureRandom;

public class IdHelper {

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TAMANHO_PADRAO = 16;
    private static final SecureRandom random = new SecureRandom();

    public static String gerarIdAleatorio() {
        StringBuilder resultado = new StringBuilder(TAMANHO_PADRAO);
        for (int i = 0; i < TAMANHO_PADRAO; i++) {
            int index = random.nextInt(CARACTERES.length());
            resultado.append(CARACTERES.charAt(index));
        }
        return resultado.toString();
    }

    // Exemplo de uso
    public static void main(String[] args) {
        String idUsuarioInexistente = gerarIdAleatorio();
        System.out.println("ID gerado: " + idUsuarioInexistente);
    }
}
