import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

public class HeaderHttpResponseFiler implements HttpResponseFilter {
    public void filter(HttpResponse response) {
        response.headers().set("Chauncy","filter-test");
    }
}
