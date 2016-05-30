package org.maxgamer.rs.model.map.spawns;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.UUID;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.structure.dbmodel.Mapping;
import org.maxgamer.rs.structure.dbmodel.Transparent;

public class NPCSpawn extends Transparent {
	@Mapping
	protected int id;
	@Mapping
	protected int npc_id;
	@Mapping
	protected String map;
	@Mapping
	protected short x;
	@Mapping
	protected short y;
	@Mapping
	protected byte z;
	
	public NPCSpawn(long id) {
		super("NPCSpawn", new String[]{"id"}, new Object[]{id});
	}
	
	public NPCSpawn(int npc_id, Location loc){
		super("NPCSpawn", new String[]{"id"}, new Object[]{UUID.randomUUID().getLeastSignificantBits()});
		this.npc_id = npc_id;
		this.x = (short) loc.x;
		this.y = (short) loc.y;
		this.z = (byte) loc.z;
		this.map = loc.getMap().getName();
	}
	
	public Location getLocation(){
		return new Location(Core.getServer().getMaps().get(map), x, y, z);
	}
	
	public NPC spawn(){
		Location l = getLocation();
		if(l.getMap() == null){
			throw new IllegalStateException("Map not found: " + map + ", cannot spawn NPC.");
		}
		
		NPC npc;
		try{
			npc = new NPC(npc_id, id, l);
		}
		catch(RuntimeException e){
			if(e.getCause() instanceof FileNotFoundException){
				Log.warning("NPC missing from cache. I'll delete it for you. ID: " + id + ", NPC_ID: " + npc_id + " at " + l);
				try {
					this.delete(Core.getWorldDatabase().getConnection());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return null;
			}
			else{
				throw e;
			}
		}
		npc.setSpawn(l);
		return npc;
	}
}
