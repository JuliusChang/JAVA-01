import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

public interface HttpResponseFilter {
    void filter(HttpResponse response);
}
