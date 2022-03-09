package gtwp.common.metatileentities.multi.parallel;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gtwp.api.capability.IParallelHatch;
import gtwp.api.metatileentities.GTWPMultiblockAbility;
import gtwp.api.utils.GTWPChatUtils;
import gtwp.common.items.GTWPMetaItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ParallelHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IParallelHatch>, IParallelHatch {

    private final boolean transmitter;
    private ParallelHatch pair = null;

    public ParallelHatch(ResourceLocation metaTileEntityId, boolean transmitter) {
        super(metaTileEntityId, 4);
        this.transmitter = transmitter;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder) {
        return new ParallelHatch(metaTileEntityId, transmitter);
    }

    public void setConnection(ParallelHatch pair){
        pair.pair = this;
        this.pair = pair;
    }

    public void setConnection(BlockPos position){
        TileEntity te = getWorld().getTileEntity(position);
        if(te instanceof MetaTileEntityHolder){
            MetaTileEntityHolder mteh = ((MetaTileEntityHolder) te);
            if(mteh.getMetaTileEntity() instanceof ParallelHatch){
                setConnection(((ParallelHatch) mteh.getMetaTileEntity()));
            }
        }
    }

    public void breakConnection(){
        if(isConnected()) {
            this.pair.pair = null;
            this.pair = null;
        }
    }

    public boolean isConnected(){
        return this.pair != null;
    }

    @Override
    public int getParallel(){
        if (transmitter) {
            if(((ParallelComputer) getController()).isReceivingSignal()) {
                TileEntity te = getWorld().getTileEntity(getPos().offset(getFrontFacing().getOpposite()));
                if (te instanceof MetaTileEntityHolder) {
                    MetaTileEntity mte = ((MetaTileEntityHolder) te).getMetaTileEntity();
                    if (mte instanceof ParallelComputerRack) {
                        return ((ParallelComputerRack) mte).getParallel();
                    }
                }
            }
        } else {
            if (isConnected())
                return pair.getParallel();
        }
        return 1;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        ItemStack item = playerIn.getHeldItem(hand);
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt != null) {
            if (playerIn.isSneaking()) {
                breakConnection();
                nbt.setInteger("inX", getPos().getX());
                nbt.setInteger("inY", getPos().getY());
                nbt.setInteger("inZ", getPos().getZ());
                GTWPChatUtils.sendMessage(playerIn, "Start connection");
            } else {
                int x = nbt.getInteger("inX");
                int y = nbt.getInteger("inY");
                int z = nbt.getInteger("inZ");
                BlockPos connectionPos = new BlockPos(x, y, z);
                if(getPos() == connectionPos){
                    GTWPChatUtils.sendMessage(playerIn, "Cannot connect to self");
                }else {
                    setConnection(connectionPos);
                    GTWPChatUtils.sendMessage(playerIn, "Connection successful " + x + " " + y + " " + z);
                    nbt.removeTag("inX");
                    nbt.removeTag("inY");
                    nbt.removeTag("inZ");
                }
            }
        }
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    @Override
    public MultiblockAbility<IParallelHatch> getAbility() {
        return transmitter ? null : GTWPMultiblockAbility.PARALLEL_HATCH;
    }

    @Override
    public void registerAbilities(List<IParallelHatch> list) {
        if(!transmitter) list.add(this);
    }
}
