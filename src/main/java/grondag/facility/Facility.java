/*******************************************************************************
 * Copyright 2019, 2020 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility;

/**
 * TODO: Things and stuff
 *
 * -- near releases
 * inserter
 * basic pump
 * basic tank
 * controller/access block
 * crafting block
 * signal station
 * config screen
 * implement multiblock limits
 *
 * -- maybe ever
 * throttle tick times
 * explicit device disconnect handling to allow retaining storage instances without wrapping
 * article metadata loader
 *
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import grondag.facility.init.BinBlocks;
import grondag.facility.init.CrateBlocks;
import grondag.facility.init.PipeBlocks;
import grondag.facility.init.ScreenHandlers;
import grondag.facility.init.TankBlocks;
import grondag.facility.init.Textures;
import grondag.facility.packet.BinActionC2S;
import grondag.facility.storage.TrackedBlockEntity;
import grondag.fermion.registrar.Registrar;
import grondag.fluidity.impl.article.ArticleTypeRegistryImpl;

public class Facility implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger("Facility");
	public static final String MODID = "facility";
	public static Registrar REG  = new Registrar(MODID, "hyper_crate");

	@Override
	public void onInitialize() {
		FacilityConfig.init();
		ScreenHandlers.values();
		Textures.values();
		CrateBlocks.values();
		BinBlocks.values();
		PipeBlocks.values();
		TankBlocks.values();
		ArticleTypeRegistryImpl.init();
		ServerPlayNetworking.registerGlobalReceiver(BinActionC2S.ID, BinActionC2S::accept);

		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be, w) -> {
			if (be instanceof TrackedBlockEntity) ((TrackedBlockEntity) be).onLoaded();
		});

		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be, w) -> {
			if (be instanceof TrackedBlockEntity) ((TrackedBlockEntity) be).onUnloaded();
		});
	}

	public static final Material CRATE_MATERIAL = (new Material.Builder(MaterialColor.WOOD)).build();
	public static Tag<Item> STORAGE_BLACKLIST_WITH_CONTENT = REG.itemTag("storage_blacklist_with_content");
	public static Tag<Item> STORAGE_BLACKLIST_ALWAYS = REG.itemTag("storage_blacklist_always");
}
