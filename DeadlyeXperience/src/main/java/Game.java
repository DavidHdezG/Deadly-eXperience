import ketai.net.KetaiNet;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;
import uibooster.UiBooster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Game extends PApplet {
    boolean connected=false;
    PImage background, menuBackground;
    boolean shooted = false;
    double healthAux = 0;
    Firebase db;
    ArrayList<Enemy> enemies = new ArrayList<>();
    PVector center;
    PImage scope, playBtn, exitBtn, scoresBtn, quitBtn;
    Random rand = new Random();
    OscP5 oscP5;
    NetAddress remoteLocation;
    String myIPAddress;
    String remoteAddress;
    int totalShoots = 0;
    int niceShoot = 0;
    int nEnemies = 5;
    double damage = 4;
    double speed = 3;
    float gyroscopeX, gyroscopeY, gyroscopeZ, p;
    float scopeX, scopeY;
    Avatar avatar;
    boolean hitted = false;
    int menu = 0;
    boolean scoreWindow=false;
    ArrayList<Float> kills;
    ArrayList<Double> killTimeAvrg;
    ArrayList<Double> accuracyAvrg;
    Timer roundTime;
    boolean roundTimeB=false;

    @Override
    public void settings() {
        fullScreen();
    }

    @Override
    public void setup() {
        frameRate(60);
        rectMode(CENTER);
        background = loadImage("back.png");
        menuBackground = loadImage("main_menu.png");
        playBtn = loadImage("playBtn.png");
        exitBtn = loadImage("exitBtn.png");
        scoresBtn = loadImage("scoresBtn.png");
        quitBtn = loadImage("quitBtn.png");
        avatar = new Avatar();
        healthAux = avatar.health;
        kills = new ArrayList<>();
        killTimeAvrg = new ArrayList<>();
        accuracyAvrg = new ArrayList<>();
        try {
            db = Firebase.getDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scope = loadImage("scope.png");
        center = new PVector(width / 2, height / 2);
        resetWave();
    }

    @Override
    public void draw() {
        switch (menu) {
            case 0 -> {
                menuScreen();
                return;
            }
            case 1 -> {
                gameScreen();
                return;
            }
            case 2 -> {
                try {
                    gameOver();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
    }

    void gameScreen() {
        if(!roundTimeB){
            roundTime=new Timer();
            roundTime.start();
            roundTimeB=true;
        }

        if (p == 1 && scopeX > 0 && scopeX < 150 && scopeY > 0 && scopeY < 51) {
            menu = 2;
        }
        if (healthAux > 0.0) {

            image(background, 0, 0, width, height);
            image(quitBtn, 0, 0, 300, 51);
            fill(255);
            textSize(20);
            text("Life: " + healthAux, width / 2, 30);
            noCursor();
            avatar.drawAvatar();
            for (Enemy enemy : enemies) {
                enemy.update();
            }
            try {
                drawScope();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            menu = 2;
        }
        shoot();
        waves();
    }

    public void gameOver() throws ExecutionException, InterruptedException {
        String player = null;
        double accuracyAverage = 0;
        if (!accuracyAvrg.isEmpty()) {
            for (Double acc :
                    accuracyAvrg) {
                accuracyAverage += acc;
            }
            accuracyAverage = accuracyAverage / accuracyAvrg.size();
        }

        double zombieTime = 0;
        if (!killTimeAvrg.isEmpty()) {
            for (Double t :
                    killTimeAvrg) {
                zombieTime += t;
            }
            zombieTime = zombieTime / killTimeAvrg.size();
        }

        double betterTime = 0;
        double betterAcc = 0;
        if (!accuracyAvrg.isEmpty() && !killTimeAvrg.isEmpty()) {
            betterTime = killTimeAvrg.get(killTimeAvrg.size() - 1) - killTimeAvrg.get(0);
            betterAcc = accuracyAvrg.get(accuracyAvrg.size() - 1) - accuracyAvrg.get(0);
        }
        String aux = "Porcentaje de disparos acertados: " + accuracyAverage + "\n" +
                "Tiempo promedio en matar a un zombie: " + zombieTime + "\n" +
                "Diferencia de puntería logrado: " + betterAcc + "%" + "\n" +
                "Diferencia de tiempo logrado: " + betterTime + " seg.\n" +
                "Introduce tu nombre: ";
        while (player == null || player.equals("")) {
            player = new UiBooster().showTextInputDialog(aux);
            if (player == null || player.equals("")) {
                new UiBooster().showInfoDialog("Introduce tu nombre");
            }
        }

        db.insert(player, accuracyAverage, zombieTime, betterAcc, betterTime);

        resetGame();
    }

    void resetGame() {
        accuracyAvrg.clear();
        killTimeAvrg.clear();
        shooted = false;
        healthAux = 500;
        avatar.health=0;
        totalShoots = 0;
        niceShoot = 0;
        nEnemies = 5;
        damage = 4;
        speed = 3;
        hitted = false;
        menu = 0;
        kills.clear();
        enemies.clear();
        resetWave();
    }

    @Override
    public void mousePressed(MouseEvent event) {
        super.mousePressed(event);
        if (menu == 0 && mouseX < width / 2 + 600 && mouseX > width / 2 - 600 && mouseY < height / 2 + 50 && mouseY > height / 2 - 50) {
            remoteAddress = new UiBooster().showTextInputDialog("Dirección del juego: " + KetaiNet.getIP() + "\nDirección del celular: ");
            initNetworkConnection();
            menu += 1;
        }
        if (menu == 0 && mouseX > width / 2 - 600 && mouseX < width / 2 + 600 && mouseY > height / 2 + 100 && mouseY < height / 2 + 200) {
            String aux;
            try {

                   aux = showScores();
                   if (aux.equals("Puntuaciones obtenidas: \n")) {
                       new UiBooster().showInfoDialog("Aún no hay puntuaciones registradas");
                   } else {
                       new UiBooster().showInfoDialog(aux);
                   }
                   scoreWindow=true;

            } catch (ExecutionException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        if (!scoreWindow&&menu == 0 && mouseX > width / 2 - 600 && mouseX < width / 2 + 600 && mouseY > height / 2 + 150 && mouseY < height / 2 + 450) {

            exit();
        }
        scoreWindow=true;
    }

    void menuScreen() {
        cursor();
        image(menuBackground, 0, 0, width, height);
        image(playBtn, width / 2 - 600, height / 2 - 50, 1200, 100);
        image(scoresBtn, width / 2 - 600, height / 2 + 100, 1200, 100);
        image(exitBtn, width / 2 - 600, height / 2 + 250, 1200, 100);
    }

    void resetWave() {
        for (int i = 0; i < nEnemies; i++) {
            enemies.add(new Enemy(375, (int) speed, damage));
        }
    }

    void waves() {
        if (enemies.isEmpty()) {
            roundTime.stop();
            double accuracy = (double) (niceShoot * 100) / totalShoots;
            double timeAux = 0;
            for (Float time :
                    kills) {
                timeAux += time;
            }
            killTimeAvrg.add(timeAux / kills.size()+1);
            accuracyAvrg.add(accuracy);
            new UiBooster().showInfoDialog("Porcentaje de precisión: " + accuracy + "%" + "\nTiempo promedio en matar un zombie: " + timeAux / kills.size()+1 + " seg."+"\nDuración de la partida: "+roundTime.getElapsedSeconds()+" seg.");
            nEnemies++;
            speed += 1;
            damage++;
            roundTimeB=false;
            resetWave();
        }
    }

    double cos1(double angle) {
        return Math.cos(angle / 180 * PI);
    }

    double sin1(double angle) {
        return Math.sin(angle / 180 * PI);
    }

    double atan3(float y, float x) {
        return Math.atan2(y, x) / PI * 180;
    }

    void drawScope() throws ExecutionException, InterruptedException {
        scopeY = map(gyroscopeX, (float) -1.5, (float) 1.5, 0, height);
        scopeX = map(gyroscopeZ, (float) -1.8, (float) 1.8, 0, width);
        image(scope, scopeX, scopeY, 50, 50);
    }

    void shoot() {
        if (p == 1 && !shooted) {
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (scopeX < enemy.xPos + 50 && scopeX > enemy.xPos - 50 && scopeY < enemy.yPos + 50 && scopeY > enemy.yPos - 50) {
                    enemy.death();
                    enemies.remove(i);
                    niceShoot++;
                    kills.add((float) enemy.timer.getElapsedSeconds()/10);
                }
            }
            shooted = true;
            totalShoots++;
        }
        if (p == 0) {
            shooted = false;
        }
    }

    void oscEvent(OscMessage theOscMessage) {
        gyroscopeX = -theOscMessage.get(0).floatValue();
        gyroscopeY = theOscMessage.get(1).floatValue();
        gyroscopeZ = theOscMessage.get(2).floatValue();
        p = theOscMessage.get(3).floatValue();
    }

    void initNetworkConnection() {
        if (!connected){
            oscP5 = new OscP5(this, 12000, 0);
            remoteLocation = new NetAddress(remoteAddress, 12000);
            myIPAddress = KetaiNet.getIP();
            connected=true;
        }
    }

    public static void main(String[] args) {
        String[] procArgs = {"Game"};
        Game app = new Game();
        PApplet.runSketch(procArgs, app);
    }

    public String showScores() throws ExecutionException, InterruptedException {
        ArrayList<Score> listAux = db.runQuery();
        StringBuilder aux = new StringBuilder();
        aux.append("Puntuaciones obtenidas: \n");
        for (Score score :
                listAux) {
            aux.append("\n\tJugador: ").append(score.player).append("\n").append("\tPorcentaje de disparos acertados: ").append(score.accAvg).append("\n").append("\tTiempo promedio de supervivencia zombie: ").append(score.zombieTime).append("\n").append("\tDiferencia de puntería lograda: ").append(score.totalAccAvg).append("\n").append("\tDiferencia de tiempo lograda: ").append(score.totalZombieTime).append("\n").append("---------------------------------------------------");
        }
        return aux.toString();
    }

    class Avatar {
        float angle;
        PImage avatar;
        int health;

        Avatar() {
            avatar = loadImage("avatar.png");
            health = 500;
        }

        void drawAvatar() {
            if (hitted) {
                health--;
                hitted = false;
            }
            pushMatrix();
            angle = atan2(scopeY - center.y, scopeX - center.x);
            translate(center.x, center.y);
            rotate(angle);
            image(avatar, -50, -50, 75, 75);
            popMatrix();
        }
    }

    class Enemy {
        PImage[] frames = new PImage[32];
        PImage[] hit = new PImage[20];
        float xPos;
        float yPos;
        PVector location;
        PVector velocity;
        double damage;
        int size;
        int speed;
        double angle, angleAux;
        boolean close = false;
        boolean hitting = false;
        Timer timer;

        public Enemy(int size, int speed, double damage) {
            timer = new Timer();
            this.size = size;
            this.speed = speed;
            velocity = new PVector();
            this.damage = damage;
            angle = random(0, 180);
            loadGif();
            setPosition();
            timer.start();
        }

        private void setPosition() {
            int rand_n = rand.nextInt(0, 2);
            int rand_d = rand.nextInt(0, 2);
            if (rand_d == 0) {
                if (rand_n == 0) {
                    this.xPos = 15;
                } else {
                    this.xPos = width - 15;
                }
                this.yPos = random(height);
            } else {
                if (rand_n == 0) {
                    this.yPos = 15;
                } else {
                    this.yPos = height - 15;
                }
                this.xPos = random(width);
            }
            this.location = new PVector(this.xPos, this.yPos);

        }

        void loadGif() {
            for (int i = 0; i < 32; i++) {
                if (i < 10) {
                    frames[i] = loadImage("Enemy/walk/walk000" + i + ".png");
                } else {
                    frames[i] = loadImage("Enemy/walk/walk00" + i + ".png");
                }
            }
            for (int i = 0; i < 20; i++) {
                if (i < 10) {
                    hit[i] = loadImage("Enemy/hit/attack01_000" + i + ".png");
                } else {
                    hit[i] = loadImage("Enemy/hit/attack01_00" + i + ".png");
                }
                if (i == 13) {
                    hitted = true;
                }
            }

        }

        public void update() {
            drawEnemy();
            moveEnemy();
        }

        public void drawEnemy() {
            angleAux = atan2(center.y - yPos, center.x - xPos);
            pushMatrix();
            translate(xPos, yPos);
            rotate((float) (angleAux));
            if (!close) {
                image(frames[frameCount % 32], (float) -187.5, (float) -187.5, size, size);

            } else {
                image(hit[frameCount % 20], (float) -187.5, (float) -187.5, size, size);
            }
            popMatrix();
        }

        public void moveEnemy() {
            this.angle = atan3(center.y - this.yPos, center.x - this.xPos);
            if (abs(center.y - this.yPos) > 50 || abs(center.x - this.xPos) > 50) {
                this.xPos += (speed * cos1(this.angle));
                this.yPos += (speed * sin1(this.angle));
            } else {
                close = true;
                if (!hitting) {
                    healthAux -= this.damage;

                }
            }
        }

        void death() {
            timer.stop();
            hitting = true;
        }
    }

    class Timer {
        private long stopWatchStartTime = 0;
        private long stopWatchStopTime = 0;
        private boolean stopWatchRunning = false;

        public void start() {
            this.stopWatchStartTime = System.nanoTime();
            this.stopWatchRunning = true;
        }


        public void stop() {
            this.stopWatchStopTime = System.nanoTime();
            this.stopWatchRunning = false;
        }

        public long getElapsedSeconds() {
            long elapsedTime;

            if (stopWatchRunning)
                elapsedTime = (System.nanoTime() - stopWatchStartTime);
            else
                elapsedTime = (stopWatchStopTime - stopWatchStartTime);

            return elapsedTime / 1000000000;
        }
    }
}
