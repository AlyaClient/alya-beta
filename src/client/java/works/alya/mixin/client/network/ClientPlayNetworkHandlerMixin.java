///*
// * Copyright (c) Alya Client 2024-2025.
// *
// * This file belongs to Alya Client,
// * an open-source Fabric injection client.
// * Rye GitHub: https://github.com/AlyaClient/alya-beta.git
// *
// * THIS PROJECT DOES NOT HAVE A WARRANTY.
// *
// * Alya (and subsequently, its files) are all licensed under the MIT License.
// * Alya should have come with a copy of the MIT License.
// * If it did not, you may obtain a copy here:
// * MIT License: https://opensource.org/license/mit
// *
// */
//
//package works.alya.mixin.client.network;
//
//import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
//import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
//import org.spongepowered.asm.mixin.Unique;
//import works.alya.AlyaClient;
//import works.alya.event.impl.PacketReceiveEvent;
//import net.minecraft.client.network.ClientPlayNetworkHandler;
//import net.minecraft.network.packet.s2c.play.*;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(ClientPlayNetworkHandler.class)
//public class ClientPlayNetworkHandlerMixin {
//
//    @Unique
//    private void dispatchPacketEvent(Object packet, CallbackInfo ci) {
//        PacketReceiveEvent event = new PacketReceiveEvent((net.minecraft.network.packet.Packet<?>) packet);
//        AlyaClient.getEventBus().dispatch(event);
//
//        if(event.isCanceled()) {
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "onGameJoin", at = @At("HEAD"), cancellable = true)
//    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
//    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
//    private void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityTrackerUpdate", at = @At("HEAD"), cancellable = true)
//    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityPositionSync", at = @At("HEAD"), cancellable = true)
//    private void onEntityPositionSync(EntityPositionSyncS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityPosition", at = @At("HEAD"), cancellable = true)
//    private void onEntityPosition(EntityPositionS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onUpdateTickRate", at = @At("HEAD"), cancellable = true)
//    private void onUpdateTickRate(UpdateTickRateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onTickStep", at = @At("HEAD"), cancellable = true)
//    private void onTickStep(TickStepS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"), cancellable = true)
//    private void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntity", at = @At("HEAD"), cancellable = true)
//    private void onEntity(EntityS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onMoveMinecartAlongTrack", at = @At("HEAD"), cancellable = true)
//    private void onMoveMinecartAlongTrack(MoveMinecartAlongTrackS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntitySetHeadYaw", at = @At("HEAD"), cancellable = true)
//    private void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntitiesDestroy", at = @At("HEAD"), cancellable = true)
//    private void onEntitiesDestroy(EntitiesDestroyS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"), cancellable = true)
//    private void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerRotation", at = @At("HEAD"), cancellable = true)
//    private void onPlayerRotation(PlayerRotationS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChunkDeltaUpdate", at = @At("HEAD"), cancellable = true)
//    private void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChunkData", at = @At("HEAD"), cancellable = true)
//    private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChunkBiomeData", at = @At("HEAD"), cancellable = true)
//    private void onChunkBiomeData(ChunkBiomeDataS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onUnloadChunk", at = @At("HEAD"), cancellable = true)
//    private void onUnloadChunk(UnloadChunkS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onBlockUpdate", at = @At("HEAD"), cancellable = true)
//    private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEnterReconfiguration", at = @At("HEAD"), cancellable = true)
//    private void onEnterReconfiguration(EnterReconfigurationS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"), cancellable = true)
//    private void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
//    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
//    private void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onProfilelessChatMessage", at = @At("HEAD"), cancellable = true)
//    private void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onRemoveMessage", at = @At("HEAD"), cancellable = true)
//    private void onRemoveMessage(RemoveMessageS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityAnimation", at = @At("HEAD"), cancellable = true)
//    private void onEntityAnimation(EntityAnimationS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onDamageTilt", at = @At("HEAD"), cancellable = true)
//    private void onDamageTilt(DamageTiltS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"), cancellable = true)
//    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerSpawnPosition", at = @At("HEAD"), cancellable = true)
//    private void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityPassengersSet", at = @At("HEAD"), cancellable = true)
//    private void onEntityPassengersSet(EntityPassengersSetS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityAttach", at = @At("HEAD"), cancellable = true)
//    private void onEntityAttach(EntityAttachS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityStatus", at = @At("HEAD"), cancellable = true)
//    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityDamage", at = @At("HEAD"), cancellable = true)
//    private void onEntityDamage(EntityDamageS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onHealthUpdate", at = @At("HEAD"), cancellable = true)
//    private void onHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onExperienceBarUpdate", at = @At("HEAD"), cancellable = true)
//    private void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerRespawn", at = @At("HEAD"), cancellable = true)
//    private void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
//    private void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onOpenHorseScreen", at = @At("HEAD"), cancellable = true)
//    private void onOpenHorseScreen(OpenHorseScreenS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
//    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
//    private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSetCursorItem", at = @At("HEAD"), cancellable = true)
//    private void onSetCursorItem(SetCursorItemS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSetPlayerInventory", at = @At("HEAD"), cancellable = true)
//    private void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
//    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSignEditorOpen", at = @At("HEAD"), cancellable = true)
//    private void onSignEditorOpen(SignEditorOpenS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onBlockEntityUpdate", at = @At("HEAD"), cancellable = true)
//    private void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onScreenHandlerPropertyUpdate", at = @At("HEAD"), cancellable = true)
//    private void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityEquipmentUpdate", at = @At("HEAD"), cancellable = true)
//    private void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
//    private void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onBlockEvent", at = @At("HEAD"), cancellable = true)
//    private void onBlockEvent(BlockEventS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
//    private void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onGameStateChange", at = @At("HEAD"), cancellable = true)
//    private void onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onMapUpdate", at = @At("HEAD"), cancellable = true)
//    private void onMapUpdate(MapUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldEvent", at = @At("HEAD"), cancellable = true)
//    private void onWorldEvent(WorldEventS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onAdvancements", at = @At("HEAD"), cancellable = true)
//    private void onAdvancements(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSelectAdvancementTab", at = @At("HEAD"), cancellable = true)
//    private void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onCommandTree", at = @At("HEAD"), cancellable = true)
//    private void onCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onStopSound", at = @At("HEAD"), cancellable = true)
//    private void onStopSound(StopSoundS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onCommandSuggestions", at = @At("HEAD"), cancellable = true)
//    private void onCommandSuggestions(CommandSuggestionsS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSynchronizeRecipes", at = @At("HEAD"), cancellable = true)
//    private void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onLookAt", at = @At("HEAD"), cancellable = true)
//    private void onLookAt(LookAtS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onNbtQueryResponse", at = @At("HEAD"), cancellable = true)
//    private void onNbtQueryResponse(NbtQueryResponseS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onStatistics", at = @At("HEAD"), cancellable = true)
//    private void onStatistics(StatisticsS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onRecipeBookAdd", at = @At("HEAD"), cancellable = true)
//    private void onRecipeBookAdd(RecipeBookAddS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onRecipeBookRemove", at = @At("HEAD"), cancellable = true)
//    private void onRecipeBookRemove(RecipeBookRemoveS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onRecipeBookSettings", at = @At("HEAD"), cancellable = true)
//    private void onRecipeBookSettings(RecipeBookSettingsS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityStatusEffect", at = @At("HEAD"), cancellable = true)
//    private void onEntityStatusEffect(EntityStatusEffectS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSynchronizeTags", at = @At("HEAD"), cancellable = true)
//    private void onSynchronizeTags(SynchronizeTagsS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEndCombat", at = @At("HEAD"), cancellable = true)
//    private void onEndCombat(EndCombatS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEnterCombat", at = @At("HEAD"), cancellable = true)
//    private void onEnterCombat(EnterCombatS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onDeathMessage", at = @At("HEAD"), cancellable = true)
//    private void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onDifficulty", at = @At("HEAD"), cancellable = true)
//    private void onDifficulty(DifficultyS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSetCameraEntity", at = @At("HEAD"), cancellable = true)
//    private void onSetCameraEntity(SetCameraEntityS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldBorderInitialize", at = @At("HEAD"), cancellable = true)
//    private void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldBorderCenterChanged", at = @At("HEAD"), cancellable = true)
//    private void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldBorderInterpolateSize", at = @At("HEAD"), cancellable = true)
//    private void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldBorderSizeChanged", at = @At("HEAD"), cancellable = true)
//    private void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldBorderWarningBlocksChanged", at = @At("HEAD"), cancellable = true)
//    private void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWorldBorderWarningTimeChanged", at = @At("HEAD"), cancellable = true)
//    private void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onTitleClear", at = @At("HEAD"), cancellable = true)
//    private void onTitleClear(ClearTitleS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onServerMetadata", at = @At("HEAD"), cancellable = true)
//    private void onServerMetadata(ServerMetadataS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChatSuggestions", at = @At("HEAD"), cancellable = true)
//    private void onChatSuggestions(ChatSuggestionsS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onOverlayMessage", at = @At("HEAD"), cancellable = true)
//    private void onOverlayMessage(OverlayMessageS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
//    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSubtitle", at = @At("HEAD"), cancellable = true)
//    private void onSubtitle(SubtitleS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onTitleFade", at = @At("HEAD"), cancellable = true)
//    private void onTitleFade(TitleFadeS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerListHeader", at = @At("HEAD"), cancellable = true)
//    private void onPlayerListHeader(PlayerListHeaderS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onRemoveEntityStatusEffect", at = @At("HEAD"), cancellable = true)
//    private void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerRemove", at = @At("HEAD"), cancellable = true)
//    private void onPlayerRemove(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerList", at = @At("HEAD"), cancellable = true)
//    private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerAbilities", at = @At("HEAD"), cancellable = true)
//    private void onPlayerAbilities(PlayerAbilitiesS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
//    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlaySoundFromEntity", at = @At("HEAD"), cancellable = true)
//    private void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onBossBar", at = @At("HEAD"), cancellable = true)
//    private void onBossBar(BossBarS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onCooldownUpdate", at = @At("HEAD"), cancellable = true)
//    private void onCooldownUpdate(CooldownUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onVehicleMove", at = @At("HEAD"), cancellable = true)
//    private void onVehicleMove(VehicleMoveS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onOpenWrittenBook", at = @At("HEAD"), cancellable = true)
//    private void onOpenWrittenBook(OpenWrittenBookS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onScoreboardObjectiveUpdate", at = @At("HEAD"), cancellable = true)
//    private void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onScoreboardScoreUpdate", at = @At("HEAD"), cancellable = true)
//    private void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onScoreboardScoreReset", at = @At("HEAD"), cancellable = true)
//    private void onScoreboardScoreReset(ScoreboardScoreResetS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onScoreboardDisplay", at = @At("HEAD"), cancellable = true)
//    private void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onTeam", at = @At("HEAD"), cancellable = true)
//    private void onTeam(TeamS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
//    private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onEntityAttributes", at = @At("HEAD"), cancellable = true)
//    private void onEntityAttributes(EntityAttributesS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onCraftFailedResponse", at = @At("HEAD"), cancellable = true)
//    private void onCraftFailedResponse(CraftFailedResponseS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onLightUpdate", at = @At("HEAD"), cancellable = true)
//    private void onLightUpdate(LightUpdateS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSetTradeOffers", at = @At("HEAD"), cancellable = true)
//    private void onSetTradeOffers(SetTradeOffersS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChunkLoadDistance", at = @At("HEAD"), cancellable = true)
//    private void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onSimulationDistance", at = @At("HEAD"), cancellable = true)
//    private void onSimulationDistance(SimulationDistanceS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChunkRenderDistanceCenter", at = @At("HEAD"), cancellable = true)
//    private void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPlayerActionResponse", at = @At("HEAD"), cancellable = true)
//    private void onPlayerActionResponse(PlayerActionResponseS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onBundle", at = @At("HEAD"), cancellable = true)
//    private void onBundle(BundleS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onProjectilePower", at = @At("HEAD"), cancellable = true)
//    private void onProjectilePower(ProjectilePowerS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onStartChunkSend", at = @At("HEAD"), cancellable = true)
//    private void onStartChunkSend(StartChunkSendS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onChunkSent", at = @At("HEAD"), cancellable = true)
//    private void onChunkSent(ChunkSentS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onDebugSample", at = @At("HEAD"), cancellable = true)
//    private void onDebugSample(DebugSampleS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onPingResult", at = @At("HEAD"), cancellable = true)
//    private void onPingResult(PingResultS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onTestInstanceBlockStatus", at = @At("HEAD"), cancellable = true)
//    private void onTestInstanceBlockStatus(TestInstanceBlockStatusS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//
//    @Inject(method = "onWaypoint", at = @At("HEAD"), cancellable = true)
//    private void onWaypoint(WaypointS2CPacket packet, CallbackInfo ci) {
//        dispatchPacketEvent(packet, ci);
//    }
//}