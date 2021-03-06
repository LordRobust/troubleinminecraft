package io.indices.troubleinminecraft.game;

import com.voxelgameslib.voxelgameslib.api.game.GameData;
import com.voxelgameslib.voxelgameslib.components.team.Team;
import com.voxelgameslib.voxelgameslib.components.user.User;
import io.indices.troubleinminecraft.shop.ShopRegistry;
import io.indices.troubleinminecraft.team.Role;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TIMData implements GameData {

    private Team innocents;
    private Team traitors;
    private Team detectives;

    private List<User> aliveInnocents = new ArrayList<>();
    private List<User> aliveTraitors = new ArrayList<>();
    private Map<User, TIMPlayer> playerMap = new HashMap<>();
    private Map<Entity, DeadPlayer> zombiePlayerMap = new HashMap<>();

    private Role winner;
    private boolean gameStarted = false;

    private ShopRegistry shopRegistry;
}
