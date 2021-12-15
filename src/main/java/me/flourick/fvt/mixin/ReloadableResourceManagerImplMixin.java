package me.flourick.fvt.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.FVTLanguagesPack;

import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;

import org.apache.logging.log4j.LogManager;

/**
 * Adds our custom resource pack with language definitions.
 * 
 * @author Flourick
 */
@Mixin(ReloadableResourceManagerImpl.class)
abstract class ReloadableResourceManagerImplMixin implements ReloadableResourceManager
{
	@Shadow
    abstract void addPack(ResourcePack resourcePack);

	@Inject(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void onReload(CallbackInfoReturnable<ResourceReload> info)
    {
        if(FVT.MC != null) {
            this.addPack(new FVTLanguagesPack(new File(FVT.MC.runDirectory, "config/fvt/translations")));
        }
        else {
            LogManager.getLogger().error("[FVT] Error adding translations resource pack!");
        }
    }
}
