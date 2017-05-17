package pl.kw.akka.experiment.fifth;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.WebSocket;
import akka.japi.Function;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * Created by Main_2 on 2017-05-17.
 */
public class ChatServer {

    public static void main(String[] args) throws Exception {

        ActorSystem actorSystem = ActorSystem.create();
        try {
            final Materializer materializer = ActorMaterializer.create(actorSystem);
            Function<HttpRequest, HttpResponse> handler = ChatServer::handleRequest;

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
        if (request.getUri().path().startsWith("ws-chat")) {
            Flow<Message, Message, NotUsed> chatFlow = chatFlow();
            return WebSocket.handleWebSocketRequestWith(request, chatFlow);
        } else {
            return HttpResponse.create().withStatus(404);
        }
    }
}
