**ZombieSMP** brings your server to life by unleashing a genuine zombie apocalypse. Designed for creative server owners and unique SMP adventures, this plugin transforms nighttime into a high-stakes survival challenge with deep community interaction.

**_Features:_**

- Quadruple zombie spawns at night for real danger and chaos.

- Beds explode if players try to sleep, just like in the Nether.

- If a player is attacked by a zombie at night or by an infected player, they become a zombie for 10 minutes with permanent slowness.

- Deep LuckPerms integration: when infected, a player’s current permission group is saved and replaced with a new "zombie" group. After the effect ends (or is cleared by staff), the previous group is restored automatically.

- Track how many times each player has become a zombie with the %zombies% placeholder, usable in scoreboard, tab, or hologram.

- Infected players cannot attack other zombies or be attacked by them..

- Staff can infect any player manually with /zombies add player, or cure them at any time with /zombies clear player.

- All messages, durations, multipliers, and commands are fully configurable in config.yml.

- Optionally, you can choose if the infection timer runs while the player is offline.

**_To set up:_**

1. Dependencies required: LuckPerms for group management (absolutely necessary), PlaceholderAPI if you want to show %zombies% in scoreboard, tab, or holograms, and PaperMC 1.21.x.
2. To create the zombie group, use the following LuckPerms commands in your console:
/lp creategroup zombie
3. Optionally, set a prefix so infected players stand out:
/lp group zombie meta setprefix "&2[Zombie] "
4. You can also adjust the group’s permissions if you want to give infected players special limitations or abilities.
5. Edit the config.yml file in the ZombieSMP folder to fully customize infection times, multipliers, messages, and commands.
6. When infected, a player is moved to the zombie group and receives permanent slowness (this can’t be removed by drinking milk). After the timer runs out or when cured using /zombies clear, their old group and permissions are restored.
7. Use %zombies% in any PlaceholderAPI-compatible plugin (scoreboard, tab, hologram, chat) to show each player’s infection count.

ZombieSMP is made for servers that want to create real tension and unique zombie infection gameplay, perfect for special events, new SMP challenges, or creative game modes.
