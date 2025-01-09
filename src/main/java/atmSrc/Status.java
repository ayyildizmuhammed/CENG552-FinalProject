package atmSrc;

public class Status {

    private boolean success;
    private String errorCode;

    public Status(boolean success, String errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return success ? "SUCCESS" : "ERROR: " + errorCode;
    }
}
