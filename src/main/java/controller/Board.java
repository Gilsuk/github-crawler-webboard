package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.Selector;
import model.Paging;
import model.Repo;

@WebServlet("/board")
public class Board extends HttpServlet {
	private static final long serialVersionUID = 1930978531009696891L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (isValidQuery(req))
			renderSearchPage(req, resp);
		else
			renderNormalPage(req, resp);
	}

	private void renderNormalPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Selector selector = new Selector();
		int curPage = getCurPage(req);
		int total = selector.getTotalCount();
		
		Paging paging = new Paging(total, curPage);
		int limit = paging.getListCount();
		int offset = paging.getStartNo() - 1;
		
		List<Repo> list = selector.getRepos(limit, offset);
		
		forward(req, resp, paging, list);
		try { selector.close();
		} catch (Exception e) { e.printStackTrace(); }
	}

	private void renderSearchPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Selector selector = new Selector();
		int curPage = getCurPage(req);
		String category = getSearchCategory(req);
		String keyword = getSearchKeyword(req);
		String queryStr = getQueryString(category, keyword);
		int total = selector.getTotalCount(category, keyword);

		Paging paging = new Paging(total, curPage);
		int limit = paging.getListCount();
		int offset = paging.getStartNo() - 1;

		List<Repo> list = selector.getRepos(category, keyword, limit, offset);
		forward(req, resp, paging, list, queryStr);
		try { selector.close();
		} catch (Exception e) { e.printStackTrace(); }
	}


	private void forward(HttpServletRequest req, HttpServletResponse resp, Paging paging, List<Repo> list,
			String queryStr) throws ServletException, IOException {
		req.setAttribute("queryStr", queryStr);
		req.setAttribute("paging", paging);
		req.setAttribute("list", list);
		req.getRequestDispatcher("/WEB-INF/board.jsp").forward(req, resp);
	}

	private void forward(HttpServletRequest req, HttpServletResponse resp, Paging paging, List<Repo> list)
			throws ServletException, IOException {
		forward(req, resp, paging, list, "");
	}

	private boolean isValidQuery(HttpServletRequest req) {
		if (getSearchKeyword(req) == null) return false;
		if (isValidCategory(getSearchCategory(req))) return true;
		return false;
	}

	private boolean isValidCategory(String category) {
		if (category == null) return false;
		
		for(String param : new String[]{"title", "desc", "lang", "tag"})
			if (param.equals(category)) return true;

		return false;
	}

	private String getSearchCategory(HttpServletRequest req) {
		return req.getParameter("category");
	}

	private String getSearchKeyword(HttpServletRequest req) {
		return req.getParameter("search");
	}

	private String getQueryString(String category, String keyword) {
		return new StringBuilder()
				.append("&category=")
				.append(category)
				.append("&search=")
				.append(keyword)
				.toString();
	}

	private int getCurPage(HttpServletRequest req) {
		try {
			return Integer.parseInt(req.getParameter("curPage"));
		} catch (Exception e) {
			return 1;
		}
	}

}
