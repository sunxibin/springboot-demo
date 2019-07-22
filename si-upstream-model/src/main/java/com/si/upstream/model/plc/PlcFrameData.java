package com.si.upstream.model.plc;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.core.style.ToStringCreator;


@JsonInclude
public final class PlcFrameData<T> {

    private int type;
    private T body;

    public static <T> PlcFrameData<T> build(int type, T body) {
        return new PlcFrameData().setType(type).setBody(body);
    }

    public int getType() {
        return type;
    }

    public PlcFrameData setType(int type) {
        this.type = type;
        return this;
    }

    public T getBody() {
        return body;
    }

    public PlcFrameData setBody(T body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("type", type)
                .append("body", body.toString())
                .toString();
    }
}
