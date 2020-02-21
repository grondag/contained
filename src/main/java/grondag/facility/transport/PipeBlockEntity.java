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
package grondag.facility.transport;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import grondag.facility.block.BlockEntityUnloadCallback;
import grondag.fermion.world.WorldTaskManager;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.fluidity.wip.base.transport.SubCarrier;

public class PipeBlockEntity extends BlockEntity implements BlockEntityUnloadCallback {
	protected final SubCarrier<UtbCostFunction> carrier = new UtbSubCarrier(UniversalTransportBus.BASIC);
	public final CarrierProvider carrierProvider;
	protected final PipeMultiBlock.Member member;
	protected PipeMultiBlock owner = null;
	protected boolean isEnqued = false;
	protected boolean isRegistered = false;

	public PipeBlockEntity(BlockEntityType<? extends PipeBlockEntity> type) {
		super(type);
		member = new PipeMultiBlock.Member(this, b -> b.carrier);
		carrierProvider = createCarrierProvider();
	}

	protected CarrierProvider createCarrierProvider() {
		return SingleCarrierProvider.of(carrier);
	}

	protected final void enqueUpdate() {
		if(!isEnqued && !world.isClient) {
			isEnqued = true;
			WorldTaskManager.enqueueImmediate(this::enquedUpdate);
		}
	}

	protected void onEnquedUpdate() {

	}

	public final void enquedUpdate() {
		if(world == null || world.isClient) {
			return;
		}

		onEnquedUpdate();
		isEnqued = false;
	}

	protected void registerDevice() {
		if(!isRegistered && hasWorld() && !world.isClient) {
			PipeMultiBlock.DEVICE_MANAGER.connect(member);
			isRegistered = true;
		}
	}

	protected void unregisterDevice() {
		if(isRegistered && hasWorld() && !world.isClient) {
			PipeMultiBlock.DEVICE_MANAGER.disconnect(member);
			isRegistered = false;
		}
	}

	@Override
	public void setLocation(World world, BlockPos blockPos) {
		unregisterDevice();
		super.setLocation(world, blockPos);
		registerDevice();
		enqueUpdate();
	}

	@Override
	public void markRemoved() {
		unregisterDevice();
		super.markRemoved();
	}

	@Override
	public void onBlockEntityUnloaded() {
		unregisterDevice();
	}

	@Override
	public void cancelRemoval() {
		super.cancelRemoval();
		registerDevice();
		enqueUpdate();
	}

	public CarrierProvider getCarrierProvider(BlockComponentContext ctx) {
		return carrierProvider;
	}
}
