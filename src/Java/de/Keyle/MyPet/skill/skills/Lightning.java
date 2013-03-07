/*
 * This file is part of MyPet
 *
 * Copyright (C) 2011-2013 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.skill.skills;

import de.Keyle.MyPet.entity.types.MyPet;
import de.Keyle.MyPet.skill.MyPetGenericSkill;
import de.Keyle.MyPet.skill.MyPetSkillTreeSkill;
import de.Keyle.MyPet.skill.SkillName;
import de.Keyle.MyPet.skill.SkillProperties;
import de.Keyle.MyPet.skill.SkillProperties.NBTdatatypes;
import de.Keyle.MyPet.util.MyPetLanguage;
import de.Keyle.MyPet.util.MyPetUtil;
import org.bukkit.entity.LightningStrike;
import org.spout.nbt.IntTag;
import org.spout.nbt.StringTag;

import java.util.*;

@SkillName("Lightning")
@SkillProperties(
        parameterNames = {"chance", "addset_chance"},
        parameterTypes = {NBTdatatypes.Int, NBTdatatypes.String},
        parameterDefaultValues = {"5", "add"})
public class Lightning extends MyPetGenericSkill
{
    private int chance = 0;
    private static Random random = new Random();
    public static Map<LightningStrike, MyPet> lightningList = new HashMap<LightningStrike, MyPet>();
    public static boolean isStriking = false;

    public Lightning(boolean addedByInheritance)
    {
        super(addedByInheritance);
    }

    @Override
    public boolean isActive()
    {
        return chance > 0;
    }

    @Override
    public void upgrade(MyPetSkillTreeSkill upgrade, boolean quiet)
    {
        if (upgrade instanceof Lightning)
        {
            boolean valuesEdit = false;
            if (upgrade.getProperties().getValue().containsKey("chance"))
            {
                if (!upgrade.getProperties().getValue().containsKey("addset_chance") || ((StringTag) upgrade.getProperties().getValue().get("addset_chance")).getValue().equals("add"))
                {
                    chance += ((IntTag) upgrade.getProperties().getValue().get("chance")).getValue();
                }
                else
                {
                    chance = ((IntTag) upgrade.getProperties().getValue().get("chance")).getValue();
                }
                valuesEdit = true;
            }
            if (!quiet && valuesEdit)
            {
                myPet.sendMessageToOwner(MyPetUtil.setColors(MyPetLanguage.getString("Msg_LightningChance")).replace("%petname%", myPet.petName).replace("%chance%", "" + chance));
            }
        }
    }

    @Override
    public String getFormattedValue()
    {
        return chance + "%";
    }

    public void reset()
    {
        chance = 0;
    }

    @Override
    public String getHtml()
    {
        String html = super.getHtml();
        if (getProperties().getValue().containsKey("chance"))
        {
            int chance = ((IntTag) getProperties().getValue().get("chance")).getValue();
            html = html.replace("chance\" value=\"0\"", "chance\" value=\"" + chance + "\"");
            if (getProperties().getValue().containsKey("addset_chance"))
            {
                if (((StringTag) getProperties().getValue().get("addset_chance")).getValue().equals("set"))
                {
                    html = html.replace("name=\"addset_chance\" value=\"add\" checked", "name=\"addset_chance\" value=\"add\"");
                    html = html.replace("name=\"addset_chance\" value=\"set\"", "name=\"addset_chance\" value=\"set\" checked");
                }
            }
        }
        return html;
    }

    public boolean getLightning()
    {
        return random.nextDouble() <= chance / 100.;
    }

    @Override
    public MyPetSkillTreeSkill cloneSkill()
    {
        MyPetSkillTreeSkill newSkill = new Lightning(this.isAddedByInheritance());
        newSkill.setProperties(getProperties());
        return newSkill;
    }

    public static int countLightnings()
    {
        removeDeadLightnings();
        return lightningList.size();
    }

    public static boolean isSkillLightning(LightningStrike lightningStrike)
    {
        removeDeadLightnings();
        return lightningList.containsKey(lightningStrike);
    }

    private static void removeDeadLightnings()
    {
        List<LightningStrike> deadLightningStrikes = new ArrayList<LightningStrike>();
        for (LightningStrike bolt : lightningList.keySet())
        {
            if (bolt.isDead())
            {
                deadLightningStrikes.add(bolt);
            }
        }
        for (LightningStrike bolt : deadLightningStrikes)
        {
            lightningList.remove(bolt);
        }
    }
}