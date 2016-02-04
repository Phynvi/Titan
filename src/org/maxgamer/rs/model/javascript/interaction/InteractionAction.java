package org.maxgamer.rs.model.javascript.interaction;

import java.io.File;
import java.io.IOException;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.javascript.JavaScriptCall;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;

import co.paralleluniverse.fibers.SuspendExecution;

public class InteractionAction extends Action{
	private String function;
	private Entity target;
	private File file;
	private JavaScriptCall call;
	
	public InteractionAction(Mob mob, Entity target, File jsFile, String function) {
		super(mob);
		this.target = target;
		this.function = function;
		this.file = jsFile;
	}

	@Override
	protected void run() throws SuspendExecution {
		JavaScriptFiber fiber = new JavaScriptFiber();
		
		try{
			fiber.set("fiber", fiber);
			fiber.set("player", getOwner());
			
			if(fiber.parse("lib/core.js").isFinished() == false){
				throw new RuntimeException("lib/core.js cannot contain pauses outside of functions."); 
			}
			if(fiber.parse("lib/dialogue.js").isFinished() == false){
				throw new RuntimeException("lib/dialogue.js cannot contain pauses outside of functions."); 
			}
			if(fiber.parse(file).isFinished() == false){
				throw new RuntimeException(file + " cannot contain pauses outside of functions.");
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		getOwner().face(target);
		
		try {
			call = fiber.invoke(function, getOwner(), target);
		}
		catch (NoSuchMethodException e) {
			Log.debug("File " + file + " exists, but the function " + function + "() does not.");
			return;
		}
		
		Action.wait(1);
		while(call.isFinished() == false){
			Action.wait(1);
		}
	}

	@Override
	protected void onCancel() {
		if(call != null){
			call.terminate();
		}
	}

	@Override
	protected boolean isCancellable() {
		return true;
	}
}