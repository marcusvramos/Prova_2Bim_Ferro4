package ramos.vinicius.marcus.Service;

public class ErrorResponse {
    private ErrorDetail error;

    public ErrorDetail getError() {
        return error;
    }

    public class ErrorDetail {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
