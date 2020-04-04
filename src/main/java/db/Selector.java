package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import controller.Category;
import model.Repo;

public class Selector implements AutoCloseable {
	
	private Connection connection;
	private StatementFactory factory;
	
	public Selector() {
		this.connection = new Connector().connect();
		this.factory = new StatementFactory(connection);
	}

	public int getTotalCount() {
		return getCount("selectTotalCount");
	}

	public int getTotalCount(String category, String keyword) {
		switch (Category.valueOf(category)) {
		case desc:
			return getCount("selectTotalCountSearchByDesc", "%" + keyword + "%");
		case lang:
			return getCount("selectTotalCountSearchByLang", keyword);
		case tag:
			return getCount("selectTotalCountSearchByTag", keyword);
		case title:
			return getCount("selectTotalCountSearchByTitle", "%" + keyword + "%");
		default:
			return 0;
		}
	}

	@Override
	public void close() throws Exception {
		factory.close();
		connection.close();
	}

	public List<Repo> getRepos(int limit, int offset) {
		List<Repo> list = selectList("selectRepos", x -> {
			try {
				x.setInt(1, limit);
				x.setInt(2, offset);
			} catch (SQLException e) { e.printStackTrace(); }
		}, this::makeRepo);
		
		completeRepos(list);

		return list;
	}

	public List<Repo> getRepos(String category, String keyword, int limit, int offset) {
		List<Repo> list;
		switch (Category.valueOf(category)) {
		case desc:
			list = getPartialRepos("selectReposByDesc", "%" + keyword + "%", limit, offset);
			break;
		case lang:
			list = getPartialRepos("selectReposByLang", keyword, limit, offset);
			break;
		case tag:
			list = getPartialRepos("selectReposByTag", keyword, limit, offset);
			break;
		case title:
			list = getPartialRepos("selectReposByTitle", "%" + keyword + "%", limit, offset);
			break;
		default:
			list = new ArrayList<Repo>();
		}
		completeRepos(list);
		return list;
	}

	private List<Repo> getPartialRepos(String statementId, String keyword, int limit, int offset) {
		return selectList(statementId, x -> {
			try {
				x.setString(1, keyword);
				x.setInt(2, limit);
				x.setInt(3, offset);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, this::makeRepo);
	}

	private void completeRepos(List<Repo> list) {
		for (Repo repo : list) {
			fillTheField(repo, repo::setTags, this::getNames, "selectTagsById");
			fillTheField(repo, repo::setLangs, this::getNames, "selectLangsById");
		}
	}

	private <T> void fillTheField (Repo repo, Consumer<T> consumer,
			BiFunction<Repo, String, T> function, String statementId) {
		consumer.accept(function.apply(repo, statementId));
	}

	private List<String> getNames(Repo repo, String statementId) {
		return selectList(statementId, x -> {
			try {
				x.setInt(1, repo.getId());
			} catch (SQLException e) { e.printStackTrace(); }
		}, this::getName);
	}

	public int getCount(String statementId) {
		return getCount(statementId, (x) -> {} );
	}

	private int getCount(String statementId, String keyword) {
		return getCount(statementId, x -> {
			try { x.setString(1, keyword);
			} catch (SQLException e) { e.printStackTrace(); }
		} );
	}

	public int getCount(String statementId, Consumer<PreparedStatement> setter) {
		return select(statementId, setter, (resultSet) -> {
			try { return resultSet.getInt(1);
			} catch (SQLException e) { return 0; }
		});
	}

	private <T> T select(
			String statementId,
			Consumer<PreparedStatement> setter,
			Function<ResultSet, T> getter
			) {
		try {
			ResultSet resultSet = getResultSet(statementId, setter);
			while(resultSet.next())
				return getter.apply(resultSet);
		} catch (SQLException e) { }
		return null;
	}
	
	private <T> List<T> selectList(
			String statementId,
			Consumer<PreparedStatement> stateSetter,
			Function<ResultSet, T> getter
			) {
		List<T> list = new ArrayList<T>();
		try {
			ResultSet resultSet = getResultSet(statementId, stateSetter);
			while(resultSet.next())
				list.add(getter.apply(resultSet));
		} catch (SQLException e) { }
		
		return list;
	}

	private ResultSet getResultSet(String statementId, Consumer<PreparedStatement> stateSetter) throws SQLException {
		PreparedStatement statement = factory.getStatement(statementId);
		stateSetter.accept(statement);
		ResultSet resultSet = statement.executeQuery();
		return resultSet;
	}

	private Repo makeRepo(ResultSet resultSet) {
		Repo repo = new Repo();
		try {
			repo.setId(resultSet.getInt("id"));
			repo.setStar(resultSet.getInt("star"));
			repo.setTitle(resultSet.getString("title"));
			repo.setDescription(resultSet.getString("description"));
		} catch (SQLException e) { e.printStackTrace(); }
		return repo;
	}

	private String getName(ResultSet resultSet) {
		try {
			return resultSet.getString("name");
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}



}
