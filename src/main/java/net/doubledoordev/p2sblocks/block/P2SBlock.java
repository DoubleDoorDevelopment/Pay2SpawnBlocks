/*
 * Copyright (c) 2014, DoubleDoorDevelopment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of DoubleDoorDevelopment nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.doubledoordev.p2sblocks.block;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.doubledoordev.p2sblocks.P2SBlocks;
import net.doubledoordev.p2sblocks.network.BlockBreakMessage;
import net.doubledoordev.p2sblocks.util.ServerHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import static net.doubledoordev.p2sblocks.util.Constants.MODID_LOWERCASE;

/**
 * @author Dries007
 */
public class P2SBlock extends Block
{
    public P2SBlock()
    {
        super(Material.cake);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setHardness(0f);
        this.setStepSound(soundTypeCloth);
        this.setBlockName(MODID_LOWERCASE);
        this.setBlockTextureName(MODID_LOWERCASE + ':' + MODID_LOWERCASE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z)
    {
        return P2SBlocks.instance.hasP2S ? 1f : .1f;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
    {
        if (world.isRemote && !P2SBlocks.instance.hasP2S) player.addChatComponentMessage(new ChatComponentTranslation("p2sblocks.msg.noP2s").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
        super.onBlockClicked(world, x, y, z, player);
    }

    @SubscribeEvent
    public void harvestHarvestDropsEvent(BlockEvent.HarvestDropsEvent event)
    {
        if (event.harvester == null || event.block != this || event.world.isRemote) return;

        if (ServerHandler.I.getHasP2S(event.harvester.getPersistentID()))
        {
            P2SBlocks.instance.snw.sendTo(new BlockBreakMessage(), (EntityPlayerMP) event.harvester);
            event.drops.clear();
        }
    }
}
