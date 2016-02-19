package org.maxgamer.rs.model.item.inventory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.Bonus;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.EquipmentSet;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.FileConfig;

/**
 * @author netherfoam
 */
public class Equipment extends Container {

	private static HashMap<String, EquipmentSet> equipmentSets = new HashMap<String, EquipmentSet>();

	public static final int SIZE = 14;

	private EquipmentSet currentSet;
	private ItemStack[] items;
	private Mob owner;
	private int[] bonus;

	public Equipment(Mob owner) {
		items = new ItemStack[SIZE];
		bonus = new int[Bonus.COUNT];
		this.owner = owner;
		this.addListener(new ContainerListener() {
			@Override
			public void onSet(Container c, int slot, ItemStack old) {
				for (EquipmentSet set : equipmentSets.values()) {
					if (set.isWearingSet(Equipment.this)) {
						currentSet = set;
						break;
					} else currentSet = null;
				}
			}
		});
	}

	public static void load() throws Exception {
		Log.info("Loading Equipment Sets...");
		FileConfig f = new FileConfig(new File("./config/equipment_sets.yml"));
		f.reload();
		
		for(String name : f.getKeys()){
			EquipmentSet set = new EquipmentSet(name);
			
			ConfigSection setConfig = f.getSection(name);
			for(String type : setConfig.getKeys()){
				WieldType w;
				try{
					w = WieldType.valueOf(type.toUpperCase());
				}
				catch(IllegalArgumentException e){
					Log.warning("WieldType " + type + " is not found. Please use one of " + Arrays.toString(WieldType.values()));
					continue;
				}
				
				List<Integer> options = setConfig.getList(type, Integer.class, new LinkedList<Integer>());
				ItemStack[] items = new ItemStack[options.size()];
				for(int i = 0; i < options.size(); i++){
					items[i] = ItemStack.create(options.get(i));
				}
				set.setStack(w, items);
			}
			
			equipmentSets.put(name, set);
		}
		Log.info("Loaded " + equipmentSets.size() + " Equipment Sets.");
	}

	public Mob getOwner() {
		return owner;
	}

	/**
	 * Fetches the bonus for the given type given by all of the equipment
	 * represented by this inventory.
	 * 
	 * @param type
	 *            the bonus type
	 * @return the bonus, potentially negative as some items give negative
	 *         effects but usually positive
	 */
	public int getBonus(int type) {
		return bonus[type];
	}

	@Override
	protected void setItem(int slot, ItemStack item) {
		ItemStack old = items[slot];
		if (old != null && old.getWeapon() != null) {
			for (int i = 0; i < Bonus.COUNT; i++) {
				bonus[i] -= old.getWeapon().getBonus(i);
			}
		}
		items[slot] = item;

		if (item != null && item.getWeapon() != null) {
			for (int i = 0; i < Bonus.COUNT; i++) {
				bonus[i] += item.getWeapon().getBonus(i);
			}
		}
	}

	public void set(WieldType type, ItemStack item) {
		this.set(type.getSlot(), item);
	}

	public ItemStack get(WieldType type) {
		return this.get(type.getSlot());
	}

	public static boolean isWearingSet(String setName, Container equipment) {
		EquipmentSet set = equipmentSets.get(setName);
		if (set == null)
			return false;
		return set.isWearingSet(equipment);
	}

	public boolean isWearingSet(String setName) {
		if (currentSet == null) {
			EquipmentSet set = equipmentSets.get(setName);
			if (set == null)
				return false;
			if (set.isWearingSet(this)) {
				this.currentSet = set;
				return true;
			}
			return false;
		}
		return currentSet.getName().equalsIgnoreCase(setName);
	}

	@Override
	public ItemStack get(int slot) {
		return items[slot];
	}

	@Override
	public int getSize() {
		return items.length;
	}

	public ItemStack getHat() {
		return get(WieldType.HAT);
	}

	public ItemStack getBody() {
		return get(WieldType.BODY);
	}

	public ItemStack getBoots() {
		return get(WieldType.BOOTS);
	}

	public ItemStack getCape() {
		return get(WieldType.CAPE);
	}

	public ItemStack getAmulet() {
		return get(WieldType.AMULET);
	}

	public ItemStack getShield() {
		return get(WieldType.SHIELD);
	}

	public ItemStack getLegs() {
		return get(WieldType.LEGS);
	}

	public ItemStack getRing() {
		return get(WieldType.RING);
	}

	public ItemStack getArrows() {
		return get(WieldType.ARROWS);
	}

	public ItemStack getWeapon() {
		return get(WieldType.WEAPON);
	}

	public ItemStack getGloves() {
		return get(WieldType.GLOVES);
	}
}