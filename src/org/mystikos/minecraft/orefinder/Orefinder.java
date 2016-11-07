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

import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Orefinder
  extends JavaPlugin
{
  static final Logger LOG = Logger.getLogger("Minecraft");
  private final RCHPlayerInteract pInteract = new RCHPlayerInteract(this);
  
    /**
     * On enable 
     */
    @Override
  public void onEnable()
  {
    getConfig().options().copyDefaults(true);
    saveConfig();
    
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(this.pInteract, this);
    
    this.pInteract.init();
    
    getLogger().info("OreFinder enabled.");
  }
  
    /**
     * On Disable
     */
    @Override
  public void onDisable()
  {
    getLogger().info("OreFinder disabled.");
  }
}
