package precisioncore.api.render.texture;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.cclop.LightMapOperation;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

public class RodHatchRenderer implements TextureUtils.IIconRegister {

    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite rodHatchBaseTexture;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite quadRodTexture;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite dualRodTexture;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite rodTexture;

    public RodHatchRenderer() {
        Textures.iconRegisters.add(this);
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        rodHatchBaseTexture = textureMap.registerSprite(new ResourceLocation(GTValues.MODID, "blocks/nuclear/base_rod_hatch"));
        quadRodTexture = textureMap.registerSprite(new ResourceLocation(GTValues.MODID, "blocks/nuclear/quad_rod"));
        dualRodTexture = textureMap.registerSprite(new ResourceLocation(GTValues.MODID, "blocks/nuclear/dual_rod"));
        rodTexture = textureMap.registerSprite(new ResourceLocation(GTValues.MODID, "blocks/nuclear/rod"));
    }

    @SideOnly(Side.CLIENT)
    public void renderSided(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline, boolean hasRod, int rodAmount, boolean isMOX) {
        Textures.renderFace(renderState, translation, (IVertexOperation[])ArrayUtils.addAll(pipeline, new IVertexOperation[]{new LightMapOperation(240, 240)}), EnumFacing.UP, Cuboid6.full, this.rodHatchBaseTexture, BlockRenderLayer.CUTOUT_MIPPED);
        if(hasRod) {
            TextureAtlasSprite sprite = rodAmount == 4 ? quadRodTexture :
                    rodAmount == 2 ? dualRodTexture : rodTexture;
            IVertexOperation[] color = (IVertexOperation[]) ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(isMOX ? 0xC51D34 : 0x00FEE2)));
            Textures.renderFace(renderState, translation, color, EnumFacing.UP, Cuboid6.full, sprite, BlockRenderLayer.CUTOUT_MIPPED);
        }
    }
}
