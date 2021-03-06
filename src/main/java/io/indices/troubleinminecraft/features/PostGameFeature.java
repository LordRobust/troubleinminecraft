package io.indices.troubleinminecraft.features;


import com.voxelgameslib.voxelgameslib.components.user.User;
import io.indices.troubleinminecraft.game.DeadPlayer;
import io.indices.troubleinminecraft.game.TIMData;
import io.indices.troubleinminecraft.team.Role;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostGameFeature extends com.voxelgameslib.voxelgameslib.api.feature.features.PostGameFeature {

    private Map<Entity, DeadPlayer> zombiePlayerMap = new HashMap<>();
    private Role winner;

    @Override
    public void enable() {
        super.enable();

        TIMData timData = getPhase().getGame().getGameData(TIMData.class).orElse(new TIMData());
        // just to keep events going
        zombiePlayerMap = timData.getZombiePlayerMap();

        if (timData.getWinner() != null) {
            this.winner = timData.getWinner();
            TextColor textColor;

            if (this.winner == Role.TRAITOR) {
                textColor = Role.TRAITOR.getTextColour();
            } else {
                textColor = Role.INNOCENT.getTextColour();
            }

            List<User> traitors;
            if (timData.getTraitors() != null) {
                traitors = timData.getTraitors().getPlayers();
            } else {
                traitors = new ArrayList<>();
            }

            String traitorListString = traitors.stream()
                    .map(User::getRawDisplayName)
                    .collect(Collectors.joining(", "));


            getPhase().getGame().getAllUsers().forEach(user -> {
                user.sendMessage(TextComponent.of("The " + this.winner.getName() + "s have won the game!").color(textColor));
                user.sendMessage(TextComponent.of("The traitors were: ").color(textColor).append(TextComponent.of(traitorListString).color(TextColor.DARK_RED)));
            });
        }
    }
}
