package methods;

import org.apache.http.client.methods.HttpPost;

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
