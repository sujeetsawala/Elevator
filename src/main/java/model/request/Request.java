package model.request;

import lombok.Data;

@Data
public abstract class Request {
    private Type type;

    public Request() {
    }

    public Request(final Type type) {
        this.type = type;
    }
}
