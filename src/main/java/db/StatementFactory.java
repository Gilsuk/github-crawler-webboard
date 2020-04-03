package db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class StatementFactory implements AutoCloseable {
	
	private static final String sqlPath = "src/main/resources/sqls.xml";
	private final Connection connection;
	private final Map<String, PreparedStatement> statements;

	public StatementFactory(Connection connection) {
		this.connection = connection;
		this.statements = new HashMap<>();
	}
	
	public PreparedStatement getStatement(String statementId) throws SQLException {
		
		PreparedStatement statement = statements.get(statementId);
		
		if (statement == null || statement.isClosed()) {
			String sql = readSqlfromXML(statementId);
			statement = connection.prepareStatement(sql);
			statements.put(statementId, statement);
		}
		
		return statement;
	}

	private String readSqlfromXML(String statementId) {
		Document document = null;
		
		try (InputStream stream = new BufferedInputStream(new FileInputStream(new File(sqlPath)))){
			document = Jsoup.parse(stream, "UTF-8", sqlPath);
		} catch (IOException e) { e.printStackTrace(); }

		Element elem = document.getElementById(statementId);
		return elem.text();
	}

	@Override
	public void close() throws Exception {
		statements.forEach((id, stmt) -> {
			try {
				if (stmt != null && !stmt.isClosed())
					stmt.close();
			} catch (Exception e) { e.printStackTrace(); }
		});
	}
}
