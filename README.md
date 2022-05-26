# Deadly-eXperience
### Compilación
#### Requerimentos: 
- IntelliJ Idea
- Java JDK 18
#### Projecto Processing Desktop
1- Dentro de IntelliJ Idea seleccionar "Abrir Proyecto" y navegar hasta la carpeta "DeadlyeXperience" del repositorio. <br/>
2- Una vez abierto, en la barra de herramientas localizar la columna "Run" y seleccionar "Run Game"
#### Projecto Processing Android
1- Abrir processing seleccionando el modo "Android". <br/>
2- Navegar hasta la carpeta "AndroidController" del repositorio.
3- Seleccionar "Run on device" o "Run on emulator"
## Guía de Usuario
### Juego
Pantalla principal:
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/menuScreen.png?raw=true)
Al seleccionar el botón puntuaciones se pueden ver las puntuaciones registradas de las partidas jugadas previamente:<br/>
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/scores.png?raw=true)<br/>
Para comenzar una partida, se tiene que clickear el botón "Start Game", donde nos aparecerá una ventana donde se tiene que indicar la dirección IP del celular (mostrada en la aplicación Android): <br/>
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/ip.png?raw=true)

Al seleccionar aceptar comenzará el juego, donde aparecerán varios enemigos a los cuales hay que dispara utilizando el control Android: <br/>
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/game.png?raw=true)<br/>
Cuando se mata a todos los enemigos aparece una ventana con los datos obtenidos durante la oleada: <br/>
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/endWave.png?raw=true)<br/>
Al perder toda la vida la partida terminar y aparecerá una ventana con los datos recolectados a través de todas las oleadas, además de solicitar el nombre del jugador para registrar la puntuación en Firebase: <br/>
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/death.png?raw=true)<br/>
Al darle en aceptar el juego volverá al menú del juego.

### Control Android
Al abrir la aplicación se solicitará que se ingrese la dirección IP del juego (mostrada en la aplicación Desktop): <br/>
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/requestIP.jpeg?raw=true)<br/>
Una vez ingresada la IP, aparecerá la pantalla del control. Si se toca la zona verde, de realizará un disparo que lo recibirá el Juego en la computadora, y al tocar la zona blanca se reiniciarán los valores del giroscopio para centrar la mira dentro del juego:<br/>
![alt text](https://github.com/DavidHdezG/Deadly-eXperience/blob/main/readmeImgs/gun.jpeg?raw=true)
