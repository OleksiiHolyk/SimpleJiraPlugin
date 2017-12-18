package ua.oleksiiholyk.servlet;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MainServlet.class);

    @ComponentImport
    private final TemplateRenderer renderer;

    private static final String TABLE_TEMPLATE = "/templates/table.vm";

    public MainServlet(TemplateRenderer renderer) {
        this.renderer = renderer;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Issue> issues = getIssues(getCurrentUser());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("issues", issues);

        resp.setContentType("text/html;charset=utf-8");
        renderer.render(TABLE_TEMPLATE, context, resp.getWriter());
    }

    private List<Issue> getIssues(ApplicationUser applicationUser) {
        PagerFilter pagerFilter = PagerFilter.getUnlimitedFilter();
        JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
        SearchService searchService = ComponentAccessor
                .getComponentOfType(SearchService.class);
        com.atlassian.query.Query query = jqlClauseBuilder.project("TUTORIAL").buildQuery();
        com.atlassian.jira.issue.search.SearchResults searchResults = null;
        try {
            searchResults = searchService.search(applicationUser, query, pagerFilter);
        } catch (SearchException e) {
            e.printStackTrace();
        }
        return searchResults.getIssues();
    }

    private ApplicationUser getCurrentUser() {
        return ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
    }

}