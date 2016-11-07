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

public class itemConf {

    private String[] inhand;
    private String[] lookfor;
    private final Orefinder plugin;
    private int size;

    itemConf(Orefinder _plugin) {
        this.plugin = _plugin;
    }

    void Init() {
        this.size = (this.plugin.getConfig().getList("indicate.inhand").size() > this.plugin.getConfig().getList("indicate.lookfor").size() ? this.plugin.getConfig().getList("indicate.lookfor").size() : this.plugin.getConfig().getList("indicate.lookfor").size());
        this.inhand = new String[this.size];
        this.lookfor = new String[this.size];
        for (int idxA = 0; idxA < this.size; idxA++) {
            this.inhand[idxA] = this.plugin.getConfig().getList("indicate.inhand").get(idxA).toString();
            this.lookfor[idxA] = this.plugin.getConfig().getList("indicate.lookfor").get(idxA).toString();
        }
    }

    String getOreId(String tid) {
        for (int idxA = 0; idxA < this.size; idxA++) {
            if (this.inhand[idxA].equalsIgnoreCase(tid)) {
                return this.lookfor[idxA];
            }
        }
        return "";
    }
}
