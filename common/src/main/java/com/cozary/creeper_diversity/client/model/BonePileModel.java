package com.cozary.creeper_diversity.client.model;

import com.cozary.creeper_diversity.client.renderer.state.BonePileRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class BonePileModel extends EntityModel<BonePileRenderState> {
    private final ModelPart head;

    public BonePileModel(ModelPart root) {
        super(root);
        this.head = root;
    }

    @Override
    public void setupAnim(BonePileRenderState state) {
        super.setupAnim(state);
        this.head.xRot = -0.3F;
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;
        this.head.y = 20.0F;

        if (state.remainingTicks < 50) {
            this.head.x = Mth.sin(state.ageInTicks * 2.0F) * 0.5F;
            this.head.z = Mth.cos(state.ageInTicks * 2.0F) * 0.5F;
        } else {
            this.head.x = 0.0F;
            this.head.z = 0.0F;
        }
    }
}
