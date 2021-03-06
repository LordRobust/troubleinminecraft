package io.indices.troubleinminecraft.features;

import com.google.gson.annotations.Expose;
import com.voxelgameslib.voxelgameslib.api.event.GameEvent;
import com.voxelgameslib.voxelgameslib.api.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.api.feature.Feature;
import com.voxelgameslib.voxelgameslib.api.feature.features.MapFeature;
import com.voxelgameslib.voxelgameslib.components.map.Marker;
import com.voxelgameslib.voxelgameslib.internal.lang.Lang;
import com.voxelgameslib.voxelgameslib.components.user.User;
import io.indices.troubleinminecraft.TroubleInMinecraftPlugin;
import io.indices.troubleinminecraft.game.TraitorTester;
import io.indices.troubleinminecraft.lang.TIMLangKey;
import io.indices.troubleinminecraft.team.Role;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitorTesterFeature extends AbstractFeature {

    @Inject
    private TroubleInMinecraftPlugin plugin;

    private Map<Integer, TraitorTester> testers = new HashMap<>();

    @Expose
    private int testerDelaySeconds = 3;
    @Expose
    private int testerLightGlowSeconds = 5;

    @Override
    public void enable() {
        loadTesters();
    }

    @Override
    @Nonnull
    public List<Class<? extends Feature>> getDependencies() {
        List<Class<? extends Feature>> list = new ArrayList<>();

        list.add(MapFeature.class);
        list.add(GameFeature.class);

        return list;
    }

    public void loadTesters() {
        com.voxelgameslib.voxelgameslib.components.map.Map map = getPhase().getFeature(MapFeature.class).getMap();

        Map<Integer, TraitorTester> loadedTesters = new HashMap<>();

        for (Marker marker : map.getMarkers()) {
            if (marker.getData().startsWith("tester")) {
                String[] params = marker.getData().split(":", -1);

                if (!params[1].isEmpty() && !params[2].isEmpty()) {
                    try {
                        int testerId = Integer.parseInt(params[1]);
                        String type = params[2];

                        TraitorTester tester;

                        if (loadedTesters.containsKey(testerId)) {
                            tester = loadedTesters.get(testerId);
                        } else {
                            tester = new TraitorTester();
                            loadedTesters.put(testerId, tester);
                        }

                        switch (type.toUpperCase()) {
                            case "BUTTON":
                                Location buttonLoc = marker.getLoc().toLocation(map.getWorldName());
                                tester.setButton(buttonLoc);
                                buttonLoc.getBlock().setType(Material.STONE_BUTTON);
                                break;
                            case "LIGHT":
                                Location lightLoc = marker.getLoc().toLocation(map.getWorldName());
                                tester.addLightLocation(lightLoc);
                                lightLoc.getBlock().setType(Material.LEGACY_REDSTONE_LAMP_OFF);
                                break;
                            case "BARRIER":
                                Location barrierLoc = marker.getLoc().toLocation(map.getWorldName());
                                tester.addBarierLocation(barrierLoc);
                                barrierLoc.getBlock().setType(Material.AIR);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        // invalid marker, continue
                    }
                }
            }
        }

        this.testers = loadedTesters;
    }

    @GameEvent
    public void onTest(@Nonnull PlayerInteractEvent event, @Nonnull User interactor) { // is interactor even a word???
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getMaterial() == Material.STONE_BUTTON) {
            testers.forEach((id, tester) -> {
                if (tester.getButton().equals(event.getClickedBlock().getLocation())) {
                    if (tester.isInUse()) {
                        return;
                    }

                    // time for the moment of truth, are you a T?

                    tester.setInUse(true);
                    tester.getBarriers().forEach(loc -> loc.getBlock().setType(Material.GLASS));
                    getPhase().getGame().getAllUsers().forEach(user -> Lang.msg(user, TIMLangKey.X_HAS_ENTERED_THE_TRAITOR_TESTER, interactor.getDisplayName()));

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Role interactorRole = getPhase().getFeature(GameFeature.class).getRole(interactor);
                            if (interactorRole != null && interactorRole == Role.TRAITOR) {
                                tester.getLights().forEach(loc -> loc.getBlock().setType(Material.LEGACY_REDSTONE_LAMP_ON));
                                tester.getBarriers().forEach(loc -> loc.getBlock().setType(Material.AIR));

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        tester.getLights().forEach(loc -> loc.getBlock().setType(Material.LEGACY_REDSTONE_LAMP_OFF));
                                    }
                                }.runTaskLater(plugin, testerLightGlowSeconds * 20);
                            }
                        }

                    }.runTaskLater(plugin, testerDelaySeconds * 20);
                }
            });
        }
    }
}
