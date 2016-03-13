package hochschuledarmstadt.photostream_tools.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HttpResult {

    public HttpResult(int responseCode, String message){
        this.responseCode = responseCode;
        this.message = message;
    }

    @SerializedName("response_code")
    @Expose
    private Integer responseCode;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     *
     * @return
     * The responseCode
     */
    public Integer getResponseCode() {
        return responseCode;
    }

    /**
     *
     * @param responseCode
     * The response_code
     */
    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
