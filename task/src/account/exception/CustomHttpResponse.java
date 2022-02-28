package account.exception;

public class CustomHttpResponse {
    private String timestamp; //       "timestamp": "2021-12-12T09:28:54.267+00:00",
    private int status;//        "status": 403,
    private String error;//        "error": "Forbidden",
    private String message;//        "message": "Forbidden",
    private String path;//        "path": "/api/admin/user/role"

    public CustomHttpResponse() {}
    private CustomHttpResponse(String timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public static class Builder {
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        Builder(){}

        public Builder setTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        Builder setError(String error) {
            this.error = error;
            return this;
        }

        Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public CustomHttpResponse build() {
            return new CustomHttpResponse(timestamp, status, error, message, path);
        }
    }

    @Override
    public String toString() {
        return "{" + "\n" +
                    "\"timestamp\"" + ":" + "\"" + timestamp + "\"" + ",\n" +
                    "\"status\"" + ":" + status + ",\n" +
                    "\"error\"" + ":" + "\"" + error + "\"" + ",\n" +
                    "\"message\"" + ":" + "\"" + message + "\"" + ",\n" +
                    "\"path\"" + ":" + "\"" + path + "\"" + "\n" +
                "}";
    }
}
