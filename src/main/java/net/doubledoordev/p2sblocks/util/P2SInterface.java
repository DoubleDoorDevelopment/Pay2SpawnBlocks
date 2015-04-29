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

import net.doubledoordev.pay2spawn.Pay2Spawn;
import net.doubledoordev.pay2spawn.util.ClientTickHandler;
import net.doubledoordev.pay2spawn.util.Donation;
import net.doubledoordev.pay2spawn.util.Reward;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author Dries007
 */
public class P2SInterface
{
    public static void trigger()
    {
        ClientTickHandler.INSTANCE.new QueEntry(getRandomReward(), new Donation(UUID.randomUUID().toString(), 0.0d, System.currentTimeMillis(), Minecraft.getMinecraft().thePlayer.getCommandSenderName()), false, null).send();
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
        return rewards.get(randomIndex);
    }
}
