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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.Field;

/**
 * @author Dries007
 */
public class P2STileEntity extends TileEntity
{
    public static final Field FIELD_148860_E = getField_148860_e();

    public float  hardness = -1f;
    public double amount   = 0;
    public String name     = null;
    public String note     = null;

    public P2STileEntity()
    {

    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, -1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        try
        {
            this.readFromNBT((NBTTagCompound) FIELD_148860_E.get(pkt));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("hardness")) this.hardness = tag.getFloat("hardness");
        if (tag.hasKey("amount")) this.amount = tag.getDouble("amount");
        if (tag.hasKey("name")) this.name = tag.getString("name");
        if (tag.hasKey("note")) this.name = tag.getString("note");
        super.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        if (this.hardness != -1) tag.setFloat("hardness", this.hardness);
        if (this.amount != 0) tag.setDouble("amount", this.amount);
        if (this.name != null) tag.setString("name", this.name);
        if (this.note != null) tag.setString("note", this.note);
        super.writeToNBT(tag);
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    public static Field getField_148860_e()
    {
        for (Field f : S35PacketUpdateTileEntity.class.getDeclaredFields())
        {
            if (f.getType() == NBTTagCompound.class)
            {
                f.setAccessible(true);
                return f;
            }
        }
        throw new RuntimeException("No NBTTagCompound field in S35PacketUpdateTileEntity. This is an unrecoverable error.");
    }
}
