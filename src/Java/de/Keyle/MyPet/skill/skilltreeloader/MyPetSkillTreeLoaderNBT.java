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

package de.Keyle.MyPet.skill.skilltreeloader;


import de.Keyle.MyPet.entity.types.MyPetType;
import de.Keyle.MyPet.skill.*;
import de.Keyle.MyPet.util.MyPetUtil;
import de.Keyle.MyPet.util.configuration.NBT_Configuration;
import org.spout.nbt.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyPetSkillTreeLoaderNBT extends MyPetSkillTreeLoader
{
    public static MyPetSkillTreeLoaderNBT getSkilltreeLoader()
    {
        return new MyPetSkillTreeLoaderNBT();
    }

    private MyPetSkillTreeLoaderNBT()
    {
    }

    public void loadSkillTrees(String configPath)
    {
        loadSkillTrees(configPath, true);
    }

    public void loadSkillTrees(String configPath, boolean applyDefaultAndInheritance)
    {
        NBT_Configuration skilltreeConfig;
        if (MyPetUtil.getDebugLogger() != null)
        {
            MyPetUtil.getDebugLogger().info("Loading nbt skill configs in: " + configPath);
        }
        File skillFile;

        skillFile = new File(configPath + File.separator + "default.st");
        MyPetSkillTreeMobType skillTreeMobType = MyPetSkillTreeMobType.getMobTypeByName("default");
        if (skillFile.exists())
        {
            skilltreeConfig = new NBT_Configuration(skillFile);
            loadSkillTree(skilltreeConfig, skillTreeMobType, applyDefaultAndInheritance);
            if (MyPetUtil.getDebugLogger() != null)
            {
                MyPetUtil.getDebugLogger().info("  default.st");
            }
        }

        for (MyPetType mobType : MyPetType.values())
        {
            skillFile = new File(configPath + File.separator + mobType.getTypeName().toLowerCase() + ".st");

            skillTreeMobType = MyPetSkillTreeMobType.getMobTypeByName(mobType.getTypeName());

            if (!skillFile.exists())
            {
                if (applyDefaultAndInheritance)
                {
                    if (!skillTreeMobType.getMobTypeName().equals("default"))
                    {
                        addDefault(skillTreeMobType);
                    }
                    manageInheritance(skillTreeMobType);
                }
                continue;
            }

            skilltreeConfig = new NBT_Configuration(skillFile);
            loadSkillTree(skilltreeConfig, skillTreeMobType, applyDefaultAndInheritance);
            if (MyPetUtil.getDebugLogger() != null)
            {
                MyPetUtil.getDebugLogger().info("  " + mobType.getTypeName().toLowerCase() + ".st");
            }
            skillTreeMobType.cleanupPlaces();
        }
    }

    protected void loadSkillTree(NBT_Configuration nbtConfiguration, MyPetSkillTreeMobType skillTreeMobType, boolean applyDefaultAndInheritance)
    {
        IntTag intTag;

        nbtConfiguration.load();
        ListTag skilltreeList = (ListTag) nbtConfiguration.getNBTCompound().getValue().get("Skilltrees");
        for (int i_skilltree = 0 ; i_skilltree < skilltreeList.getValue().size() ; i_skilltree++)
        {
            CompoundTag skilltreeCompound = (CompoundTag) skilltreeList.getValue().get(i_skilltree);
            MyPetSkillTree skillTree = new MyPetSkillTree(((StringTag) skilltreeCompound.getValue().get("Name")).getValue());

            intTag = (IntTag) skilltreeCompound.getValue().get("Place");
            int place = intTag.getValue();

            if (skilltreeCompound.getValue().containsKey("Inherits"))
            {
                skillTree.setInheritance(((StringTag) skilltreeCompound.getValue().get("Inherits")).getValue());
            }
            if (skilltreeCompound.getValue().containsKey("Permission"))
            {
                skillTree.setPermission(((StringTag) skilltreeCompound.getValue().get("Permission")).getValue());
            }
            if (skilltreeCompound.getValue().containsKey("Display"))
            {
                skillTree.setDisplayName(((StringTag) skilltreeCompound.getValue().get("Display")).getValue());
            }

            ListTag levelList = (ListTag) skilltreeCompound.getValue().get("Level");
            for (int i_level = 0 ; i_level < levelList.getValue().size() ; i_level++)
            {
                CompoundTag levelCompound = (CompoundTag) levelList.getValue().get(i_level);
                short thisLevel = ((ShortTag) levelCompound.getValue().get("Level")).getValue();
                skillTree.addLevel(thisLevel);

                ListTag skillList = (ListTag) levelCompound.getValue().get("Skills");
                for (int i_skill = 0 ; i_skill < skillList.getValue().size() ; i_skill++)
                {
                    CompoundTag skillCompound = (CompoundTag) skillList.getValue().get(i_skill);
                    String skillName = ((StringTag) skillCompound.getValue().get("Name")).getValue();
                    if (MyPetSkills.isValidSkill(skillName))
                    {
                        CompoundTag skillPropertyCompound = (CompoundTag) skillCompound.getValue().get("Properties");
                        MyPetSkillTreeSkill skill = MyPetSkills.getNewSkillInstance(skillName);
                        skill.setProperties(skillPropertyCompound);
                        skill.setDefaultProperties();
                        skillTree.addSkillToLevel(thisLevel, skill);
                    }
                }
            }
            skillTreeMobType.addSkillTree(skillTree, place);
        }
        if (applyDefaultAndInheritance)
        {
            if (!skillTreeMobType.getMobTypeName().equals("default"))
            {
                addDefault(skillTreeMobType);
            }
            manageInheritance(skillTreeMobType);
        }
    }

    public List<String> saveSkillTrees(String configPath)
    {
        NBT_Configuration nbtConfig;
        File skillFile;
        List<String> savedPetTypes = new ArrayList<String>();

        for (MyPetType petType : MyPetType.values())
        {
            skillFile = new File(configPath + File.separator + petType.getTypeName().toLowerCase() + ".st");
            nbtConfig = new NBT_Configuration(skillFile);
            if (saveSkillTree(nbtConfig, petType.getTypeName()))
            {
                savedPetTypes.add(petType.getTypeName());
            }
        }

        skillFile = new File(configPath + File.separator + "default.st");
        nbtConfig = new NBT_Configuration(skillFile);
        if (saveSkillTree(nbtConfig, "default"))
        {
            savedPetTypes.add("default");
        }

        return savedPetTypes;
    }

    protected boolean saveSkillTree(NBT_Configuration nbtConfiguration, String petTypeName)
    {
        boolean saveMobType = false;

        if (MyPetSkillTreeMobType.getMobTypeByName(petTypeName).getSkillTreeNames().size() != 0)
        {
            MyPetSkillTreeMobType mobType = MyPetSkillTreeMobType.getMobTypeByName(petTypeName);
            mobType.cleanupPlaces();

            List<CompoundTag> skilltreeList = new ArrayList<CompoundTag>();
            for (MyPetSkillTree skillTree : mobType.getSkillTrees())
            {
                CompoundTag skilltreeCompound = new CompoundTag(skillTree.getName(), new CompoundMap());
                skilltreeCompound.getValue().put("Name", new StringTag("Name", skillTree.getName()));
                skilltreeCompound.getValue().put("Place", new IntTag("Place", mobType.getSkillTreePlace(skillTree)));
                if (skillTree.hasInheritance())
                {
                    skilltreeCompound.getValue().put("Inherits", new StringTag("Inherits", skillTree.getInheritance()));
                }
                if (skillTree.hasCustomPermissions())
                {
                    skilltreeCompound.getValue().put("Permission", new StringTag("Permission", skillTree.getPermission()));
                }
                if (skillTree.hasDisplayName())
                {
                    skilltreeCompound.getValue().put("Display", new StringTag("Display", skillTree.getDisplayName()));
                }

                List<CompoundTag> levelList = new ArrayList<CompoundTag>();
                for (MyPetSkillTreeLevel level : skillTree.getLevelList())
                {
                    CompoundTag levelCompound = new CompoundTag("" + level.getLevel(), new CompoundMap());
                    levelCompound.getValue().put("Level", new ShortTag("Level", level.getLevel()));

                    List<CompoundTag> skillList = new ArrayList<CompoundTag>();
                    for (MyPetSkillTreeSkill skill : skillTree.getLevel(level.getLevel()).getSkills())
                    {
                        if (!skill.isAddedByInheritance())
                        {
                            CompoundTag skillCompound = new CompoundTag(skill.getName(), new CompoundMap());
                            skillCompound.getValue().put("Name", new StringTag("Name", skill.getName()));
                            skillCompound.getValue().put("Properties", skill.getProperties());

                            skillList.add(skillCompound);
                        }
                    }
                    levelCompound.getValue().put("Name", new ListTag<CompoundTag>("Skills", CompoundTag.class, skillList));
                    levelList.add(levelCompound);
                }
                skilltreeCompound.getValue().put("Level", new ListTag<CompoundTag>("Levels", CompoundTag.class, levelList));
                skilltreeList.add(skilltreeCompound);
            }
            nbtConfiguration.getNBTCompound().getValue().put("Skilltrees", new ListTag<CompoundTag>("Skilltrees", CompoundTag.class, skilltreeList));

            if (mobType.getSkillTreeNames().size() > 0)
            {
                nbtConfiguration.save();
                saveMobType = true;
            }
        }
        return saveMobType;
    }
}