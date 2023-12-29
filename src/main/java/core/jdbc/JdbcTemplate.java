package core.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    public void executeUpdate(String sql, PrepareStatementsSetter pss) {
        try {
            try (Connection con = ConnectionManager.getConnection(); PreparedStatement psmt = con.prepareStatement(sql)) {
                pss.setParameters(psmt);
                psmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeUpdate(String sql, Object... parameters) {
        executeUpdate(sql, createPrepareStatementSetter(parameters));
    }

    public <T> T executeQuery(String sql, RowMapper<T> rm, PrepareStatementsSetter pss) {
        List<T> list = executeListQuery(sql, rm, pss);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public <T> T executeQuery(String sql, RowMapper<T> rm, Object... parameters) {
        return executeQuery(sql, rm, createPrepareStatementSetter(parameters));
    }

    public <T> List<T> executeListQuery(String sql, RowMapper<T> rm, PrepareStatementsSetter pss) {
        ResultSet rs;
        try {
            try (Connection con = ConnectionManager.getConnection(); PreparedStatement psmt = con.prepareStatement(sql)) {
                pss.setParameters(psmt);
                rs = psmt.executeQuery();
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rm.mapRow(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PrepareStatementsSetter createPrepareStatementSetter(Object[] parameters) {
        return psmt -> {
            for (int i = 0; i < parameters.length; i++) {
                psmt.setObject(i + 1, parameters[i]);
            }
        };
    }
}
