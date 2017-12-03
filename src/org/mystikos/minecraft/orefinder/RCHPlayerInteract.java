/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mystikos.minecraft.orefinder;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.Random;

class RCHPlayerInteract
        implements Listener {

    private final Orefinder plugin;
    private final itemConf ic;
    private PlayerCD pcdm;

    public RCHPlayerInteract(Orefinder _of) {
        this.plugin = _of;
        this.ic = new itemConf(this.plugin);
    }

    private int getBlockTypeDistance(Location loc, String stid) {
        World wrld = loc.getWorld();
        loc.getX();
        double cbx = loc.getX();
        double cby = loc.getY();
        double cbz = loc.getZ();
        for (int d = 0; d < 20; d++) {
            for (int dx = -d; dx <= d; dx++) {
                for (int dz = -d; dz <= d; dz++) {
                    if ((wrld.getBlockAt((int) cbx + dx, (int) cby + d, (int) cbz + dz).getType().toString().equalsIgnoreCase(stid))
                            || (wrld.getBlockAt((int) cbx + dx, (int) cby - d, (int) cbz + dz).getType().toString().equalsIgnoreCase(stid))) {
                        return d;
                    }
                }
            }
            for (int dx = -d; dx <= d; dx++) {
                for (int dy = -d + 1; dy <= d - 1; dy++) {
                    if ((wrld.getBlockAt((int) cbx + dx, (int) cby + dy, (int) cbz + d).getType().toString().equalsIgnoreCase(stid))
                            || (wrld.getBlockAt((int) cbx + dx, (int) cby + dy, (int) cbz - d).getType().toString().equalsIgnoreCase(stid))) {
                        return d;
                    }
                }
            }
            for (int dz = -d + 1; dz <= d - 1; dz++) {
                for (int dy = -d + 1; dy <= d - 1; dy++) {
                    if ((wrld.getBlockAt((int) cbx + d, (int) cby + dy, (int) cbz + dz).getType().toString().equalsIgnoreCase(stid))
                            || (wrld.getBlockAt((int) cbx - d, (int) cby + dy, (int) cbz + dz).getType().toString().equalsIgnoreCase(stid))) {
                        return d;
                    }
                }
            }
        }
        return -1;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        int pid = event.getPlayer().getEntityId();
        this.pcdm.delRecord(pid);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("orefinder.use")) {
            ItemStack holding = player.getInventory().getItemInMainHand();
            String stid;

            if ((event.getAction() == Action.LEFT_CLICK_BLOCK) && (!(this.ic.getOreId(holding.getType().toString())).equalsIgnoreCase("")) && (this.pcdm.getUseRight(player.getEntityId()))) {
                stid = this.ic.getOreId(holding.getType().toString().toLowerCase());
                int R = getBlockTypeDistance(event.getClickedBlock().getLocation(), stid);
                if (R == -1) {
                    player.sendMessage(ChatColor.BLUE + this.plugin.getConfig().getString("text.very_cold"));
                } else if (R < 2) {
                    player.sendMessage(ChatColor.DARK_RED + this.plugin.getConfig().getString("text.oneblock_hot"));
                } else if (R < 4) {
                    player.sendMessage(ChatColor.RED + this.plugin.getConfig().getString("text.very_hot"));
                } else if (R < 6) {
                    player.sendMessage(ChatColor.RED + this.plugin.getConfig().getString("text.hot"));
                } else if (R < 8) {
                    player.sendMessage(ChatColor.GOLD + this.plugin.getConfig().getString("text.warm"));
                } else if (R < 15) {
                    player.sendMessage(ChatColor.YELLOW + this.plugin.getConfig().getString("text.lukewarm"));
                } else if (R < 20) {
                    player.sendMessage(ChatColor.AQUA + this.plugin.getConfig().getString("text.cold"));
                }
                if (this.plugin.getConfig().getBoolean("functions.block_stealing")) {
                    Random rgen = new Random();
                    int rndnum = rgen.nextInt(this.plugin.getConfig().getInt("chance.steal_block"));
                    if (rndnum == 0) {
                        player.damage(1);
                        if (player.getGameMode() == GameMode.SURVIVAL) {
                            int amt = holding.getAmount();
                            if (amt > 1) {
                                holding.setAmount(--amt);
                            } else {
                                PlayerInventory inventory = player.getInventory();
                                inventory.setItemInMainHand(null);
                            }
                        }
                        player.sendMessage(this.plugin.getConfig().getString("text.ender_steal"));
                    }
                }
            }
        }
    }

    public void init() {
        this.pcdm = new PlayerCD(Bukkit.getMaxPlayers());
        this.ic.Init();
    }

    class PCDR {

        private final int pid;
        private int time;

        PCDR(int _pid) {
            this.time = 0;
            this.pid = _pid;
        }

        int getPid() {
            return this.pid;
        }

        int getTime() {
            return this.time;
        }

        void setTime(int _time) {
            this.time = _time;
        }
    }

    class PlayerCD {

        private final RCHPlayerInteract.PCDR[] pcdrs;
        private final int cap;
        private int cnt;

        PlayerCD(int slots) {
            this.cnt = 0;
            this.cap = slots;
            this.pcdrs = new RCHPlayerInteract.PCDR[slots];
        }

        private int getRid(int _pid) {
            for (int idxA = 0; idxA < this.cnt; idxA++) {
                if (this.pcdrs[idxA].getPid() == _pid) {
                    return idxA;
                }
            }
            return -1;
        }

        private void delRecord(int _pid) {
            int rid = getRid(_pid);
            if (rid != -1) {
                System.arraycopy(this.pcdrs, rid + 1, this.pcdrs, rid + 1 - 1, this.cap - (rid + 1));
                this.cnt -= 1;
            }
        }

        public int getSize() {
            return this.cap;
        }

        boolean getUseRight(int _pid) {
            int rid = getRid(_pid);
            if ((rid == -1) && (this.cnt < this.cap)) {
                rid = this.cnt;
                this.pcdrs[rid] = new RCHPlayerInteract.PCDR(_pid);
                this.cnt += 1;
            }
            if (rid != -1) {
                int ctime = (int) (System.currentTimeMillis() / 1000L);
                if (ctime > this.pcdrs[rid].getTime()) {
                    this.pcdrs[rid].setTime(ctime);
                    return true;
                }
            }
            return false;
        }
    }
}
