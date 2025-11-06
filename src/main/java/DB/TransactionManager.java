package DB;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    @FunctionalInterface
    public interface TransactionCallable<T> {
        T call(Connection conn) throws Exception;
    }

    public <T> T execute(TransactionCallable<T> work) throws Exception {
        try (Connection conn = ConexionDB.getConnection()) {
            boolean previousAuto = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                T result = work.call(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback fallido: " + ex.getMessage());
                }
                throw e;
            } finally {
                try {
                    conn.setAutoCommit(previousAuto);
                } catch (SQLException ex) {
                    System.err.println("No se pudo restaurar autoCommit: " + ex.getMessage());
                }
            }
        }
    }
}
