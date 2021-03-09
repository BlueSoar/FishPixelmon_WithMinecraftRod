package io.github.bluesoar.fpwmr;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleType;
import com.pixelmonmod.pixelmon.util.PixelmonPlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class FishPixelmon extends JavaPlugin implements Listener {
  public static FishPixelmon plugin;
  
  public void onEnable() {
    plugin = this;
    saveDefaultConfig();
    Bukkit.getPluginManager().registerEvents(this, (Plugin)this);
    getLogger().info("原版鱼竿钓宝可梦系统已开启");
  }
  
  @EventHandler
  private void event(PlayerFishEvent event) {
    Player player = event.getPlayer();
    if (event.getCaught() != null && 
      getChance(getConfig().getDouble("FPWMR.fish-chance"))) {
      World world = Bukkit.getWorld(getConfig().getString("FPWMR.enable-world"));
      if (player.getWorld() == world) {
        CraftWorld craftworld = (CraftWorld)world;
        WorldServer forgetWorld = craftworld.getHandle();
        Pokemon pokemon = Pixelmon.pokemonFactory.create(EnumSpecies.randomPoke());
        String name = "fish" + pokemon.getSpecies().getLocalizedName();
          EntityPixelmon pixelmon = pokemon.getOrSpawnPixelmon((World)forgetWorld, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
          WildPixelmonParticipant pm2 = new WildPixelmonParticipant(new EntityPixelmon[] { pixelmon });
          EntityPlayerMP p1 = PixelmonPlayerUtils.getUniquePlayerStartingWith(player.getName());
          EntityPixelmon player1FirstPokemon = Pixelmon.storageManager.getParty(p1).getAndSendOutFirstAblePokemon((Entity)p1);
          PlayerParticipant pplayer1 = new PlayerParticipant(p1, new EntityPixelmon[] { player1FirstPokemon });
          BattleRegistry.startBattle(pplayer1.getParticipantList(), pm2.getParticipantList(), EnumBattleType.Single);
          event.setCancelled(true);
          return;
        } 
      } 
    } 
  
  public boolean getChance(double chance) {
    boolean chanceBoolen = (chance >= 0.0D && chance <= 1000.0D && Math.random() <= chance / 1000.0D);
    return chanceBoolen;
  }
}
