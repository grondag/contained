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
package grondag.facility.storage.item;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import grondag.facility.storage.StorageBlockEntity;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;

public class CrateBlockEntity extends StorageBlockEntity<CrateClientState, CrateMultiBlock.Member> {
	public CrateBlockEntity(BlockEntityType<? extends CrateBlockEntity> type, BlockPos pos, BlockState state, @SuppressWarnings("rawtypes") Supplier<AbstractStore> storageSupplier, String labelRoot) {
		super(type, pos, state, storageSupplier, labelRoot);
	}

	@Override
	protected CrateMultiBlock.Member createMember() {
		return new CrateMultiBlock.Member(this, b -> b.getInternalStorage());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected MultiBlockManager deviceManager() {
		return CrateMultiBlock.DEVICE_MANAGER;
	}

	@Override
	protected CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.getAccess(be).applyIfPresent(neighborSide, p ->
		p.attachIfPresent(ArticleType.ITEM, ct -> ct.getAccess(this)));
	}

	@Override
	protected CrateClientState createClientState() {
		return new CrateClientState(this);
	}
}
