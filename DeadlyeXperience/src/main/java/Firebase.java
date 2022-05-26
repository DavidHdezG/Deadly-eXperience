import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Firebase {
    public static Firebase obj;

    private Firestore db;

    private Firebase() throws IOException {
        connect();
    }
    public void connect() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/firebaseData.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fmr-01-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        db= FirestoreClient.getFirestore();
    }

    public ArrayList<Score> runQuery() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection("GameData").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents=querySnapshot.getDocuments();
        ArrayList<Score> list = new ArrayList<>();

        String player;
        String accAvg;
        String zombieTime;
        String totalAccAvg;
        String totalZombieTime;

        for (QueryDocumentSnapshot document:
             documents) {
            player = document.getString("player");
            accAvg=document.getString("accAvg");
            zombieTime=document.getString("zombieTime");
            totalAccAvg=document.getString("totalAccAvg");
            totalZombieTime=document.getString("totalZombieTime");
            list.add(new Score(player,accAvg,zombieTime,totalAccAvg,totalZombieTime));
        }
        return list;
    }

    public static Firebase getDB() throws IOException {
        if (obj==null){
            obj=new Firebase();
        }
        return obj;
    }

    public void insert(String player, double accAvg, double zombieTime,double totalAccAvg,double totalZombieTime) throws ExecutionException, InterruptedException {
        Map<String, String> data = new HashMap();
        data.put("player",player);
        data.put("accAvg",Double.toString(accAvg));
        data.put("zombieTime",Double.toString(zombieTime));
        data.put("totalAccAvg",Double.toString(totalAccAvg));
        data.put("totalZombieTime",Double.toString(totalZombieTime));
        CollectionReference docRef=db.collection("GameData");
        ApiFuture<DocumentReference> result = docRef.add(data);
    }
}
class Score{
    String player;
    String accAvg;
    String zombieTime;
    String totalAccAvg;
    String totalZombieTime;
    public Score(String player, String accAvg, String zombieTime, String totalAccAvg, String totalZombieTime) {
        this.player = player;
        this.accAvg = accAvg;
        this.zombieTime = zombieTime;
        this.totalAccAvg = totalAccAvg;
        this.totalZombieTime = totalZombieTime;
    }
}