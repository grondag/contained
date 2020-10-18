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
package grondag.facility.storage.bulk;

import grondag.facility.storage.StorageClientState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.texture.Sprite;

public class TankClientState extends StorageClientState<TankBlockEntity> {
	public @Nullable Sprite fluidSprite;
	public int fluidColor = -1;
	public float level;
	public boolean glowing;

	public TankClientState(TankBlockEntity owner) {
		super(owner);
	}
}
