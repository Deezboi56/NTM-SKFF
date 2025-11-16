package com.leafia.eventbuses;

import com.hbm.capability.HbmLivingProps;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaDebug;
import com.leafia.transformer.LeafiaGeneralLocal;
import com.leafia.transformer.LeafiaGls;
import com.leafia.unsorted.IEntityCustomCollision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fluids.FluidEvent.FluidFillingEvent;
import net.minecraftforge.fluids.FluidEvent.FluidMotionEvent;
import net.minecraftforge.fluids.FluidEvent.FluidSpilledEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeafiaClientListener {
	public static class Digamma {
		public static float digammaDose = 1; // debug
		static Random rand = new Random();
		static List<DigammaText> texts = new ArrayList<>();
		@SubscribeEvent
		public void shake(EntityViewRenderEvent.CameraSetup e) {
			if (digammaDose > 0.25f) {
				float ratio = (digammaDose-0.25f)/0.75f;
				GL11.glTranslated(rand.nextGaussian()*ratio*0.065,0,0);
			}
		}
		@SubscribeEvent
		public void onOverlayRender(RenderGameOverlayEvent.Pre event) {
			if (event.getType() == ElementType.CROSSHAIRS) {
				ScaledResolution resolution = event.getResolution();
				FontRenderer font = Minecraft.getMinecraft().fontRenderer;
				for (DigammaText text : texts) {
					int w = font.getStringWidth(text.message);
					int h = font.FONT_HEIGHT;
					LeafiaGls.pushMatrix();
					float shakex = rand.nextFloat()*4-2;
					float shakey = rand.nextFloat()*4-2;
					LeafiaGls.translate(resolution.getScaledWidth()*text.x+shakex,resolution.getScaledHeight()*text.y+shakey,0);
					LeafiaGls.scale(text.scale);
					LeafiaGls.translate(-w/2f,-h/2f,0);
					/*
					int alphaChannel = 0x01000000;
					float alpha = text.timeElapsed;
					if (text.timeElapsed >= 1) {
						if (text.timeElapsed > 5)
							alpha = (1-(text.timeElapsed-5))*255;
						else
							alpha = 1;
					}
					int value = alphaChannel*(int)Math.ceil(alpha*255);*/ // fuck off
					font.drawString(text.message,0,0,0xFFFFFF);
					LeafiaGls.popMatrix();
				}
			}
		}
		static float timer = 0;
		static float timerMax = 5;
		public static int messageVariants = 10;
		public static void update() {
			digammaDose = HbmLivingProps.getDigamma(Minecraft.getMinecraft().player)/10;
			int needle = 0;
			while (needle < texts.size()) {
				DigammaText text = texts.get(needle);
				text.timeElapsed += 0.05f;
				if (text.timeElapsed > text.lifetime)
					texts.remove(needle);
				else
					needle++;
			}
			timer = timer + 0.05f;
			if (timer >= timerMax) {
				timer = 0;
				timerMax = rand.nextFloat()*3f+1;
				if (rand.nextFloat()+0.1f < digammaDose) {
					String msg = I18nUtil.resolveKey("gui.digamma_message."+rand.nextInt(messageVariants));
					float offset = rand.nextFloat()-0.5f;
					texts.add(new DigammaText(
							msg,
							0.5f+offset*Math.max(0,1-msg.length()/60f),
							0.1f+rand.nextFloat()*0.8f,
							1f+rand.nextFloat(),
							5+rand.nextFloat()*4
					));
				}
			}
		}
		public static class DigammaText {
			final float x;
			final float y;
			final float scale;
			final String message;
			float timeElapsed = 0;
			final float lifetime;
			public DigammaText(String message,float x,float y,float scale,float lifetime) {
				this.message = message;
				this.x = x;
				this.y = y;
				this.scale = scale;
				this.lifetime = lifetime;
			}
		}
	}
	public static class Unsorted {
		/**
		 * Thank you forge for naming it like this
		 * <p>Yes, {@link RenderGameOverlayEvent.Text} is the event solely for debug screen, despite the radically confusing name just "Text".
		 * <p>Good job, forge. I'll kindly prepare 9800 schrabidium missiles to serve you.
		 */
		@SubscribeEvent
		public void dammit(RenderGameOverlayEvent.Text debug) {
			LeafiaGeneralLocal.injectDebugInfoLeft(debug.getLeft());
		}

		@SubscribeEvent
		public void onGetEntityCollision(GetCollisionBoxesEvent evt) {
			if (evt.getEntity() == null) return;
			List<AxisAlignedBB> list = evt.getCollisionBoxesList();
			List<Entity> list1 = evt.getWorld().getEntitiesWithinAABBExcludingEntity(evt.getEntity(), evt.getAabb().grow((double)02.25F));
			for(int i = 0; i < list1.size(); ++i) {
				Entity entity = (Entity)list1.get(i);
				if (!evt.getEntity().isRidingSameEntity(entity)) {
					if (entity instanceof IEntityCustomCollision) {
						List<AxisAlignedBB> aabbs = ((IEntityCustomCollision)entity).getCollisionBoxes(evt.getEntity());
						if (aabbs == null) continue;
						for (AxisAlignedBB aabb : aabbs) {
							if (aabb != null && aabb.intersects(aabb))
								list.add(aabb);
						}
					}
				}
			}
		}
	}
	public static class Fluids {
		@SubscribeEvent
		public void filled(FluidFillingEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
		@SubscribeEvent
		public void spilled(FluidSpilledEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
		@SubscribeEvent
		public void moved(FluidMotionEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
	}
}
