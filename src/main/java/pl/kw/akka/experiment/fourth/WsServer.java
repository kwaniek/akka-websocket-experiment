package pl.kw.akka.experiment.fourth;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.model.ws.WebSocket;
import akka.japi.Function;
import akka.japi.JavaPartialFunction;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * Created by Main_2 on 2017-05-15.
 */
public class WsServer {

    public static void main(String[] args) throws Exception {
        ActorSystem actorSystem = ActorSystem.create();

        try {
            final Materializer materializer = ActorMaterializer.create(actorSystem);
            Function<HttpRequest, HttpResponse> handler = request -> handleRequest(request);

            CompletionStage<ServerBinding> serverBindingFuture = Http
                    .get(actorSystem)
                    .bindAndHandleSync(
                            handler,
                            ConnectHttp.toHost("localhost", 8081),
                            materializer
                    );

            serverBindingFuture.toCompletableFuture().get(1, TimeUnit.SECONDS);
            System.out.println("ENTER to exit");
            System.in.read();
        } finally {
            actorSystem.terminate();
        }
    }

    public static HttpResponse handleRequest(HttpRequest request) {
        System.out.println("Handling request to " + request.getUri());
        if (request.getUri().path().equals("/greeter")) {
            Flow<Message, Message, NotUsed> greeterFlow = greeter();
            return WebSocket.handleWebSocketRequestWith(request, greeterFlow);
        } else {
            return HttpResponse.create().withStatus(404);
        }
    }

    public static Flow<Message, Message, NotUsed> greeter() {
        return Flow
                .<Message>create()
                .collect(new JavaPartialFunction<Message, Message>() {
                    @Override
                    public Message apply(Message x, boolean isCheck) throws Exception {
                        if (isCheck) {
                            if (x.isText()) {
                                return null;
                            } else {
                                throw noMatch();
                            }
                        } else {
                            return handleTextMessage(x.asTextMessage());
                        }
                    }
                });
    }

    public static TextMessage handleTextMessage(TextMessage msg) {
        if (msg.isStrict()) {
            return TextMessage.create("Hello " + msg.getStrictText());
        } else {
            return TextMessage.create(Source.single("Hello ").concat(msg.getStreamedText()));
        }
    }
}
