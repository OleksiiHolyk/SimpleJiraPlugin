package ua.oleksiiholyk.model;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;

/**
 * Created by Oleksii on 18.12.2017.
 */
public class OperationResponseJSON {
//                respStr = "{ \"success\" : \"false\", error: \"Couldn't find issue\"}";
//                respStr = "{ \"success\": \"false\", error: \"" + result.getErrorCollection().getErrors().get(0) + "\" }";

    Long userId;
    boolean operationStatus;
    String operationError;


    public boolean isOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(boolean operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getOperationError() {
        return operationError;
    }

    public void setOperationError(String operationError) {
        this.operationError = operationError;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", this.getUserId());
            jsonObject.put("operationStatus", this.isOperationStatus());
            jsonObject.put("operationError", this.getOperationError());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        return "OperationResponseJSON{" +
                "userId=" + userId +
                ", operationStatus=" + operationStatus +
                ", operationError='" + operationError + '\'' +
                '}';
    }
}
