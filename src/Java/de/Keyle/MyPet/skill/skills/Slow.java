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

import de.Keyle.MyPet.skill.MyPetGenericSkill;
import de.Keyle.MyPet.skill.MyPetSkillTreeSkill;
import de.Keyle.MyPet.skill.SkillName;
import de.Keyle.MyPet.skill.SkillProperties;
import de.Keyle.MyPet.skill.SkillProperties.NBTdatatypes;
import de.Keyle.MyPet.util.MyPetLanguage;
import de.Keyle.MyPet.util.MyPetUtil;
import org.spout.nbt.IntTag;
import org.spout.nbt.StringTag;

import java.util.Random;

@SkillName("Slow")
@SkillProperties(
        parameterNames = {"chance", "duration", "addset_chance", "addset_duration"},
        parameterTypes = {NBTdatatypes.Int, NBTdatatypes.Int, NBTdatatypes.String, NBTdatatypes.String},
        parameterDefaultValues = {"5", "3", "add", "add"})
public class Slow extends MyPetGenericSkill
{
    private int chance = 0;
    private int duration = 0;
    private static Random random = new Random();

    public Slow(boolean addedByInheritance)
    {
        super(addedByInheritance);
    }

    @Override
    public boolean isActive()
    {
        return chance > 0 && duration > 0;
    }

    @Override
    public void upgrade(MyPetSkillTreeSkill upgrade, boolean quiet)
    {
        if (upgrade instanceof Slow)
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
            if (upgrade.getProperties().getValue().containsKey("duration"))
            {
                if (!upgrade.getProperties().getValue().containsKey("addset_duration") || ((StringTag) upgrade.getProperties().getValue().get("addset_duration")).getValue().equals("add"))
                {
                    duration += ((IntTag) upgrade.getProperties().getValue().get("duration")).getValue();
                }
                else
                {
                    duration = ((IntTag) upgrade.getProperties().getValue().get("duration")).getValue();
                }
                valuesEdit = true;
            }
            if (!quiet && valuesEdit)
            {
                myPet.sendMessageToOwner(MyPetUtil.setColors(MyPetLanguage.getString("Msg_SlowChance")).replace("%petname%", myPet.petName).replace("%chance%", "" + chance).replace("%duration%", "" + duration));
            }
        }
    }

    @Override
    public String getFormattedValue()
    {
        return chance + "% -> " + duration + "sec";
    }

    public void reset()
    {
        chance = 0;
        duration = 0;
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
        if (getProperties().getValue().containsKey("duration"))
        {
            int duration = ((IntTag) getProperties().getValue().get("duration")).getValue();
            html = html.replace("duration\" value=\"0\"", "duration\" value=\"" + duration + "\"");
            if (getProperties().getValue().containsKey("addset_duration"))
            {
                if (((StringTag) getProperties().getValue().get("addset_duration")).getValue().equals("set"))
                {
                    html = html.replace("name=\"addset_duration\" value=\"add\" checked", "name=\"addset_duration\" value=\"add\"");
                    html = html.replace("name=\"addset_duration\" value=\"set\"", "name=\"addset_duration\" value=\"set\" checked");
                }
            }
        }
        return html;
    }

    public boolean getSlow()
    {
        return random.nextDouble() <= chance / 100.;
    }

    public int getDuration()
    {
        return duration;
    }

    @Override
    public MyPetSkillTreeSkill cloneSkill()
    {
        MyPetSkillTreeSkill newSkill = new Slow(this.isAddedByInheritance());
        newSkill.setProperties(getProperties());
        return newSkill;
    }
}