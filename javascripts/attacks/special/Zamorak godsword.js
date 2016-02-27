/**
 * This is the Zamorak Godsword special attack (Ice Cleave)
 *
 * @author Albert Beaupre
 * @date 20 Feb 2016
 */
importClass(org.maxgamer.rs.model.entity.mob.combat.MeleeAttack);

/**
 * Optional - Generate the damage that should be dealt to the target via the damage object.
 * No damage should ever be applied here, as that would skip the Event system, animations,
 * delays and damage tracking.  This method defaults to a single random melee strike if the
 * method isn't specified.
 * @return boolean (default true). If the preparation fails, returning false will end the attack
 * 		   without dealing damage. Not dealing any damage will have the same effect.
 */
function prepare(attacker, target, damage){
	damage.add(MeleeAttack.roll(attacker, target, 2.0, 1.10));
}

/**
 * Optional - Apply the damage that was generated by prepare(). This damage has been pushed through
 * the event system, the animation has been performed and the damage logged. By default, this method
 * will apply the damage (damage.apply(target)) and grant experience to the attacker. This method is
 * the only method that may call wait(int ticks).  Calling wait() here will NOT be called as part of the player's
 * action - The attack is now separated from any game actions. Eg, the projectile is already flying,
 * the attacker has no responsibility to continue the action.
 */
function perform(attacker, target, damage){
	attacker.animate(7070);
	attacker.graphics(2110);
	target.graphics(2111);
	wait(1);
	damage.apply(attacker);
	
	//TODO freeze target for 20 seconds
}

/**
 * Optional - Take the consumable items from the attacker. Consumables may be runes, special attack
 * energy, equipment (arrows/knives), or anything else.  Returning false will fail the attack. Not
 * returning or returning true signifies that the attacker had all required consumables to attack
 */
function takeConsumables(attacker){
	var e = attacker.getAttackEnergy();
	if (e < 50) {
		attacker.sendMessage("You do not have enough special attack energy.");
		return false;
	}
	attacker.setAttackEnergy(e - 50);
	return true;
}