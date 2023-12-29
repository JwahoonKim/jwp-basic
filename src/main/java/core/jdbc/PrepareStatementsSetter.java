package core.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PrepareStatementsSetter {
    void setParameters(PreparedStatement psmt) throws SQLException;
}
