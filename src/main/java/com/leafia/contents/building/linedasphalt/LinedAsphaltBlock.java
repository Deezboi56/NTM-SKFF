package com.leafia.contents.building.linedasphalt;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.BlockBase;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.ModBlocks.LinedAsphalts;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.leafia.dev.customblock.ICustomBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

import static com.hbm.blocks.ModBlocks.GenericBlockResistance.CONCRETE;

public class LinedAsphaltBlock extends AsphaltBlock implements ICustomBlock {
	public static class AsphaltLine {
		public boolean x = false;
		public boolean z = false;
		public boolean n = false;
		public boolean s = false;
		public boolean w = false;
		public boolean e = false;
		public AsphaltLine() {}
		public AsphaltLine(Block b) {
			if (b instanceof LinedAsphaltBlock lined) {
				x = lined.texture.contains("x");
				z = lined.texture.contains("z");
				n = lined.texture.contains("n");
				s = lined.texture.contains("s");
				w = lined.texture.contains("w");
				e = lined.texture.contains("e");
			}
		}
		public AsphaltLine(String texture) {
			x = texture.contains("x");
			z = texture.contains("z");
			n = texture.contains("n");
			s = texture.contains("s");
			w = texture.contains("w");
			e = texture.contains("e");
		}
		public AsphaltLine rotate() {
			AsphaltLine line = new AsphaltLine();
			line.z = x;
			line.x = z;
			line.e = n;
			line.s = e;
			line.w = s;
			line.n = w;
			return line;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			if (x) builder.append("x");
			if (z) builder.append("z");
			if (n) builder.append("n");
			if (s) builder.append("s");
			if (w) builder.append("w");
			if (e) builder.append("e");
			return builder.toString();
		}
		@Nullable
		public static Block getBlock(String str) {
			if (LinedAsphalts.replacementMap.containsKey(str))
				str = LinedAsphalts.replacementMap.get(str);
			return LinedAsphalts.blocks.get(str);
		}
	}
	// xznswe
	String texture;
	public LinedAsphaltBlock(String s,String texture) {
		super(Material.ROCK,s);
		INSTANCES.add(this);
		setCreativeTab(MainRegistry.blockTab);
		setHardness(15.0F);
		setResistance(CONCRETE.v);
		this.texture = texture;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void bakeModel(ModelBakeEvent event) {
		try {
			IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft:block/cube_bottom_top"));
			ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();
			ResourceLocation side = new ResourceLocation(RefStrings.MODID, "blocks/asphalt");
			ResourceLocation top = new ResourceLocation(RefStrings.MODID, "blocks/leafia/sellacity/lined_asphalt/"+texture);
			// Base texture
			textureMap.put("top", top.toString());
			textureMap.put("bottom", side.toString());
			textureMap.put("side", side.toString());

			IModel retexturedModel = baseModel.retexture(textureMap.build());

			IBakedModel bakedModel = retexturedModel.bake(
					ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()
			);
			event.getModelRegistry().putObject(new ModelResourceLocation(getRegistryName(),"normal"), bakedModel);
			event.getModelRegistry().putObject(new ModelResourceLocation(getRegistryName(),"inventory"), bakedModel);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(
				Item.getItemFromBlock(this),
				0,
				new ModelResourceLocation(getRegistryName(),"inventory")
		);
		// pain pain PAIN PAIN PAIN PAIN PAIN PAIN PAIN PAIN PAIN PAIN
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprite(TextureMap map) {
		ResourceLocation loc = new ResourceLocation(RefStrings.MODID, "blocks/leafia/sellacity/lined_asphalt/"+texture);
		map.registerSprite(loc);
	}

	@Override
	public Item getItemDropped(IBlockState state,Random rand,int fortune) {
		return Item.getItemFromBlock(ModBlocks.asphalt);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state,RayTraceResult target,World world,BlockPos pos,EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(ModBlocks.asphalt));
	}
}
