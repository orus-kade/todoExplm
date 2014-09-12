package org.apache.http.client.methods;

import java.net.URI;

public class HttpPatch extends HttpPost {
    @Override
    public String getMethod() {
        return "PATCH";
    }

    public HttpPatch() {
        super();
    }

    public HttpPatch(URI uri) {
        super(uri);
    }

    public HttpPatch(String url) {
        super(url);
    }
}
