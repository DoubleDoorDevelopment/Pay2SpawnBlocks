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

package net.doubledoordev.p2sblocks.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.doubledoordev.p2sblocks.P2SBlocks;
import net.doubledoordev.pay2spawn.Pay2Spawn;
import net.doubledoordev.pay2spawn.random.RandomRegistry;
import net.doubledoordev.pay2spawn.util.ClientTickHandler;
import net.doubledoordev.pay2spawn.util.Donation;
import net.doubledoordev.pay2spawn.util.Reward;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author Dries007
 */
public class P2SInterface
{
    public static void trigger(NBTTagCompound tag)
    {
        Donation donation = new Donation(UUID.randomUUID().toString(), 0.0d, System.currentTimeMillis(), Minecraft.getMinecraft().thePlayer.getCommandSenderName());

        if (tag.hasKey("name")) donation.username = tag.getString("name");
        if (tag.hasKey("note")) donation.note = tag.getString("note");

        Reward reward = null;
        if (tag.hasKey("amount"))
        {
            donation.amount = tag.getDouble("amount");
            reward = getRewardFor(donation.amount);
        }

        if (reward == null) reward = getRandomReward();

        if (reward == null)
        {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("You have no rewards with amount > 0."));
            return;
        }

        ClientTickHandler.INSTANCE.new QueEntry(reward, donation, false, null).send();
    }

    public static Reward getRewardFor(double amount)
    {
        double highestMatch = -1;

        for (Double key : Pay2Spawn.getRewardsDB().getAmounts()) if (key > 0 && key < amount && highestMatch < key) highestMatch = key;
        ArrayList<Reward> rewards = new ArrayList<>(Pay2Spawn.getRewardsDB().getRewards());
        Iterator<Reward> i = rewards.iterator();
        while (i.hasNext())
        {
            if (i.next().getAmount() != highestMatch) i.remove();
        }

        return RandomRegistry.getRandomFromSet(rewards);
    }

    public static Reward getRandomReward()
    {
        ArrayList<Reward> rewards = new ArrayList<>(Pay2Spawn.getRewardsDB().getRewards());
        // Compute the total weight of all items together
        double totalWeight = 0.0d;
        // filter out <= 0$ rewards
        {
            Iterator<Reward> i = rewards.iterator();
            while (i.hasNext())
            {
                Reward reward = i.next();
                if (reward.getAmount() <= 0.0d) i.remove();
                else totalWeight += 1.0d / reward.getAmount();
            }
        }
        // Now choose a random item
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < rewards.size(); ++i)
        {
            random -= 1.0d / rewards.get(i).getAmount();
            if (random <= 0.0d)
            {
                randomIndex = i;
                break;
            }
        }

        if (randomIndex == -1) return null;

        return rewards.get(randomIndex);
    }
}
