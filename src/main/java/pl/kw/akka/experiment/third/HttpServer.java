package pl.kw.akka.experiment.third;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.IncomingConnection;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Created by Main_2 on 2017-05-15.
 */
public class HttpServer {

    public static void main(String[] args) throws IOException {
        ActorSystem actorSystem = ActorSystem.create();
        Materializer materializer = ActorMaterializer.create(actorSystem);

        Source<IncomingConnection, CompletionStage<ServerBinding>> serverSource = Http
                .get(actorSystem)
                .bind(ConnectHttp.toHost("localhost", 8081), materializer);

        final Function<HttpRequest, HttpResponse> requestHandler =
                new Function<HttpRequest, HttpResponse>() {

                    @Override
                    public HttpResponse apply(HttpRequest request) {
                        Uri uri = request.getUri();
                        if (request.method() == HttpMethods.GET) {
                            if (uri.path().equals("/")) {
                                return HttpResponse
                                        .create()
                                        .withEntity(
                                                ContentTypes.TEXT_HTML_UTF8,
                                                "<html><body>hello</body></html>");
                            } else if (uri.path().equals("/hello")) {
                                return HttpResponse
                                        .create()
                                        .withEntity("Hello " + UUID.randomUUID().toString());
                            } else if (uri.path().equals("/ping")) {
                                return HttpResponse.create().withEntity("Pong");
                            } else {
                                return NOT_FOUND;
                            }
                        } else {
                            return NOT_FOUND;
                        }
                    }

                    private final HttpResponse NOT_FOUND =
                            HttpResponse.create()
                                    .withStatus(404)
                                    .withEntity("Unknown resource!");
                };

        CompletionStage<ServerBinding> serverBindingFuture =
                serverSource.to(Sink.foreach(connection -> {
                    System.out.println("Accepted new connection from " + connection.remoteAddress());
                    connection.handleWithSyncHandler(requestHandler, materializer);
                })).run(materializer);

        System.out.println("ENTER to exit");
        System.in.read();
        actorSystem.terminate();
    }

    private static String fail() {
        throw new RuntimeException("BOOM");
    }
}
