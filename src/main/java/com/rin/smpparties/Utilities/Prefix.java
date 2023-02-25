package com.rin.smpparties.Utilities;

public class Prefix {
    public static final Prefix SERVER = new Prefix("§x§f§2§f§b§4§f[§x§e§7§f§b§5§eS§x§d§c§f§b§6§eM§x§d§1§f§c§7§dP§x§c§6§f§c§8§dP§x§b§b§f§c§9§ca§x§b§1§f§c§a§cr§x§a§6§f§c§b§bt§x§9§b§f§c§c§bi§x§9§0§f§d§d§ae§x§8§5§f§d§e§as§x§7§a§f§d§f§9] §x§f§f§f§f§f§f");
    public static final Prefix WARNING = new Prefix("§x§f§f§0§0§0§0[§x§f§f§0§e§0§0W§x§f§f§1§c§0§0A§x§f§f§2§a§0§0R§x§f§f§3§8§0§0N§x§f§f§4§7§0§0I§x§f§f§5§5§0§0N§x§f§f§6§3§0§0G§x§f§f§7§1§0§0S§x§f§f§7§f§0§0] §x§f§f§5§5§5§5");

    private final String prefix;

    private Prefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}

