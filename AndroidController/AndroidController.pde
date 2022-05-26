import cassette.audiofiles.SoundFile;
import ketai.net.*;
import ketai.sensors.*;
import netP5.*;
import oscP5.*;
import android.content.DialogInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.widget.EditText;

Activity act;
OscP5 oscP5;
KetaiSensor sensor;
NetAddress remoteLocation;
float GyroscopeX, GyroscopeY, GyroscopeZ;
float touch=0;
String myIPAddress; 
String remoteAddress;   
SoundFile []shoot;
int i;
int nFire=10;
int menu=0;
boolean keyboard=false;
void setup(){
  i=0;
  sensor= new KetaiSensor(this);
  orientation(PORTRAIT);
  textAlign(CENTER, CENTER);
  textSize(42);
  sensor.start();
  shoot=new SoundFile[10];
   for(int n=0;n<nFire;n++){
     shoot[n]=new SoundFile(this,"fire.wav");
   }   
}
void draw(){
  switch(menu){
    case 0:
      background(0);
      fill(255);
      text("IP del celular: "+KetaiNet.getIP(),width/2,height/4);      
      return;
    case 1:
      background(78, 93, 75);
      text("Toca para disparar!", width/2, height/2);
      sendData();
      if(i==(nFire-1)){
         i=0;
      }
      rect(0,0,width,height/5);
      fill(0);
      text("Toca para calibrar la puntería",width/2,height/10);
      fill(255);
      return;
  }  
}

void mousePressed() {
  if(menu==1&&touch==0&& mouseY>height/5){
    touch=1;
    if(shoot[i]!=null){
      shoot[i].play();
    }   
    i++;
  }
  if(menu==1&&mouseY<height/5){
    GyroscopeX=0;
    GyroscopeZ=0;
  }
  if(menu==0){
    dialogBox();
  }
}
void mouseReleased() {
    touch = 0;
}
void onGyroscopeEvent(float x, float y, float z)
{
  GyroscopeX += 0.1 * x;
  GyroscopeY = y;
  GyroscopeZ -= 0.1 * z;
}

void sendData(){
  OscMessage myMessage = new OscMessage("/1234");
  myMessage.add(GyroscopeX);    
  myMessage.add(GyroscopeY);
  myMessage.add(GyroscopeZ);
  myMessage.add(touch);
  oscP5.send(myMessage, remoteLocation);   
}

void initNetworkConnection()
{
  oscP5 = new OscP5(this, 12000);                         
  remoteLocation = new NetAddress(remoteAddress, 12000);  
  myIPAddress = KetaiNet.getIP();                         
}

void dialogBox() {
  act = this.getActivity();
  act.runOnUiThread(new Runnable() {
    public void run() {
      AlertDialog.Builder builder = new AlertDialog.Builder(act);
      final EditText input = new EditText(act); 
      builder.setView(input); 
      builder.setTitle("Dirección del juego:");
      builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
        public void onClick(DialogInterface dialog, int whichButton) { 
          
          remoteAddress = input.getText().toString().trim(); 
          initNetworkConnection();
          menu++;
        }
      }
      );
      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
        public void onClick(DialogInterface dialog, int whichButton) { 
          dialog.cancel();
        }
      }
      ); 
      builder.show();
    }
  }
  );
}
