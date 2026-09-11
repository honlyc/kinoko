## Kinoko

Kinoko is a server emulator for the popular mushroom game.

## MapleGlory

This project is based on [Kinoko](https://github.com/iw2d/kinoko), 80% of logic is done by Kinoko's developer.
Main difference between the repos is the addition of A LOT of quests -- not all conform with GMS behaviour.

## Setup

Basic configuration is available via environment variables - the names and default values of the configurable options
are defined in [ServerConstants.java](src/main/java/mapleglory/server/ServerConstants.java) and [ServerConfig.java](src/main/java/mapleglory/server/ServerConfig.java), and in `.env`.
and [ServerConfig.java](src/main/java/kinoko/server/ServerConfig.java).

> [!NOTE]
> Client WZ files are expected to be present in the `wz/` directory in order for the provider classes to extract the
> required data. The required files are as follows:
> ```
> Character.wz
> Item.wz
> Skill.wz
> Morph.wz
> Map.wz
> Mob.wz
> Npc.wz
> Reactor.wz
> Quest.wz
> String.wz
> Etc.wz
> ```

#### Java setup

Building the project requires Java 21 and maven.

```bash
# Build jar
$ mvn clean package
```