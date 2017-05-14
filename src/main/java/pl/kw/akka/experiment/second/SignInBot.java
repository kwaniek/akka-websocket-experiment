package pl.kw.akka.experiment.second;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.Optional;

/**
 * Created by Main_2 on 2017-05-14.
 */
public class SignInBot extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ValidateInput.class, this::onValidateInput)
                .build();
    }

    private void onValidateInput(ValidateInput validateInput) {
        System.out.println("Validating " + Optional.of(validateInput.value));
        getSender().tell(new ConnectedBot.SignedIn(validateInput.value), ActorRef.noSender());
    }

    public static class ValidateInput {
        public final String value;

        public ValidateInput(String value) {
            this.value = value;
        }
    }
}
