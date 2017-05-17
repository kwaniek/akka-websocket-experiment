package pl.kw.akka.experiment.fifth;

import akka.actor.ActorSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Main_2 on 2017-05-17.
 */
public class ChatRooms {

    private static final Map<String, SimpleChatRoom> allRooms = new HashMap<>();

    public static SimpleChatRoom findOrCreate(ActorSystem actorSystem, String uuid) {
        if (allRooms.containsKey(uuid)) {
            return allRooms.get(uuid);
        } else {
            String newUid = UUID.randomUUID().toString();
            SimpleChatRoom room = new SimpleChatRoom(actorSystem, newUid);
            allRooms.put(newUid, room);
            return room;
        }
    }
}
