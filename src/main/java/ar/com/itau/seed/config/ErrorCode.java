package ar.com.itau.seed.config;

public enum ErrorCode {

    INTERNAL_ERROR(100, "Internal server error"),
    BAD_REQUEST(101, "Bad request"),
    RESOURCE_NOT_FOUND(102, "Not found"),
    DOCUMENT_TYPE_NOT_FOUND(103, "Document type not found"),
    UNIQUE_ID_TYPE_NOT_FOUND(104, "UID type not found");

    private final int value;
    private final String reasonPhrase;

    ErrorCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}
