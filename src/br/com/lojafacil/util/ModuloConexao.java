package br.com.lojafacil.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ModuloConexao {

    // --- Constantes para a Conexão (Futuramente virão de um arquivo de propriedades) ---
    private static final String DB_NAME = "lojafacil"; // Nome do seu banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String USER = "root";
    private static final String PASSWORD = "Magalhaes@1990"; // <-- Coloque sua senha aqui, se houver

    /**
     * Estabelece e retorna uma conexão com o banco de dados.
     * * @return um objeto Connection com a conexão estabelecida.
     * @throws SQLException se ocorrer um erro ao tentar conectar.
     */
    public static Connection conector() throws SQLException {
        try {
         
            // Tenta estabelecer a conexão e a retorna.
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {
            // <-- MUDANÇA 2: Removemos os System.out.println.
            // A classe que chama este método (como o DAO) será responsável por tratar a exceção.
            // Re-lançamos a exceção para notificar sobre o erro.
            throw new SQLException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
    
}
