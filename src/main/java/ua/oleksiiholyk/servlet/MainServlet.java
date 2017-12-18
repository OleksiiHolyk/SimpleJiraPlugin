package ua.oleksiiholyk.servlet;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.oleksiiholyk.model.OperationResponseJSON;

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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApplicationUser user = getCurrentUser();
        IssueService issueService = ComponentAccessor.getIssueService();
        String reqKeys = req.getParameter("key");

        String[] reqStrArr = reqKeys.split(",");
        Long[] reqLongArr = new Long[reqStrArr.length];

        JSONObject responseObjectJSON = new JSONObject();
        JSONArray issuesJSON = new JSONArray();
        if (reqStrArr.length >= 1) {
            for (String aReqStrArr : reqStrArr) {
                OperationResponseJSON orj = new OperationResponseJSON();

                IssueService.IssueResult issue = issueService.getIssue(user, Long.valueOf(aReqStrArr));
                orj.setUserId(issue.getIssue().getId());

                if (issue.isValid()) {
                    IssueService.DeleteValidationResult result = issueService.validateDelete(user, issue.getIssue().getId());
                    if (result.getErrorCollection().hasAnyErrors()) {
                        orj.setOperationStatus(false);
                        orj.setOperationError(result.getErrorCollection().getErrors().get(0));
                    } else {
                        issueService.delete(user, result);
                        orj.setOperationStatus(true);
                        orj.setOperationError("");
                    }
                } else {
                    orj.setOperationStatus(false);
                    orj.setOperationError("Couldn't find issue");
                }
                issuesJSON.put(orj.toJSON());
            }
        } else {
            System.out.println("nothing");
        }
        try {
            responseObjectJSON.put("DeleteOperation", issuesJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write(responseObjectJSON.toString());
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