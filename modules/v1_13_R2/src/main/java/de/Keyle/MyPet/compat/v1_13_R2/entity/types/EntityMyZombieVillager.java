/*
 * This file is part of MyPet
 *
 * Copyright © 2011-2018 Keyle
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

package de.Keyle.MyPet.compat.v1_13_R2.entity.types;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.entity.EntitySize;
import de.Keyle.MyPet.api.entity.EquipmentSlot;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.types.MyZombieVillager;
import de.Keyle.MyPet.compat.v1_13_R2.entity.EntityMyPet;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;

@EntitySize(width = 0.6F, height = 1.9F)
public class EntityMyZombieVillager extends EntityMyPet {

    private static final DataWatcherObject<Boolean> babyWatcher = DataWatcher.a(EntityMyZombieVillager.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> typeWatcher = DataWatcher.a(EntityMyZombieVillager.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> unusedWatcher1 = DataWatcher.a(EntityMyZombieVillager.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> unusedWatcher2 = DataWatcher.a(EntityMyZombieVillager.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> shiverwatcher = DataWatcher.a(EntityMyZombieVillager.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> professionWatcher = DataWatcher.a(EntityMyZombieVillager.class, DataWatcherRegistry.b);

    public EntityMyZombieVillager(World world, MyPet myPet) {
        super(EntityTypes.ZOMBIE_VILLAGER, world, myPet);
    }

    /**
     * Returns the sound that is played when the MyPet dies
     */
    @Override
    protected String getDeathSound() {
        return "entity.zombie.death";
    }

    /**
     * Returns the sound that is played when the MyPet get hurt
     */
    @Override
    protected String getHurtSound() {
        return "entity.zombie.hurt";
    }

    /**
     * Returns the default sound of the MyPet
     */
    protected String getLivingSound() {
        return "entity.zombie.ambient";
    }

    /**
     * Is called when player rightclicks this MyPet
     * return:
     * true: there was a reaction on rightclick
     * false: no reaction on rightclick
     */
    public boolean handlePlayerInteraction(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemStack) {
        if (super.handlePlayerInteraction(entityhuman, enumhand, itemStack)) {
            return true;
        }

        if (getOwner().equals(entityhuman) && itemStack != null) {
            if (itemStack.getItem() == Items.SHEARS && getOwner().getPlayer().isSneaking() && canEquip()) {
                boolean hadEquipment = false;
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack itemInSlot = CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot));
                    if (itemInSlot != null && itemInSlot.getItem() != Items.AIR) {
                        EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY + 1, this.locZ, itemInSlot);
                        entityitem.pickupDelay = 10;
                        entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                        this.world.addEntity(entityitem);
                        getMyPet().setEquipment(slot, null);
                        hadEquipment = true;
                    }
                }
                if (hadEquipment) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemStack.damage(1, entityhuman);
                    }
                }
                return true;
            } else if (MyPetApi.getPlatformHelper().isEquipment(CraftItemStack.asBukkitCopy(itemStack)) && getOwner().getPlayer().isSneaking() && canEquip()) {
                EquipmentSlot slot = EquipmentSlot.getSlotById(e(itemStack).c());
                ItemStack itemInSlot = CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot));
                if (itemInSlot != null && itemInSlot.getItem() != Items.AIR && !entityhuman.abilities.canInstantlyBuild) {
                    EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY + 1, this.locZ, itemInSlot);
                    entityitem.pickupDelay = 10;
                    entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                    this.world.addEntity(entityitem);
                }
                getMyPet().setEquipment(slot, CraftItemStack.asBukkitCopy(itemStack));
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemStack.subtract(1);
                    if (itemStack.getCount() <= 0) {
                        entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, ItemStack.a);
                    }
                }
                return true;
            } else if (Configuration.MyPet.Zombie.GROW_UP_ITEM.compare(itemStack) && getMyPet().isBaby() && getOwner().getPlayer().isSneaking()) {
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemStack.subtract(1);
                    if (itemStack.getCount() <= 0) {
                        entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, ItemStack.a);
                    }
                }
                getMyPet().setBaby(false);
                return true;
            }
        }
        return false;
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        getDataWatcher().register(babyWatcher, false);
        getDataWatcher().register(typeWatcher, 0);
        getDataWatcher().register(unusedWatcher1, false);
        getDataWatcher().register(unusedWatcher2, false);
        getDataWatcher().register(shiverwatcher, false);
        getDataWatcher().register(professionWatcher, 0);
    }

    @Override
    public void updateVisuals() {
        this.datawatcher.set(babyWatcher, getMyPet().isBaby());
        this.datawatcher.set(professionWatcher, getMyPet().getProfession());

        Bukkit.getScheduler().runTaskLater(MyPetApi.getPlugin(), () -> {
            if (getMyPet().getStatus() == MyPet.PetState.Here) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    setPetEquipment(slot, CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot)));
                }
            }
        }, 5L);
    }

    public void playPetStepSound() {
        makeSound("entity.zombie.step", 0.15F, 1.0F);
    }

    public MyZombieVillager getMyPet() {
        return (MyZombieVillager) myPet;
    }

    public void setPetEquipment(EquipmentSlot slot, ItemStack itemStack) {
        ((WorldServer) this.world).getTracker().a(this, new PacketPlayOutEntityEquipment(getId(), EnumItemSlot.values()[slot.get19Slot()], itemStack));
    }

    public ItemStack getEquipment(EnumItemSlot vanillaSlot) {
        if (Util.findClassInStackTrace(Thread.currentThread().getStackTrace(), "net.minecraft.server." + MyPetApi.getCompatUtil().getInternalVersion() + ".EntityTrackerEntry", 2)) {
            EquipmentSlot slot = EquipmentSlot.getSlotById(vanillaSlot.c());
            if (getMyPet().getEquipment(slot) != null) {
                return CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot));
            }
        }
        return super.getEquipment(vanillaSlot);
    }
}