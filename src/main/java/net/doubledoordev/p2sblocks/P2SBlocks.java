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

package net.doubledoordev.p2sblocks;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.doubledoordev.d3core.util.ID3Mod;
import net.doubledoordev.p2sblocks.block.P2SBlock;
import net.doubledoordev.p2sblocks.block.P2STileEntity;
import net.doubledoordev.p2sblocks.network.BlockBreakMessage;
import net.doubledoordev.p2sblocks.network.HandshakeMessage;
import net.doubledoordev.p2sblocks.util.ServerHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static net.doubledoordev.p2sblocks.util.Constants.*;

/**
 * @author Dries007
 */
@Mod(modid = MODID, name = NAME)
public class P2SBlocks implements ID3Mod
{
    @Mod.Instance(MODID)
    public static P2SBlocks instance;
    public Logger logger;
    public Configuration config;
    public SimpleNetworkWrapper snw;
    public P2SBlock p2SBlock;
    public boolean serverHasP2S;
    public boolean hasP2S;
    @Mod.Metadata(MODID)
    private ModMetadata metadata;

    public float bedrockChance;
    public float hardnessWithP2S;
    public float hardnessWithoutP2S;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());

        GameRegistry.registerBlock(p2SBlock = new P2SBlock(), MODID_LOWERCASE);
        GameRegistry.registerTileEntity(P2STileEntity.class, "p2sBlockTE");

        MinecraftForge.EVENT_BUS.register(ServerHandler.I);
        FMLCommonHandler.instance().bus().register(ServerHandler.I);

        int id = 0;
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        snw.registerMessage(HandshakeMessage.Handler.class, HandshakeMessage.class, id++, Side.SERVER);
        snw.registerMessage(HandshakeMessage.Handler.class, HandshakeMessage.class, id++, Side.CLIENT);
        snw.registerMessage(BlockBreakMessage.Handler.class, BlockBreakMessage.class, id++, Side.CLIENT);

        syncConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        hasP2S = Loader.isModLoaded(P2S_MODID);

        if (hasP2S) logger.info("Pay2Spawn detected, breaking blocks will trigger rewards.");
        else logger.warn("Pay2Spawn was not found, blocks you do will do nothing.");
    }


    @Override
    public void syncConfig()
    {
        config.addCustomCategoryComment(MODID_LOWERCASE, "Add a recipe with minetweaker!");

        //public float getFloat(String name, String category, float defaultValue, float minValue, float maxValue, String comment)
        bedrockChance = config.getFloat("bedrockChance", MODID_LOWERCASE, 0.01f, 0.0f, 1.0f, "The chance that a p2sblock placed turns unbreakable.");
        hardnessWithP2S = config.getFloat("hardnessWithP2S", MODID_LOWERCASE, 1f, 0.0f, 1.0f, "The block hardness for people with p2s. 1 breaks instantly, 0 doesn't break at all.");
        hardnessWithoutP2S = config.getFloat("hardnessWithoutP2S", MODID_LOWERCASE, 0.1f, 0.0f, 1.0f, "The block hardness for people without p2s. 1 breaks instantly, 0 doesn't break at all.");

        if (config.hasChanged()) config.save();
    }

    @Override
    public void addConfigElements(List<IConfigElement> list)
    {
        list.add(new ConfigElement(config.getCategory(MODID_LOWERCASE)));
    }
}
