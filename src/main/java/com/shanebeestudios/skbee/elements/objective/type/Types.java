package com.shanebeestudios.skbee.elements.objective.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Types {

    private static final List<Criteria> CRITERIAS = new ArrayList<>();

    static {
        Class<?> craftCriteriaClass = ReflectionUtils.getOBCClass("scoreboard.CraftCriteria");
        assert craftCriteriaClass != null;
        Object defaults = ReflectionUtils.getField("DEFAULTS", craftCriteriaClass, null);
        @SuppressWarnings("unchecked") Map<String, Criteria> map = (Map<String, Criteria>) defaults;
        assert map != null;
        CRITERIAS.addAll(map.values());

        if (Classes.getExactClassInfo(Objective.class) == null) {
            Classes.registerClass(new ClassInfo<>(Objective.class, "objective")
                .user("objectives?")
                .name("Scoreboard - Objective")
                .description("Represents an objective in a scoreboard.",
                    "When deleting, the objective will be unregistered.")
                .since("2.6.0")
                .supplier(() -> Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().iterator())
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Objective objective, int flags) {
                        return "objective '" + objective.getName() + "' with criteria '" + objective.getTrackedCriteria().getName() + "'";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Objective objective) {
                        return "objective{name=" + objective.getName() + "}";
                    }
                })
                .changer(new Changer<>() {
                    @Override
                    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) {
                            return CollectionUtils.array(Objective.class);
                        }
                        return null;
                    }

                    @Override
                    public void change(Objective[] what, @Nullable Object[] delta, ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) {
                            for (Objective objective : what) {
                                objective.unregister();
                            }
                        }
                    }
                }));
        }

        if (Skript.classExists("org.bukkit.scoreboard.Criteria") && Classes.getExactClassInfo(Criteria.class) == null) {
            Classes.registerClass(new ClassInfo<>(Criteria.class, "criteria")
                .user("criterias?")
                .name("Scoreboard - Criteria")
                .description("Represents a criteria for a scoreboard objective.",
                    "See [**Scoreboard Criteria**](https://minecraft.wiki/w/Scoreboard#Criteria) on McWiki for more details.")
                .since("2.6.0")
                .supplier(() -> CRITERIAS.stream().sorted(Comparator.comparing(Criteria::getName)).iterator())
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Criteria criteria, int flags) {
                        return "criteria '" + criteria.getName() + "'";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Criteria o) {
                        return "criteria{name=" + o.getName() + "}";
                    }
                }));
        }

        if (Classes.getExactClassInfo(RenderType.class) == null) {
            EnumWrapper<RenderType> RENDER_ENUM = new EnumWrapper<>(RenderType.class);
            Classes.registerClass(RENDER_ENUM.getClassInfo("rendertype")
                .user("render ?types?")
                .name("Scoreboard - Objective Render Type")
                .description("Controls the way in which an Objective is rendered client side.")
                .since("2.6.0"));
        }

        if (Classes.getExactClassInfo(DisplaySlot.class) == null) {
            EnumWrapper<DisplaySlot> DISPLAY_ENUM = new EnumWrapper<>(DisplaySlot.class);
            Classes.registerClass(DISPLAY_ENUM.getClassInfo("displayslot")
                .user("display ?slots?")
                .name("Scoreboard - Objective Display Slot")
                .description("Locations for displaying objectives to the player")
                .since("2.6.0"));
        }
    }

}
