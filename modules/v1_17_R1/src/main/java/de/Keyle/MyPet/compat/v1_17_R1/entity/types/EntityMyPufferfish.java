/*
 * This file is part of MyPet
 *
 * Copyright © 2011-2020 Keyle
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

package de.Keyle.MyPet.compat.v1_17_R1.entity.types;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.compat.ParticleCompat;
import de.Keyle.MyPet.api.entity.EntitySize;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.types.MyPufferfish;
import de.Keyle.MyPet.compat.v1_17_R1.entity.EntityMyAquaticPet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;

@EntitySize(width = 0.5F, height = 0.5f)
public class EntityMyPufferfish extends EntityMyAquaticPet {

	private static final EntityDataAccessor<Boolean> FROM_BUCKET_WATCHER = SynchedEntityData.defineId(EntityMyPufferfish.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> PUFF_WATCHER = SynchedEntityData.defineId(EntityMyPufferfish.class, EntityDataSerializers.INT);

	public EntityMyPufferfish(Level world, MyPet myPet) {
		super(world, myPet);
	}

	@Override
	protected String getMyPetDeathSound() {
		return "entity.puffer_fish.death";
	}

	@Override
	protected String getHurtSound() {
		return "entity.puffer_fish.hurt";
	}

	@Override
	protected String getLivingSound() {
		return "entity.puffer_fish.ambient";
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!isInWater() && this.random.nextBoolean()) {
			MyPetApi.getPlatformHelper().playParticleEffect(myPet.getLocation().get().add(0, 0.7, 0), ParticleCompat.WATER_SPLASH.get(), 0.2F, 0.2F, 0.2F, 0.5F, 10, 20);
		}
	}

	@Override
	public MyPufferfish getMyPet() {
		return (MyPufferfish) myPet;
	}

	@Override
	public void updateVisuals() {
		getEntityData().set(PUFF_WATCHER, getMyPet().getPuffState().ordinal());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		getEntityData().define(FROM_BUCKET_WATCHER, false);
		getEntityData().define(PUFF_WATCHER, 0);
	}
}
