# CloudNet-Lobby
This Plugin depends on McUtils which you can obtain <a href="https://software4you.eu/prods/utils.php" target="_blank">here</a>.<br>
You can find the official SpigotMC resource <a href="https://www.spigotmc.org/resources/cloudnet-lobby.63985/" target="_blank">here</a>.

CloudNet-Lobby is a module-based (lobby-) system. What does module-based mean? The Lobby-System is only the base for the modules (like Spigot is the base for plugins).

That means: The system itself cannot do much. The power lies in the modules (I'll call them "addons" from now)!

But don't worry, you'll get some pre-made addons and configurations.

You can also use this system on non-lobby servers, just disable (or enable) every setting to make the server behavior normal (check out the configuration!). Why should you place a Lobby-System on a non-lobby server and make the server behavior normal? You can still use the Addons, which is a great advantage (e.g. the Action Scoreboard)!

## For Developers
### Maven access
  Repository:
```xml
  <repositories>
    ...
    <repository>
      <id>software4you-repo</id>
      <url>https://repo.software4you.eu/repo</url>
    </repository>
    ...
  </repositories>
```
  Dependency:
```xml
  <dependencies>
    ...
    <dependency>
      <groupId>eu.software4you.minecraft.cloudnetlobby</groupId>
      <artifactId>CloudNetLobby</artifactId>
      <version>1.0</version>
    </dependency>
    ...
  </dependencies>
```
