package com.fxfps.cmi.client.fonts;

import java.util.*;
import net.minecraft.resources.Identifier;

/**
 * Registro unificado oficial de todos los 1723 emojis, rangos, banderas, íconos y bloques
 * del pack de texturas de Argentina Network para CheManyItems (CMI) 🇦🇷.
 * Sincronizado automáticamente desde el pack de texturas.
 */
public class ServerEmojiRegistry {

    public record ServerEmoji(String unicode, String id, String displayName, String category, String shortcode, String texturePath, Identifier texture) {}

    public static final List<ServerEmoji> ALL_EMOJIS = new ArrayList<>();
    public static final Map<String, ServerEmoji> BY_SHORTCODE = new HashMap<>();
    public static final Map<String, ServerEmoji> BY_ID = new HashMap<>();
    public static final List<String> CATEGORIES = new ArrayList<>();
    public static final List<ServerEmoji> QUICK_FAVORITES = new ArrayList<>();

    private static void register(String unicode, String id, String displayName, String category, String shortcode, String texturePath) {
        Identifier texture = (texturePath != null && !texturePath.isEmpty())
                ? Identifier.fromNamespaceAndPath("minecraft", texturePath)
                : null;
        ServerEmoji emoji = new ServerEmoji(unicode, id, displayName, category, shortcode, texturePath, texture);
        ALL_EMOJIS.add(emoji);
        BY_SHORTCODE.put(shortcode.toLowerCase(), emoji);
        BY_ID.put(id.toLowerCase(), emoji);
        if (!CATEGORIES.contains(category)) {
            CATEGORIES.add(category);
        }
    }

    private static void alias(String shortcode, String targetId) {
        ServerEmoji target = BY_ID.get(targetId.toLowerCase());
        if (target != null) {
            BY_SHORTCODE.put(shortcode.toLowerCase(), target);
        }
    }

    static {
        initRangosAndLogos();
        initPatria();
        initBanderas();
        initServerUiAndUtils();
        initCombatAndItems();
        initMaterialsAndShops();
        initFoodsAndPotions();
        initBasicUiAndEffects();
        initSocial();
        initCapella();
        initGeneral();
        initBlocks();
        initLetters();
        initAliases();
        initFavorites();
    }

    private static void initRangosAndLogos() {
        register("\uE200", "badge_owner", "Rango Owner (Staff)", "Rangos", ":badge_owner:", "textures/font/badges/staff/badge_owner.png");
        register("\uE201", "badge_usuario", "Rango Usuario (Comunidad)", "Rangos", ":badge_usuario:", "textures/font/badges/comunidad/badge_usuario.png");
        register("\uE202", "badge_manager", "Rango Manager (Staff)", "Rangos", ":badge_manager:", "textures/font/badges/staff/badge_manager.png");
        register("\uE204", "badge_senioradmin", "Rango Senioradmin (Staff)", "Rangos", ":badge_senioradmin:", "textures/font/badges/staff/badge_senioradmin.png");
        register("\uE205", "badge_admin", "Rango Admin (Staff)", "Rangos", ":badge_admin:", "textures/font/badges/staff/badge_admin.png");
        register("\uE206", "badge_modplus", "Rango Modplus (Staff)", "Rangos", ":badge_modplus:", "textures/font/badges/staff/badge_modplus.png");
        register("\uE207", "badge_mod", "Rango Mod (Staff)", "Rangos", ":badge_mod:", "textures/font/badges/staff/badge_mod.png");
        register("\uE208", "badge_ayudante", "Rango Ayudante (Staff)", "Rangos", ":badge_ayudante:", "textures/font/badges/staff/badge_ayudante.png");
        register("\uE209", "badge_famoso", "Rango Famoso (Comunidad)", "Rangos", ":badge_famoso:", "textures/font/badges/comunidad/badge_famoso.png");
        register("\uE210", "badge_sanmartin", "Rango Sanmartin (Proceres)", "Rangos", ":badge_sanmartin:", "textures/font/badges/proceres/badge_sanmartin.png");
        register("\uE211", "badge_belgrano", "Rango Belgrano (Proceres)", "Rangos", ":badge_belgrano:", "textures/font/badges/proceres/badge_belgrano.png");
        register("\uE212", "badge_sarmiento", "Rango Sarmiento (Proceres)", "Rangos", ":badge_sarmiento:", "textures/font/badges/proceres/badge_sarmiento.png");
        register("\uE213", "badge_betatester", "Rango Betatester (Comunidad)", "Rangos", ":badge_betatester:", "textures/font/badges/comunidad/badge_betatester.png");
        register("\uE214", "badge_developer", "Rango Developer (Staff)", "Rangos", ":badge_developer:", "textures/font/badges/staff/badge_developer.png");
        register("\uE215", "badge_guemes", "Rango Guemes (Proceres)", "Rangos", ":badge_guemes:", "textures/font/badges/proceres/badge_guemes.png");
        register("\uE216", "badge_media", "Rango Media (Comunidad)", "Rangos", ":badge_media:", "textures/font/badges/comunidad/badge_media.png");
        register("\uE220", "badge_coowner", "Rango Coowner (Staff)", "Rangos", ":badge_coowner:", "textures/font/badges/staff/badge_coowner.png");
        register("\uE221", "badge_matero", "Rango Matero (Mate)", "Rangos", ":badge_matero:", "textures/font/badges/mate/badge_matero.png");
        register("\uE302", "badge_mate", "Rango Mate (Mate)", "Rangos", ":badge_mate:", "textures/font/badges/mate/badge_mate.png");
        register("\uE300", "logo_full", "Logo Full", "Logos", ":logo_full:", "textures/font/logo/logo_full.png");
        register("\uE301", "cmi_logo", "Logo Cmi_logo", "Logos", ":cmi_logo:", "textures/font/logo/cmi_logo.png");
        register("\uE310", "logo_scoreboard", "Logo Scoreboard", "Logos", ":logo_scoreboard:", "textures/font/logo/logo_scoreboard.png");
        register("\uF00A", "logo_survival", "Logo Survival", "Logos", ":logo_survival:", "textures/font/logo/logo_survival.png");
        register("\uF00D", "logo_skywars", "Logo Skywars", "Logos", ":logo_skywars:", "textures/font/logo/logo_skywars.png");
    }

    private static void initPatria() {
        register("\uE8B0", "badge_sol_de_mayo", "Patria Sol De Mayo", "Patria Argentina", ":badge_sol_de_mayo:", "textures/font/badges/nacionales/badge_sol_de_mayo.png");
        register("\uE8B1", "badge_escarapela", "Patria Escarapela", "Patria Argentina", ":badge_escarapela:", "textures/font/badges/nacionales/badge_escarapela.png");
        register("\uE8B2", "badge_islas_malvinas", "Patria Islas Malvinas", "Patria Argentina", ":badge_islas_malvinas:", "textures/font/badges/nacionales/badge_islas_malvinas.png");
        register("\uE8B3", "badge_escudo_nacional", "Patria Escudo Nacional", "Patria Argentina", ":badge_escudo_nacional:", "textures/font/badges/nacionales/badge_escudo_nacional.png");
        register("\uE8B4", "badge_gorro_frigio", "Patria Gorro Frigio", "Patria Argentina", ":badge_gorro_frigio:", "textures/font/badges/nacionales/badge_gorro_frigio.png");
        register("\uE8B5", "badge_cabildo", "Patria Cabildo", "Patria Argentina", ":badge_cabildo:", "textures/font/badges/nacionales/badge_cabildo.png");
        register("\uE8B6", "badge_obelisco", "Patria Obelisco", "Patria Argentina", ":badge_obelisco:", "textures/font/badges/nacionales/badge_obelisco.png");
        register("\uE8B7", "badge_monumento_bandera", "Patria Monumento Bandera", "Patria Argentina", ":badge_monumento_bandera:", "textures/font/badges/nacionales/badge_monumento_bandera.png");
        register("\uE8B8", "badge_tres_estrellas", "Patria Tres Estrellas", "Patria Argentina", ":badge_tres_estrellas:", "textures/font/badges/nacionales/badge_tres_estrellas.png");
        register("\uE8B9", "badge_copa_del_mundo", "Patria Copa Del Mundo", "Patria Argentina", ":badge_copa_del_mundo:", "textures/font/badges/nacionales/badge_copa_del_mundo.png");
        register("\uE8BA", "badge_camiseta_argentina", "Patria Camiseta Argentina", "Patria Argentina", ":badge_camiseta_argentina:", "textures/font/badges/nacionales/badge_camiseta_argentina.png");
        register("\uE8BB", "badge_asado_criollo", "Patria Asado Criollo", "Patria Argentina", ":badge_asado_criollo:", "textures/font/badges/nacionales/badge_asado_criollo.png");
        register("\uE8BC", "badge_empanada", "Patria Empanada", "Patria Argentina", ":badge_empanada:", "textures/font/badges/nacionales/badge_empanada.png");
        register("\uE8BD", "badge_dulce_de_leche", "Patria Dulce De Leche", "Patria Argentina", ":badge_dulce_de_leche:", "textures/font/badges/nacionales/badge_dulce_de_leche.png");
        register("\uE8BE", "badge_alfajor_marplatense", "Patria Alfajor Marplatense", "Patria Argentina", ":badge_alfajor_marplatense:", "textures/font/badges/nacionales/badge_alfajor_marplatense.png");
        register("\uE8BF", "badge_alfajor_maicena", "Patria Alfajor Maicena", "Patria Argentina", ":badge_alfajor_maicena:", "textures/font/badges/nacionales/badge_alfajor_maicena.png");
        register("\uE8C0", "badge_termo_mate", "Patria Termo Mate", "Patria Argentina", ":badge_termo_mate:", "textures/font/badges/nacionales/badge_termo_mate.png");
        register("\uE8C1", "badge_gaucho", "Patria Gaucho", "Patria Argentina", ":badge_gaucho:", "textures/font/badges/nacionales/badge_gaucho.png");
        register("\uE8C2", "badge_bandoneon", "Patria Bandoneon", "Patria Argentina", ":badge_bandoneon:", "textures/font/badges/nacionales/badge_bandoneon.png");
        register("\uE8C3", "badge_hornero", "Patria Hornero", "Patria Argentina", ":badge_hornero:", "textures/font/badges/nacionales/badge_hornero.png");
        register("\uE8C4", "badge_flor_ceibo", "Patria Flor Ceibo", "Patria Argentina", ":badge_flor_ceibo:", "textures/font/badges/nacionales/badge_flor_ceibo.png");
        register("\uE8C5", "badge_mapa_argentina", "Patria Mapa Argentina", "Patria Argentina", ":badge_mapa_argentina:", "textures/font/badges/nacionales/badge_mapa_argentina.png");
    }

    private static void initBanderas() {
        register("\uE100", "flag_ar", "Bandera AR", "Banderas", ":flag_ar:", "textures/font/flags/flag_ar.png");
        register("\uE101", "flag_br", "Bandera BR", "Banderas", ":flag_br:", "textures/font/flags/flag_br.png");
        register("\uE102", "flag_cl", "Bandera CL", "Banderas", ":flag_cl:", "textures/font/flags/flag_cl.png");
        register("\uE103", "flag_co", "Bandera CO", "Banderas", ":flag_co:", "textures/font/flags/flag_co.png");
        register("\uE104", "flag_mx", "Bandera MX", "Banderas", ":flag_mx:", "textures/font/flags/flag_mx.png");
        register("\uE105", "flag_pe", "Bandera PE", "Banderas", ":flag_pe:", "textures/font/flags/flag_pe.png");
        register("\uE106", "flag_uy", "Bandera UY", "Banderas", ":flag_uy:", "textures/font/flags/flag_uy.png");
        register("\uE107", "flag_py", "Bandera PY", "Banderas", ":flag_py:", "textures/font/flags/flag_py.png");
        register("\uE108", "flag_bo", "Bandera BO", "Banderas", ":flag_bo:", "textures/font/flags/flag_bo.png");
        register("\uE109", "flag_ve", "Bandera VE", "Banderas", ":flag_ve:", "textures/font/flags/flag_ve.png");
        register("\uE10A", "flag_ec", "Bandera EC", "Banderas", ":flag_ec:", "textures/font/flags/flag_ec.png");
        register("\uE10B", "flag_us", "Bandera US", "Banderas", ":flag_us:", "textures/font/flags/flag_us.png");
        register("\uE10C", "flag_es", "Bandera ES", "Banderas", ":flag_es:", "textures/font/flags/flag_es.png");
        register("\uE10D", "flag_de", "Bandera DE", "Banderas", ":flag_de:", "textures/font/flags/flag_de.png");
        register("\uE10E", "flag_fr", "Bandera FR", "Banderas", ":flag_fr:", "textures/font/flags/flag_fr.png");
        register("\uE10F", "flag_it", "Bandera IT", "Banderas", ":flag_it:", "textures/font/flags/flag_it.png");
        register("\uE110", "flag_gb", "Bandera GB", "Banderas", ":flag_gb:", "textures/font/flags/flag_gb.png");
        register("\uE111", "flag_pt", "Bandera PT", "Banderas", ":flag_pt:", "textures/font/flags/flag_pt.png");
        register("\uE112", "flag_nl", "Bandera NL", "Banderas", ":flag_nl:", "textures/font/flags/flag_nl.png");
        register("\uE113", "flag_ru", "Bandera RU", "Banderas", ":flag_ru:", "textures/font/flags/flag_ru.png");
        register("\uE114", "flag_cn", "Bandera CN", "Banderas", ":flag_cn:", "textures/font/flags/flag_cn.png");
        register("\uE115", "flag_jp", "Bandera JP", "Banderas", ":flag_jp:", "textures/font/flags/flag_jp.png");
        register("\uE116", "flag_kr", "Bandera KR", "Banderas", ":flag_kr:", "textures/font/flags/flag_kr.png");
        register("\uE117", "flag_au", "Bandera AU", "Banderas", ":flag_au:", "textures/font/flags/flag_au.png");
        register("\uE118", "flag_ca", "Bandera CA", "Banderas", ":flag_ca:", "textures/font/flags/flag_ca.png");
        register("\uE119", "flag_tr", "Bandera TR", "Banderas", ":flag_tr:", "textures/font/flags/flag_tr.png");
        register("\uE11A", "flag_pl", "Bandera PL", "Banderas", ":flag_pl:", "textures/font/flags/flag_pl.png");
        register("\uE11B", "flag_se", "Bandera SE", "Banderas", ":flag_se:", "textures/font/flags/flag_se.png");
        register("\uE11C", "flag_no", "Bandera NO", "Banderas", ":flag_no:", "textures/font/flags/flag_no.png");
        register("\uE11D", "flag_dk", "Bandera DK", "Banderas", ":flag_dk:", "textures/font/flags/flag_dk.png");
        register("\uE11E", "flag_fi", "Bandera FI", "Banderas", ":flag_fi:", "textures/font/flags/flag_fi.png");
        register("\uE11F", "flag_cz", "Bandera CZ", "Banderas", ":flag_cz:", "textures/font/flags/flag_cz.png");
        register("\uE120", "flag_hu", "Bandera HU", "Banderas", ":flag_hu:", "textures/font/flags/flag_hu.png");
        register("\uE121", "flag_ro", "Bandera RO", "Banderas", ":flag_ro:", "textures/font/flags/flag_ro.png");
        register("\uE122", "flag_gt", "Bandera GT", "Banderas", ":flag_gt:", "textures/font/flags/flag_gt.png");
        register("\uE123", "flag_hn", "Bandera HN", "Banderas", ":flag_hn:", "textures/font/flags/flag_hn.png");
        register("\uE124", "flag_sv", "Bandera SV", "Banderas", ":flag_sv:", "textures/font/flags/flag_sv.png");
        register("\uE125", "flag_ni", "Bandera NI", "Banderas", ":flag_ni:", "textures/font/flags/flag_ni.png");
        register("\uE126", "flag_cr", "Bandera CR", "Banderas", ":flag_cr:", "textures/font/flags/flag_cr.png");
        register("\uE127", "flag_pa", "Bandera PA", "Banderas", ":flag_pa:", "textures/font/flags/flag_pa.png");
        register("\uE128", "flag_do", "Bandera DO", "Banderas", ":flag_do:", "textures/font/flags/flag_do.png");
        register("\uE129", "flag_cu", "Bandera CU", "Banderas", ":flag_cu:", "textures/font/flags/flag_cu.png");
        register("\uE12A", "flag_pr", "Bandera PR", "Banderas", ":flag_pr:", "textures/font/flags/flag_pr.png");
        register("\uE12B", "flag_za", "Bandera ZA", "Banderas", ":flag_za:", "textures/font/flags/flag_za.png");
        register("\uE12C", "flag_ng", "Bandera NG", "Banderas", ":flag_ng:", "textures/font/flags/flag_ng.png");
        register("\uE12D", "flag_eg", "Bandera EG", "Banderas", ":flag_eg:", "textures/font/flags/flag_eg.png");
        register("\uE12E", "flag_ma", "Bandera MA", "Banderas", ":flag_ma:", "textures/font/flags/flag_ma.png");
        register("\uE12F", "flag_in", "Bandera IN", "Banderas", ":flag_in:", "textures/font/flags/flag_in.png");
        register("\uE130", "flag_ph", "Bandera PH", "Banderas", ":flag_ph:", "textures/font/flags/flag_ph.png");
        register("\uE131", "flag_id", "Bandera ID", "Banderas", ":flag_id:", "textures/font/flags/flag_id.png");
        register("\uE132", "flag_th", "Bandera TH", "Banderas", ":flag_th:", "textures/font/flags/flag_th.png");
        register("\uE133", "flag_vn", "Bandera VN", "Banderas", ":flag_vn:", "textures/font/flags/flag_vn.png");
        register("\uE134", "flag_ua", "Bandera UA", "Banderas", ":flag_ua:", "textures/font/flags/flag_ua.png");
        register("\uE135", "flag_gr", "Bandera GR", "Banderas", ":flag_gr:", "textures/font/flags/flag_gr.png");
        register("\uE136", "flag_sk", "Bandera SK", "Banderas", ":flag_sk:", "textures/font/flags/flag_sk.png");
        register("\uE137", "flag_bg", "Bandera BG", "Banderas", ":flag_bg:", "textures/font/flags/flag_bg.png");
        register("\uE138", "flag_hr", "Bandera HR", "Banderas", ":flag_hr:", "textures/font/flags/flag_hr.png");
        register("\uE139", "flag_rs", "Bandera RS", "Banderas", ":flag_rs:", "textures/font/flags/flag_rs.png");
        register("\uE13A", "flag_il", "Bandera IL", "Banderas", ":flag_il:", "textures/font/flags/flag_il.png");
        register("\uE13B", "flag_sa", "Bandera SA", "Banderas", ":flag_sa:", "textures/font/flags/flag_sa.png");
        register("\uE13C", "flag_unknown", "Bandera UNKNOWN", "Banderas", ":flag_unknown:", "textures/font/flags/flag_unknown.png");
    }

    private static void initServerUiAndUtils() {
        register("\uE770", "sui_alert_bell", "S-UI Alert Bell", "Server UI", ":alert_bell:", "textures/font/icons/server_ui_v2/alert_bell.png");
        register("\uE771", "sui_back_arrow", "S-UI Back Arrow", "Server UI", ":back_arrow:", "textures/font/icons/server_ui_v2/back_arrow.png");
        register("\uE772", "sui_bank_vault", "S-UI Bank Vault", "Server UI", ":bank_vault:", "textures/font/icons/server_ui_v2/bank_vault.png");
        register("\uE773", "sui_checkmark_confirm", "S-UI Checkmark Confirm", "Server UI", ":checkmark_confirm:", "textures/font/icons/server_ui_v2/checkmark_confirm.png");
        register("\uE774", "sui_close_cancel", "S-UI Close Cancel", "Server UI", ":close_cancel:", "textures/font/icons/server_ui_v2/close_cancel.png");
        register("\uE775", "sui_coin_single", "S-UI Coin Single", "Server UI", ":coin_single:", "textures/font/icons/server_ui_v2/coin_single.png");
        register("\uE776", "sui_coins_stack", "S-UI Coins Stack", "Server UI", ":coins_stack:", "textures/font/icons/server_ui_v2/coins_stack.png");
        register("\uE777", "sui_crown_vip", "S-UI Crown Vip", "Server UI", ":crown_vip:", "textures/font/icons/server_ui_v2/crown_vip.png");
        register("\uE778", "sui_favorite_star", "S-UI Favorite Star", "Server UI", ":favorite_star:", "textures/font/icons/server_ui_v2/favorite_star.png");
        register("\uE779", "sui_gem_diamond", "S-UI Gem Diamond", "Server UI", ":gem_diamond:", "textures/font/icons/server_ui_v2/gem_diamond.png");
        register("\uE77A", "sui_gem_ruby", "S-UI Gem Ruby", "Server UI", ":gem_ruby:", "textures/font/icons/server_ui_v2/gem_ruby.png");
        register("\uE77B", "sui_gift_box", "S-UI Gift Box", "Server UI", ":gift_box:", "textures/font/icons/server_ui_v2/gift_box.png");
        register("\uE77C", "sui_golden_key", "S-UI Golden Key", "Server UI", ":golden_key:", "textures/font/icons/server_ui_v2/golden_key.png");
        register("\uE77D", "sui_home", "S-UI Home", "Server UI", ":home:", "textures/font/icons/server_ui_v2/home.png");
        register("\uE77E", "sui_info_help", "S-UI Info Help", "Server UI", ":info_help:", "textures/font/icons/server_ui_v2/info_help.png");
        register("\uE77F", "sui_lock_locked", "S-UI Lock Locked", "Server UI", ":lock_locked:", "textures/font/icons/server_ui_v2/lock_locked.png");
        register("\uE780", "sui_lock_unlocked", "S-UI Lock Unlocked", "Server UI", ":lock_unlocked:", "textures/font/icons/server_ui_v2/lock_unlocked.png");
        register("\uE781", "sui_mail_letter", "S-UI Mail Letter", "Server UI", ":mail_letter:", "textures/font/icons/server_ui_v2/mail_letter.png");
        register("\uE782", "sui_medal_ribbon", "S-UI Medal Ribbon", "Server UI", ":medal_ribbon:", "textures/font/icons/server_ui_v2/medal_ribbon.png");
        register("\uE783", "sui_money_bag", "S-UI Money Bag", "Server UI", ":money_bag:", "textures/font/icons/server_ui_v2/money_bag.png");
        register("\uE784", "sui_next_arrow", "S-UI Next Arrow", "Server UI", ":next_arrow:", "textures/font/icons/server_ui_v2/next_arrow.png");
        register("\uE785", "sui_player_profile", "S-UI Player Profile", "Server UI", ":player_profile:", "textures/font/icons/server_ui_v2/player_profile.png");
        register("\uE786", "sui_price_tag", "S-UI Price Tag", "Server UI", ":price_tag:", "textures/font/icons/server_ui_v2/price_tag.png");
        register("\uE787", "sui_quest_scroll", "S-UI Quest Scroll", "Server UI", ":quest_scroll:", "textures/font/icons/server_ui_v2/quest_scroll.png");
        register("\uE788", "sui_refresh_reload", "S-UI Refresh Reload", "Server UI", ":refresh_reload:", "textures/font/icons/server_ui_v2/refresh_reload.png");
        register("\uE789", "sui_reward_chest", "S-UI Reward Chest", "Server UI", ":reward_chest:", "textures/font/icons/server_ui_v2/reward_chest.png");
        register("\uE78A", "sui_search_magnifier", "S-UI Search Magnifier", "Server UI", ":search_magnifier:", "textures/font/icons/server_ui_v2/search_magnifier.png");
        register("\uE78B", "sui_settings_gear", "S-UI Settings Gear", "Server UI", ":settings_gear:", "textures/font/icons/server_ui_v2/settings_gear.png");
        register("\uE78C", "sui_shopping_cart", "S-UI Shopping Cart", "Server UI", ":shopping_cart:", "textures/font/icons/server_ui_v2/shopping_cart.png");
        register("\uE78D", "sui_trophy_cup", "S-UI Trophy Cup", "Server UI", ":trophy_cup:", "textures/font/icons/server_ui_v2/trophy_cup.png");
        register("\uE790", "uicon_anvil_forge", "Icono Anvil Forge", "Útiles", ":anvil_forge:", "textures/font/icons/useful_icons_v4/anvil_forge.png");
        register("\uE791", "uicon_auction_gavel", "Icono Auction Gavel", "Útiles", ":auction_gavel:", "textures/font/icons/useful_icons_v4/auction_gavel.png");
        register("\uE792", "uicon_backpack_storage", "Icono Backpack Storage", "Útiles", ":backpack_storage:", "textures/font/icons/useful_icons_v4/backpack_storage.png");
        register("\uE793", "uicon_battlepass_ticket", "Icono Battlepass Ticket", "Útiles", ":battlepass_ticket:", "textures/font/icons/useful_icons_v4/battlepass_ticket.png");
        register("\uE794", "uicon_booster_rocket", "Icono Booster Rocket", "Útiles", ":booster_rocket:", "textures/font/icons/useful_icons_v4/booster_rocket.png");
        register("\uE795", "uicon_bow_target", "Icono Bow Target", "Útiles", ":bow_target:", "textures/font/icons/useful_icons_v4/bow_target.png");
        register("\uE796", "uicon_buff_potion", "Icono Buff Potion", "Útiles", ":buff_potion:", "textures/font/icons/useful_icons_v4/buff_potion.png");
        register("\uE797", "uicon_casino_dice", "Icono Casino Dice", "Útiles", ":casino_dice:", "textures/font/icons/useful_icons_v4/casino_dice.png");
        register("\uE798", "uicon_claim_flag", "Icono Claim Flag", "Útiles", ":claim_flag:", "textures/font/icons/useful_icons_v4/claim_flag.png");
        register("\uE799", "uicon_compass_nav", "Icono Compass Nav", "Útiles", ":compass_nav:", "textures/font/icons/useful_icons_v4/compass_nav.png");
        register("\uE79A", "uicon_cooldown_clock", "Icono Cooldown Clock", "Útiles", ":cooldown_clock:", "textures/font/icons/useful_icons_v4/cooldown_clock.png");
        register("\uE79B", "uicon_cosmetics_wardrobe", "Icono Cosmetics Wardrobe", "Útiles", ":cosmetics_wardrobe:", "textures/font/icons/useful_icons_v4/cosmetics_wardrobe.png");
        register("\uE79C", "uicon_daily_calendar", "Icono Daily Calendar", "Útiles", ":daily_calendar:", "textures/font/icons/useful_icons_v4/daily_calendar.png");
        register("\uE79D", "uicon_discord_badge", "Icono Discord Badge", "Útiles", ":discord_badge:", "textures/font/icons/useful_icons_v4/discord_badge.png");
        register("\uE79E", "uicon_enchant_book", "Icono Enchant Book", "Útiles", ":enchant_book:", "textures/font/icons/useful_icons_v4/enchant_book.png");
        register("\uE79F", "uicon_kill_skull", "Icono Kill Skull", "Útiles", ":kill_skull:", "textures/font/icons/useful_icons_v4/kill_skull.png");
        register("\uE7A0", "uicon_leaderboard_podium", "Icono Leaderboard Podium", "Útiles", ":leaderboard_podium:", "textures/font/icons/useful_icons_v4/leaderboard_podium.png");
        register("\uE7A1", "uicon_lucky_wheel", "Icono Lucky Wheel", "Útiles", ":lucky_wheel:", "textures/font/icons/useful_icons_v4/lucky_wheel.png");
        register("\uE7A2", "uicon_ping_network", "Icono Ping Network", "Útiles", ":ping_network:", "textures/font/icons/useful_icons_v4/ping_network.png");
        register("\uE7A3", "uicon_player_stats", "Icono Player Stats", "Útiles", ":player_stats:", "textures/font/icons/useful_icons_v4/player_stats.png");
        register("\uE7A4", "uicon_pvp_swords", "Icono Pvp Swords", "Útiles", ":pvp_swords:", "textures/font/icons/useful_icons_v4/pvp_swords.png");
        register("\uE7A5", "uicon_ranks_badge", "Icono Ranks Badge", "Útiles", ":ranks_badge:", "textures/font/icons/useful_icons_v4/ranks_badge.png");
        register("\uE7A6", "uicon_repair_wrench", "Icono Repair Wrench", "Útiles", ":repair_wrench:", "textures/font/icons/useful_icons_v4/repair_wrench.png");
        register("\uE7A7", "uicon_shield_defense", "Icono Shield Defense", "Útiles", ":shield_defense:", "textures/font/icons/useful_icons_v4/shield_defense.png");
        register("\uE7A8", "uicon_skill_tree", "Icono Skill Tree", "Útiles", ":skill_tree:", "textures/font/icons/useful_icons_v4/skill_tree.png");
        register("\uE7A9", "uicon_spawn_beacon", "Icono Spawn Beacon", "Útiles", ":spawn_beacon:", "textures/font/icons/useful_icons_v4/spawn_beacon.png");
        register("\uE7AA", "uicon_trash_bin", "Icono Trash Bin", "Útiles", ":trash_bin:", "textures/font/icons/useful_icons_v4/trash_bin.png");
        register("\uE7AB", "uicon_vote_ballot", "Icono Vote Ballot", "Útiles", ":vote_ballot:", "textures/font/icons/useful_icons_v4/vote_ballot.png");
        register("\uE7AC", "uicon_warp_portal", "Icono Warp Portal", "Útiles", ":warp_portal:", "textures/font/icons/useful_icons_v4/warp_portal.png");
        register("\uE7AD", "uicon_world_globe", "Icono World Globe", "Útiles", ":world_globe:", "textures/font/icons/useful_icons_v4/world_globe.png");
    }

    private static void initCombatAndItems() {
        register("\uE740", "combat_shield_combat", "Combate Shield Combat", "Combate", ":shield_combat:", "textures/font/icons/combat/shield_combat.png");
        register("\uE741", "combat_shield_argentina", "Combate Shield Argentina", "Combate", ":shield_argentina:", "textures/font/icons/combat/shield_argentina.png");
        register("\uE742", "combat_shield_netherite", "Combate Shield Netherite", "Combate", ":shield_netherite:", "textures/font/icons/combat/shield_netherite.png");
        register("\uE743", "combat_shield_diamond", "Combate Shield Diamond", "Combate", ":shield_diamond:", "textures/font/icons/combat/shield_diamond.png");
        register("\uE744", "combat_totem_of_undying", "Combate Totem Of Undying", "Combate", ":totem_of_undying:", "textures/font/icons/combat/totem_of_undying.png");
        register("\uE745", "combat_totem_argentina", "Combate Totem Argentina", "Combate", ":totem_argentina:", "textures/font/icons/combat/totem_argentina.png");
        register("\uE746", "combat_netherite_sword", "Combate Netherite Sword", "Combate", ":netherite_sword:", "textures/font/icons/combat/netherite_sword.png");
        register("\uE747", "combat_diamond_sword", "Combate Diamond Sword", "Combate", ":diamond_sword:", "textures/font/icons/combat/diamond_sword.png");
        register("\uE748", "combat_iron_sword", "Combate Iron Sword", "Combate", ":iron_sword:", "textures/font/icons/combat/iron_sword.png");
        register("\uE749", "combat_golden_sword", "Combate Golden Sword", "Combate", ":golden_sword:", "textures/font/icons/combat/golden_sword.png");
        register("\uE74A", "combat_stone_sword", "Combate Stone Sword", "Combate", ":stone_sword:", "textures/font/icons/combat/stone_sword.png");
        register("\uE74B", "combat_wooden_sword", "Combate Wooden Sword", "Combate", ":wooden_sword:", "textures/font/icons/combat/wooden_sword.png");
        register("\uE74C", "combat_mace", "Combate Mace", "Combate", ":mace:", "textures/font/icons/combat/mace.png");
        register("\uE74D", "combat_trident", "Combate Trident", "Combate", ":trident:", "textures/font/icons/combat/trident.png");
        register("\uE74E", "combat_bow", "Combate Bow", "Combate", ":bow:", "textures/font/icons/combat/bow.png");
        register("\uE74F", "combat_crossbow", "Combate Crossbow", "Combate", ":crossbow:", "textures/font/icons/combat/crossbow.png");
        register("\uE750", "combat_netherite_axe", "Combate Netherite Axe", "Combate", ":netherite_axe:", "textures/font/icons/combat/netherite_axe.png");
        register("\uE751", "combat_diamond_axe", "Combate Diamond Axe", "Combate", ":diamond_axe:", "textures/font/icons/combat/diamond_axe.png");
        register("\uE752", "combat_iron_axe", "Combate Iron Axe", "Combate", ":iron_axe:", "textures/font/icons/combat/iron_axe.png");
        register("\uE753", "combat_golden_axe", "Combate Golden Axe", "Combate", ":golden_axe:", "textures/font/icons/combat/golden_axe.png");
        register("\uE754", "combat_arrow", "Combate Arrow", "Combate", ":arrow:", "textures/font/icons/combat/arrow.png");
        register("\uE755", "combat_spectral_arrow", "Combate Spectral Arrow", "Combate", ":spectral_arrow:", "textures/font/icons/combat/spectral_arrow.png");
        register("\uE756", "combat_tipped_arrow", "Combate Tipped Arrow", "Combate", ":tipped_arrow:", "textures/font/icons/combat/tipped_arrow.png");
        register("\uE757", "combat_netherite_helmet", "Combate Netherite Helmet", "Combate", ":netherite_helmet:", "textures/font/icons/combat/netherite_helmet.png");
        register("\uE758", "combat_netherite_chestplate", "Combate Netherite Chestplate", "Combate", ":netherite_chestplate:", "textures/font/icons/combat/netherite_chestplate.png");
        register("\uE759", "combat_netherite_leggings", "Combate Netherite Leggings", "Combate", ":netherite_leggings:", "textures/font/icons/combat/netherite_leggings.png");
        register("\uE75A", "combat_netherite_boots", "Combate Netherite Boots", "Combate", ":netherite_boots:", "textures/font/icons/combat/netherite_boots.png");
        register("\uE75B", "combat_diamond_helmet", "Combate Diamond Helmet", "Combate", ":diamond_helmet:", "textures/font/icons/combat/diamond_helmet.png");
        register("\uE75C", "combat_diamond_chestplate", "Combate Diamond Chestplate", "Combate", ":diamond_chestplate:", "textures/font/icons/combat/diamond_chestplate.png");
        register("\uE75D", "combat_diamond_leggings", "Combate Diamond Leggings", "Combate", ":diamond_leggings:", "textures/font/icons/combat/diamond_leggings.png");
        register("\uE75E", "combat_diamond_boots", "Combate Diamond Boots", "Combate", ":diamond_boots:", "textures/font/icons/combat/diamond_boots.png");
        register("\uE75F", "combat_iron_helmet", "Combate Iron Helmet", "Combate", ":iron_helmet:", "textures/font/icons/combat/iron_helmet.png");
        register("\uE760", "combat_iron_chestplate", "Combate Iron Chestplate", "Combate", ":iron_chestplate:", "textures/font/icons/combat/iron_chestplate.png");
        register("\uE761", "combat_iron_leggings", "Combate Iron Leggings", "Combate", ":iron_leggings:", "textures/font/icons/combat/iron_leggings.png");
        register("\uE762", "combat_iron_boots", "Combate Iron Boots", "Combate", ":iron_boots:", "textures/font/icons/combat/iron_boots.png");
        register("\uE763", "combat_golden_helmet", "Combate Golden Helmet", "Combate", ":golden_helmet:", "textures/font/icons/combat/golden_helmet.png");
        register("\uE764", "combat_turtle_helmet", "Combate Turtle Helmet", "Combate", ":turtle_helmet:", "textures/font/icons/combat/turtle_helmet.png");
        register("\uE720", "item_golden_apple", "Item Golden Apple", "Items", ":item_golden_apple:", "textures/font/icons/items/golden_apple.png");
        register("\uE721", "item_enchanted_golden_apple", "Item Enchanted Golden Apple", "Items", ":item_enchanted_golden_apple:", "textures/font/icons/items/enchanted_golden_apple.png");
        register("\uE722", "item_ender_pearl", "Item Ender Pearl", "Items", ":item_ender_pearl:", "textures/font/icons/items/ender_pearl.png");
        register("\uE723", "item_eye_of_ender", "Item Eye Of Ender", "Items", ":item_eye_of_ender:", "textures/font/icons/items/eye_of_ender.png");
        register("\uE724", "item_elytra", "Item Elytra", "Items", ":item_elytra:", "textures/font/icons/items/elytra.png");
        register("\uE725", "item_nether_star", "Item Nether Star", "Items", ":item_nether_star:", "textures/font/icons/items/nether_star.png");
        register("\uE726", "item_experience_bottle", "Item Experience Bottle", "Items", ":item_experience_bottle:", "textures/font/icons/items/experience_bottle.png");
        register("\uE727", "item_enchanted_book", "Item Enchanted Book", "Items", ":item_enchanted_book:", "textures/font/icons/items/enchanted_book.png");
        register("\uE728", "item_book", "Item Book", "Items", ":item_book:", "textures/font/icons/items/book.png");
        register("\uE729", "item_bundle", "Item Bundle", "Items", ":item_bundle:", "textures/font/icons/items/bundle.png");
        register("\uE72A", "item_firework_rocket", "Item Firework Rocket", "Items", ":item_firework_rocket:", "textures/font/icons/items/firework_rocket.png");
        register("\uE72B", "item_wind_charge", "Item Wind Charge", "Items", ":item_wind_charge:", "textures/font/icons/items/wind_charge.png");
        register("\uE72C", "item_breeze_rod", "Item Breeze Rod", "Items", ":item_breeze_rod:", "textures/font/icons/items/breeze_rod.png");
        register("\uE72D", "item_blaze_rod", "Item Blaze Rod", "Items", ":item_blaze_rod:", "textures/font/icons/items/blaze_rod.png");
        register("\uE72E", "item_blaze_powder", "Item Blaze Powder", "Items", ":item_blaze_powder:", "textures/font/icons/items/blaze_powder.png");
        register("\uE72F", "item_ghast_tear", "Item Ghast Tear", "Items", ":item_ghast_tear:", "textures/font/icons/items/ghast_tear.png");
        register("\uE730", "item_magma_cream", "Item Magma Cream", "Items", ":item_magma_cream:", "textures/font/icons/items/magma_cream.png");
        register("\uE731", "item_slime_ball", "Item Slime Ball", "Items", ":item_slime_ball:", "textures/font/icons/items/slime_ball.png");
        register("\uE732", "item_shulker_shell", "Item Shulker Shell", "Items", ":item_shulker_shell:", "textures/font/icons/items/shulker_shell.png");
        register("\uE733", "item_nautilus_shell", "Item Nautilus Shell", "Items", ":item_nautilus_shell:", "textures/font/icons/items/nautilus_shell.png");
        register("\uE734", "item_heart_of_the_sea", "Item Heart Of The Sea", "Items", ":item_heart_of_the_sea:", "textures/font/icons/items/heart_of_the_sea.png");
        register("\uE735", "item_honey_bottle", "Item Honey Bottle", "Items", ":item_honey_bottle:", "textures/font/icons/items/honey_bottle.png");
        register("\uE736", "item_name_tag", "Item Name Tag", "Items", ":item_name_tag:", "textures/font/icons/items/name_tag.png");
        register("\uE737", "item_lead", "Item Lead", "Items", ":item_lead:", "textures/font/icons/items/lead.png");
        register("\uE738", "item_saddle", "Item Saddle", "Items", ":item_saddle:", "textures/font/icons/items/saddle.png");
        register("\uE739", "item_clock", "Item Clock", "Items", ":item_clock:", "textures/font/icons/items/clock.png");
        register("\uE73A", "item_compass", "Item Compass", "Items", ":item_compass:", "textures/font/icons/items/compass.png");
        register("\uE73B", "item_recovery_compass", "Item Recovery Compass", "Items", ":item_recovery_compass:", "textures/font/icons/items/recovery_compass.png");
    }

    private static void initMaterialsAndShops() {
        register("\uE700", "mat_diamond", "Material Diamond", "Materiales", ":mat_diamond:", "textures/font/icons/materials/diamond.png");
        register("\uE701", "mat_emerald", "Material Emerald", "Materiales", ":mat_emerald:", "textures/font/icons/materials/emerald.png");
        register("\uE702", "mat_netherite_ingot", "Material Netherite Ingot", "Materiales", ":mat_netherite_ingot:", "textures/font/icons/materials/netherite_ingot.png");
        register("\uE703", "mat_gold_ingot", "Material Gold Ingot", "Materiales", ":mat_gold_ingot:", "textures/font/icons/materials/gold_ingot.png");
        register("\uE704", "mat_iron_ingot", "Material Iron Ingot", "Materiales", ":mat_iron_ingot:", "textures/font/icons/materials/iron_ingot.png");
        register("\uE705", "mat_copper_ingot", "Material Copper Ingot", "Materiales", ":mat_copper_ingot:", "textures/font/icons/materials/copper_ingot.png");
        register("\uE706", "mat_netherite_scrap", "Material Netherite Scrap", "Materiales", ":mat_netherite_scrap:", "textures/font/icons/materials/netherite_scrap.png");
        register("\uE707", "mat_coal", "Material Coal", "Materiales", ":mat_coal:", "textures/font/icons/materials/coal.png");
        register("\uE708", "mat_charcoal", "Material Charcoal", "Materiales", ":mat_charcoal:", "textures/font/icons/materials/charcoal.png");
        register("\uE709", "mat_raw_iron", "Material Raw Iron", "Materiales", ":mat_raw_iron:", "textures/font/icons/materials/raw_iron.png");
        register("\uE70A", "mat_raw_gold", "Material Raw Gold", "Materiales", ":mat_raw_gold:", "textures/font/icons/materials/raw_gold.png");
        register("\uE70B", "mat_raw_copper", "Material Raw Copper", "Materiales", ":mat_raw_copper:", "textures/font/icons/materials/raw_copper.png");
        register("\uE70C", "mat_redstone", "Material Redstone", "Materiales", ":mat_redstone:", "textures/font/icons/materials/redstone.png");
        register("\uE70D", "mat_lapis_lazuli", "Material Lapis Lazuli", "Materiales", ":mat_lapis_lazuli:", "textures/font/icons/materials/lapis_lazuli.png");
        register("\uE70E", "mat_quartz", "Material Quartz", "Materiales", ":mat_quartz:", "textures/font/icons/materials/quartz.png");
        register("\uE70F", "mat_amethyst_shard", "Material Amethyst Shard", "Materiales", ":mat_amethyst_shard:", "textures/font/icons/materials/amethyst_shard.png");
        register("\uE710", "mat_gold_nugget", "Material Gold Nugget", "Materiales", ":mat_gold_nugget:", "textures/font/icons/materials/gold_nugget.png");
        register("\uE711", "mat_iron_nugget", "Material Iron Nugget", "Materiales", ":mat_iron_nugget:", "textures/font/icons/materials/iron_nugget.png");
        register("\uE712", "mat_prismarine_shard", "Material Prismarine Shard", "Materiales", ":mat_prismarine_shard:", "textures/font/icons/materials/prismarine_shard.png");
        register("\uE713", "mat_prismarine_crystals", "Material Prismarine Crystals", "Materiales", ":mat_prismarine_crystals:", "textures/font/icons/materials/prismarine_crystals.png");
        register("\uE714", "mat_echo_shard", "Material Echo Shard", "Materiales", ":mat_echo_shard:", "textures/font/icons/materials/echo_shard.png");
        register("\uE715", "mat_flint", "Material Flint", "Materiales", ":mat_flint:", "textures/font/icons/materials/flint.png");
        register("\uE716", "mat_clay_ball", "Material Clay Ball", "Materiales", ":mat_clay_ball:", "textures/font/icons/materials/clay_ball.png");
        register("\uE717", "mat_brick", "Material Brick", "Materiales", ":mat_brick:", "textures/font/icons/materials/brick.png");
        register("\uE718", "mat_nether_brick", "Material Nether Brick", "Materiales", ":mat_nether_brick:", "textures/font/icons/materials/nether_brick.png");
        register("\uE719", "mat_resin_clump", "Material Resin Clump", "Materiales", ":mat_resin_clump:", "textures/font/icons/materials/resin_clump.png");
        register("\uE71A", "mat_resin_brick", "Material Resin Brick", "Materiales", ":mat_resin_brick:", "textures/font/icons/materials/resin_brick.png");
        register("\uE7B0", "shop_balance_scale", "Shop Balance Scale", "Mercado & Tienda", ":balance_scale:", "textures/font/icons/market_shops/balance_scale.png");
        register("\uE7B1", "shop_black_market", "Shop Black Market", "Mercado & Tienda", ":black_market:", "textures/font/icons/market_shops/black_market.png");
        register("\uE7B2", "shop_booster_pass", "Shop Booster Pass", "Mercado & Tienda", ":booster_pass:", "textures/font/icons/market_shops/booster_pass.png");
        register("\uE7B3", "shop_bounty_contract", "Shop Bounty Contract", "Mercado & Tienda", ":bounty_contract:", "textures/font/icons/market_shops/bounty_contract.png");
        register("\uE7B4", "shop_buy_basket", "Shop Buy Basket", "Mercado & Tienda", ":buy_basket:", "textures/font/icons/market_shops/buy_basket.png");
        register("\uE7B5", "shop_cart_checkout", "Shop Cart Checkout", "Mercado & Tienda", ":cart_checkout:", "textures/font/icons/market_shops/cart_checkout.png");
        register("\uE7B6", "shop_crate_key", "Shop Crate Key", "Mercado & Tienda", ":crate_key:", "textures/font/icons/market_shops/crate_key.png");
        register("\uE7B7", "shop_currency_coins", "Shop Currency Coins", "Mercado & Tienda", ":currency_coins:", "textures/font/icons/market_shops/currency_coins.png");
        register("\uE7B8", "shop_currency_crystals", "Shop Currency Crystals", "Mercado & Tienda", ":currency_crystals:", "textures/font/icons/market_shops/currency_crystals.png");
        register("\uE7B9", "shop_currency_emerald", "Shop Currency Emerald", "Mercado & Tienda", ":currency_emerald:", "textures/font/icons/market_shops/currency_emerald.png");
        register("\uE7BA", "shop_currency_tokens", "Shop Currency Tokens", "Mercado & Tienda", ":currency_tokens:", "textures/font/icons/market_shops/currency_tokens.png");
        register("\uE7BB", "shop_discount_percent", "Shop Discount Percent", "Mercado & Tienda", ":discount_percent:", "textures/font/icons/market_shops/discount_percent.png");
        register("\uE7BC", "shop_exchange_swap", "Shop Exchange Swap", "Mercado & Tienda", ":exchange_swap:", "textures/font/icons/market_shops/exchange_swap.png");
        register("\uE7BD", "shop_golden_pouch", "Shop Golden Pouch", "Mercado & Tienda", ":golden_pouch:", "textures/font/icons/market_shops/golden_pouch.png");
        register("\uE7BE", "shop_sell_chest", "Shop Sell Chest", "Mercado & Tienda", ":sell_chest:", "textures/font/icons/market_shops/sell_chest.png");
        register("\uE7BF", "shop_shop_building", "Shop Shop Building", "Mercado & Tienda", ":shop_building:", "textures/font/icons/market_shops/shop_building.png");
        register("\uE7C0", "shop_shop_farming", "Shop Shop Farming", "Mercado & Tienda", ":shop_farming:", "textures/font/icons/market_shops/shop_farming.png");
        register("\uE7C1", "shop_shop_food", "Shop Shop Food", "Mercado & Tienda", ":shop_food:", "textures/font/icons/market_shops/shop_food.png");
        register("\uE7C2", "shop_shop_mob_drops", "Shop Shop Mob Drops", "Mercado & Tienda", ":shop_mob_drops:", "textures/font/icons/market_shops/shop_mob_drops.png");
        register("\uE7C3", "shop_shop_nether_end", "Shop Shop Nether End", "Mercado & Tienda", ":shop_nether_end:", "textures/font/icons/market_shops/shop_nether_end.png");
        register("\uE7C4", "shop_shop_ores", "Shop Shop Ores", "Mercado & Tienda", ":shop_ores:", "textures/font/icons/market_shops/shop_ores.png");
        register("\uE7C5", "shop_shop_potions", "Shop Shop Potions", "Mercado & Tienda", ":shop_potions:", "textures/font/icons/market_shops/shop_potions.png");
        register("\uE7C6", "shop_shop_redstone", "Shop Shop Redstone", "Mercado & Tienda", ":shop_redstone:", "textures/font/icons/market_shops/shop_redstone.png");
        register("\uE7C7", "shop_shop_ticket", "Shop Shop Ticket", "Mercado & Tienda", ":shop_ticket:", "textures/font/icons/market_shops/shop_ticket.png");
        register("\uE7C8", "shop_shop_wallet", "Shop Shop Wallet", "Mercado & Tienda", ":shop_wallet:", "textures/font/icons/market_shops/shop_wallet.png");
        register("\uE7C9", "shop_shop_weapons", "Shop Shop Weapons", "Mercado & Tienda", ":shop_weapons:", "textures/font/icons/market_shops/shop_weapons.png");
        register("\uE7CA", "shop_special_offer", "Shop Special Offer", "Mercado & Tienda", ":special_offer:", "textures/font/icons/market_shops/special_offer.png");
        register("\uE7CB", "shop_star_points", "Shop Star Points", "Mercado & Tienda", ":star_points:", "textures/font/icons/market_shops/star_points.png");
        register("\uE7CC", "shop_store_crates", "Shop Store Crates", "Mercado & Tienda", ":store_crates:", "textures/font/icons/market_shops/store_crates.png");
        register("\uE7CD", "shop_store_ranks", "Shop Store Ranks", "Mercado & Tienda", ":store_ranks:", "textures/font/icons/market_shops/store_ranks.png");
    }

    private static void initFoodsAndPotions() {
        register("\uE422", "food_acorn", "Comida Acorn", "Comidas", ":food_acorn:", "textures/font/icons/foods/acorn.png");
        register("\uE423", "food_bacon", "Comida Bacon", "Comidas", ":food_bacon:", "textures/font/icons/foods/bacon.png");
        register("\uE424", "food_banana", "Comida Banana", "Comidas", ":food_banana:", "textures/font/icons/foods/banana.png");
        register("\uE425", "food_beer", "Comida Beer", "Comidas", ":food_beer:", "textures/font/icons/foods/beer.png");
        register("\uE426", "food_boba", "Comida Boba", "Comidas", ":food_boba:", "textures/font/icons/foods/boba.png");
        register("\uE427", "food_bread", "Comida Bread", "Comidas", ":food_bread:", "textures/font/icons/foods/bread.png");
        register("\uE428", "food_broccoli", "Comida Broccoli", "Comidas", ":food_broccoli:", "textures/font/icons/foods/broccoli.png");
        register("\uE429", "food_burrito", "Comida Burrito", "Comidas", ":food_burrito:", "textures/font/icons/foods/burrito.png");
        register("\uE42A", "food_cheese", "Comida Cheese", "Comidas", ":food_cheese:", "textures/font/icons/foods/cheese.png");
        register("\uE42B", "food_chocolate", "Comida Chocolate", "Comidas", ":food_chocolate:", "textures/font/icons/foods/chocolate.png");
        register("\uE42C", "food_coca-cola", "Comida Coca-Cola", "Comidas", ":food_coca-cola:", "textures/font/icons/foods/coca-cola.png");
        register("\uE42D", "food_coconut", "Comida Coconut", "Comidas", ":food_coconut:", "textures/font/icons/foods/coconut.png");
        register("\uE42E", "food_cookie-donut", "Comida Cookie-Donut", "Comidas", ":food_cookie-donut:", "textures/font/icons/foods/cookie-donut.png");
        register("\uE42F", "food_cotton-candy", "Comida Cotton-Candy", "Comidas", ":food_cotton-candy:", "textures/font/icons/foods/cotton-candy.png");
        register("\uE430", "food_donut", "Comida Donut", "Comidas", ":food_donut:", "textures/font/icons/foods/donut.png");
        register("\uE431", "food_fanta", "Comida Fanta", "Comidas", ":food_fanta:", "textures/font/icons/foods/fanta.png");
        register("\uE432", "food_fry-egg", "Comida Fry-Egg", "Comidas", ":food_fry-egg:", "textures/font/icons/foods/fry-egg.png");
        register("\uE433", "food_ham", "Comida Ham", "Comidas", ":food_ham:", "textures/font/icons/foods/ham.png");
        register("\uE434", "food_hamburger", "Comida Hamburger", "Comidas", ":food_hamburger:", "textures/font/icons/foods/hamburger.png");
        register("\uE435", "food_honey", "Comida Honey", "Comidas", ":food_honey:", "textures/font/icons/foods/honey.png");
        register("\uE436", "food_hotdog", "Comida Hotdog", "Comidas", ":food_hotdog:", "textures/font/icons/foods/hotdog.png");
        register("\uE437", "food_ice-cream1", "Comida Ice-Cream1", "Comidas", ":food_ice-cream1:", "textures/font/icons/foods/ice-cream1.png");
        register("\uE438", "food_ice-cream2", "Comida Ice-Cream2", "Comidas", ":food_ice-cream2:", "textures/font/icons/foods/ice-cream2.png");
        register("\uE439", "food_jelly", "Comida Jelly", "Comidas", ":food_jelly:", "textures/font/icons/foods/jelly.png");
        register("\uE43A", "food_mango", "Comida Mango", "Comidas", ":food_mango:", "textures/font/icons/foods/mango.png");
        register("\uE43B", "food_muffin", "Comida Muffin", "Comidas", ":food_muffin:", "textures/font/icons/foods/muffin.png");
        register("\uE43C", "food_nutella", "Comida Nutella", "Comidas", ":food_nutella:", "textures/font/icons/foods/nutella.png");
        register("\uE43D", "food_onion", "Comida Onion", "Comidas", ":food_onion:", "textures/font/icons/foods/onion.png");
        register("\uE43E", "food_oreo", "Comida Oreo", "Comidas", ":food_oreo:", "textures/font/icons/foods/oreo.png");
        register("\uE43F", "food_peach", "Comida Peach", "Comidas", ":food_peach:", "textures/font/icons/foods/peach.png");
        register("\uE440", "food_pear", "Comida Pear", "Comidas", ":food_pear:", "textures/font/icons/foods/pear.png");
        register("\uE441", "food_pepsi-cola", "Comida Pepsi-Cola", "Comidas", ":food_pepsi-cola:", "textures/font/icons/foods/pepsi-cola.png");
        register("\uE442", "food_pie-slice", "Comida Pie-Slice", "Comidas", ":food_pie-slice:", "textures/font/icons/foods/pie-slice.png");
        register("\uE443", "food_pie", "Comida Pie", "Comidas", ":food_pie:", "textures/font/icons/foods/pie.png");
        register("\uE444", "food_pizza", "Comida Pizza", "Comidas", ":food_pizza:", "textures/font/icons/foods/pizza.png");
        register("\uE445", "food_plum", "Comida Plum", "Comidas", ":food_plum:", "textures/font/icons/foods/plum.png");
        register("\uE446", "food_popcorn", "Comida Popcorn", "Comidas", ":food_popcorn:", "textures/font/icons/foods/popcorn.png");
        register("\uE447", "food_pumpkin", "Comida Pumpkin", "Comidas", ":food_pumpkin:", "textures/font/icons/foods/pumpkin.png");
        register("\uE448", "food_sprite", "Comida Sprite", "Comidas", ":food_sprite:", "textures/font/icons/foods/sprite.png");
        register("\uE449", "food_strawberry-jam", "Comida Strawberry-Jam", "Comidas", ":food_strawberry-jam:", "textures/font/icons/foods/strawberry-jam.png");
        register("\uE44A", "food_strawberry", "Comida Strawberry", "Comidas", ":food_strawberry:", "textures/font/icons/foods/strawberry.png");
        register("\uE44B", "food_taco", "Comida Taco", "Comidas", ":food_taco:", "textures/font/icons/foods/taco.png");
        register("\uE44C", "food_toast", "Comida Toast", "Comidas", ":food_toast:", "textures/font/icons/foods/toast.png");
        register("\uE44D", "food_tofu", "Comida Tofu", "Comidas", ":food_tofu:", "textures/font/icons/foods/tofu.png");
        register("\uE44E", "food_tomato", "Comida Tomato", "Comidas", ":food_tomato:", "textures/font/icons/foods/tomato.png");
        register("\uE44F", "food_waffle", "Comida Waffle", "Comidas", ":food_waffle:", "textures/font/icons/foods/waffle.png");
        register("\uE450", "pot_blue_potion", "Poción Blue Potion", "Pociones", ":pot_blue_potion:", "textures/font/icons/potions/blue_potion.png");
        register("\uE451", "pot_green_potion", "Poción Green Potion", "Pociones", ":pot_green_potion:", "textures/font/icons/potions/green_potion.png");
        register("\uE452", "pot_orange_potion", "Poción Orange Potion", "Pociones", ":pot_orange_potion:", "textures/font/icons/potions/orange_potion.png");
        register("\uE453", "pot_pink_potion", "Poción Pink Potion", "Pociones", ":pot_pink_potion:", "textures/font/icons/potions/pink_potion.png");
        register("\uE454", "pot_purple_potion", "Poción Purple Potion", "Pociones", ":pot_purple_potion:", "textures/font/icons/potions/purple_potion.png");
        register("\uE455", "pot_red_potion", "Poción Red Potion", "Pociones", ":pot_red_potion:", "textures/font/icons/potions/red_potion.png");
    }

    private static void initBasicUiAndEffects() {
        register("\uE400", "ui_arrow_down", "UI Arrow Down", "UI", ":ui_arrow_down:", "textures/font/icons/basic_ui/arrow_down.png");
        register("\uE401", "ui_arrow_left", "UI Arrow Left", "UI", ":ui_arrow_left:", "textures/font/icons/basic_ui/arrow_left.png");
        register("\uE402", "ui_arrow_right", "UI Arrow Right", "UI", ":ui_arrow_right:", "textures/font/icons/basic_ui/arrow_right.png");
        register("\uE403", "ui_arrow_up", "UI Arrow Up", "UI", ":ui_arrow_up:", "textures/font/icons/basic_ui/arrow_up.png");
        register("\uE404", "ui_check", "UI Check", "UI", ":ui_check:", "textures/font/icons/basic_ui/check.png");
        register("\uE405", "ui_check_color", "UI Check Color", "UI", ":ui_check_color:", "textures/font/icons/basic_ui/check_color.png");
        register("\uE406", "ui_close", "UI Close", "UI", ":ui_close:", "textures/font/icons/basic_ui/close.png");
        register("\uE407", "ui_close_color", "UI Close Color", "UI", ":ui_close_color:", "textures/font/icons/basic_ui/close_color.png");
        register("\uE408", "ui_coins", "UI Coins", "UI", ":ui_coins:", "textures/font/icons/basic_ui/coins.png");
        register("\uE409", "ui_exclamation", "UI Exclamation", "UI", ":ui_exclamation:", "textures/font/icons/basic_ui/exclamation.png");
        register("\uE40A", "ui_exclamation_color", "UI Exclamation Color", "UI", ":ui_exclamation_color:", "textures/font/icons/basic_ui/exclamation_color.png");
        register("\uE40B", "ui_lock_closed", "UI Lock Closed", "UI", ":ui_lock_closed:", "textures/font/icons/basic_ui/lock_closed.png");
        register("\uE40C", "ui_lock_open", "UI Lock Open", "UI", ":ui_lock_open:", "textures/font/icons/basic_ui/lock_open.png");
        register("\uE40D", "ui_minus", "UI Minus", "UI", ":ui_minus:", "textures/font/icons/basic_ui/minus.png");
        register("\uE40E", "ui_minus_color", "UI Minus Color", "UI", ":ui_minus_color:", "textures/font/icons/basic_ui/minus_color.png");
        register("\uE40F", "ui_plus", "UI Plus", "UI", ":ui_plus:", "textures/font/icons/basic_ui/plus.png");
        register("\uE410", "ui_plus_color", "UI Plus Color", "UI", ":ui_plus_color:", "textures/font/icons/basic_ui/plus_color.png");
        register("\uE411", "ui_question", "UI Question", "UI", ":ui_question:", "textures/font/icons/basic_ui/question.png");
        register("\uE412", "ui_question_color", "UI Question Color", "UI", ":ui_question_color:", "textures/font/icons/basic_ui/question_color.png");
        register("\uE413", "ui_refresh", "UI Refresh", "UI", ":ui_refresh:", "textures/font/icons/basic_ui/refresh.png");
        register("\uE414", "ui_search", "UI Search", "UI", ":ui_search:", "textures/font/icons/basic_ui/search.png");
        register("\uE415", "ui_settings", "UI Settings", "UI", ":ui_settings:", "textures/font/icons/basic_ui/settings.png");
        register("\uE374", "eff_bad_omen", "Efecto Bad Omen", "Efectos", ":eff_bad_omen:", "textures/font/icons/effects/bad_omen.png");
        register("\uE375", "eff_blindness", "Efecto Blindness", "Efectos", ":eff_blindness:", "textures/font/icons/effects/blindness.png");
        register("\uE376", "eff_breath_of_the_nautilus", "Efecto Breath Of The Nautilus", "Efectos", ":eff_breath_of_the_nautilus:", "textures/font/icons/effects/breath_of_the_nautilus.png");
        register("\uE377", "eff_conduit_power", "Efecto Conduit Power", "Efectos", ":eff_conduit_power:", "textures/font/icons/effects/conduit_power.png");
        register("\uE378", "eff_darkness", "Efecto Darkness", "Efectos", ":eff_darkness:", "textures/font/icons/effects/darkness.png");
        register("\uE379", "eff_dolphins_grace", "Efecto Dolphins Grace", "Efectos", ":eff_dolphins_grace:", "textures/font/icons/effects/dolphins_grace.png");
        register("\uE37A", "eff_glowing", "Efecto Glowing", "Efectos", ":eff_glowing:", "textures/font/icons/effects/glowing.png");
        register("\uE37B", "eff_hero_of_the_village", "Efecto Hero Of The Village", "Efectos", ":eff_hero_of_the_village:", "textures/font/icons/effects/hero_of_the_village.png");
        register("\uE37C", "eff_hunger", "Efecto Hunger", "Efectos", ":eff_hunger:", "textures/font/icons/effects/hunger.png");
        register("\uE37D", "eff_infested", "Efecto Infested", "Efectos", ":eff_infested:", "textures/font/icons/effects/infested.png");
        register("\uE37E", "eff_instant_damage", "Efecto Instant Damage", "Efectos", ":eff_instant_damage:", "textures/font/icons/effects/instant_damage.png");
        register("\uE37F", "eff_instant_health", "Efecto Instant Health", "Efectos", ":eff_instant_health:", "textures/font/icons/effects/instant_health.png");
        register("\uE380", "eff_invisibility", "Efecto Invisibility", "Efectos", ":eff_invisibility:", "textures/font/icons/effects/invisibility.png");
        register("\uE381", "eff_levitation", "Efecto Levitation", "Efectos", ":eff_levitation:", "textures/font/icons/effects/levitation.png");
        register("\uE382", "eff_luck", "Efecto Luck", "Efectos", ":eff_luck:", "textures/font/icons/effects/luck.png");
        register("\uE383", "eff_mining_fatigue", "Efecto Mining Fatigue", "Efectos", ":eff_mining_fatigue:", "textures/font/icons/effects/mining_fatigue.png");
        register("\uE384", "eff_nausea", "Efecto Nausea", "Efectos", ":eff_nausea:", "textures/font/icons/effects/nausea.png");
        register("\uE385", "eff_oozing", "Efecto Oozing", "Efectos", ":eff_oozing:", "textures/font/icons/effects/oozing.png");
        register("\uE386", "eff_poison", "Efecto Poison", "Efectos", ":eff_poison:", "textures/font/icons/effects/poison.png");
        register("\uE387", "eff_raid_omen", "Efecto Raid Omen", "Efectos", ":eff_raid_omen:", "textures/font/icons/effects/raid_omen.png");
        register("\uE388", "eff_slow_falling", "Efecto Slow Falling", "Efectos", ":eff_slow_falling:", "textures/font/icons/effects/slow_falling.png");
        register("\uE389", "eff_slowness", "Efecto Slowness", "Efectos", ":eff_slowness:", "textures/font/icons/effects/slowness.png");
        register("\uE38A", "eff_trial_omen", "Efecto Trial Omen", "Efectos", ":eff_trial_omen:", "textures/font/icons/effects/trial_omen.png");
        register("\uE38B", "eff_unluck", "Efecto Unluck", "Efectos", ":eff_unluck:", "textures/font/icons/effects/unluck.png");
        register("\uE38C", "eff_weakness", "Efecto Weakness", "Efectos", ":eff_weakness:", "textures/font/icons/effects/weakness.png");
        register("\uE38D", "eff_weaving", "Efecto Weaving", "Efectos", ":eff_weaving:", "textures/font/icons/effects/weaving.png");
        register("\uE38E", "eff_wind_charged", "Efecto Wind Charged", "Efectos", ":eff_wind_charged:", "textures/font/icons/effects/wind_charged.png");
        register("\uE38F", "eff_wither", "Efecto Wither", "Efectos", ":eff_wither:", "textures/font/icons/effects/wither.png");
        register("\uE416", "eff_absorption", "Efecto Absorption", "Efectos", ":eff_absorption:", "textures/font/icons/effects/absorption.png");
        register("\uE417", "eff_fire_resistance", "Efecto Fire Resistance", "Efectos", ":eff_fire_resistance:", "textures/font/icons/effects/fire_resistance.png");
        register("\uE418", "eff_haste", "Efecto Haste", "Efectos", ":eff_haste:", "textures/font/icons/effects/haste.png");
        register("\uE419", "eff_health_boost", "Efecto Health Boost", "Efectos", ":eff_health_boost:", "textures/font/icons/effects/health_boost.png");
        register("\uE41A", "eff_jump_boost", "Efecto Jump Boost", "Efectos", ":eff_jump_boost:", "textures/font/icons/effects/jump_boost.png");
        register("\uE41B", "eff_night_vision", "Efecto Night Vision", "Efectos", ":eff_night_vision:", "textures/font/icons/effects/night_vision.png");
        register("\uE41C", "eff_regeneration", "Efecto Regeneration", "Efectos", ":eff_regeneration:", "textures/font/icons/effects/regeneration.png");
        register("\uE41D", "eff_resistance", "Efecto Resistance", "Efectos", ":eff_resistance:", "textures/font/icons/effects/resistance.png");
        register("\uE41E", "eff_saturation", "Efecto Saturation", "Efectos", ":eff_saturation:", "textures/font/icons/effects/saturation.png");
        register("\uE41F", "eff_speed", "Efecto Speed", "Efectos", ":eff_speed:", "textures/font/icons/effects/speed.png");
        register("\uE420", "eff_strength", "Efecto Strength", "Efectos", ":eff_strength:", "textures/font/icons/effects/strength.png");
        register("\uE421", "eff_water_breathing", "Efecto Water Breathing", "Efectos", ":eff_water_breathing:", "textures/font/icons/effects/water_breathing.png");
    }

    private static void initSocial() {
        register("\uE456", "social_discord", "Social Discord", "Redes Sociales", ":social_discord:", "textures/font/icons/social/discord.png");
        register("\uE457", "social_youtube", "Social Youtube", "Redes Sociales", ":social_youtube:", "textures/font/icons/social/youtube.png");
        register("\uE458", "social_twitch", "Social Twitch", "Redes Sociales", ":social_twitch:", "textures/font/icons/social/twitch.png");
        register("\uE459", "social_instagram", "Social Instagram", "Redes Sociales", ":social_instagram:", "textures/font/icons/social/instagram.png");
        register("\uE45A", "social_twitter", "Social Twitter", "Redes Sociales", ":social_twitter:", "textures/font/icons/social/twitter.png");
        register("\uE45B", "social_xtwitter", "Social Xtwitter", "Redes Sociales", ":social_xtwitter:", "textures/font/icons/social/xtwitter.png");
        register("\uE45C", "social_tiktok", "Social Tiktok", "Redes Sociales", ":social_tiktok:", "textures/font/icons/social/tiktok.png");
        register("\uE45D", "social_reddit", "Social Reddit", "Redes Sociales", ":social_reddit:", "textures/font/icons/social/reddit.png");
        register("\uE45E", "social_github", "Social Github", "Redes Sociales", ":social_github:", "textures/font/icons/social/github.png");
        register("\uE45F", "social_spotify", "Social Spotify", "Redes Sociales", ":social_spotify:", "textures/font/icons/social/spotify.png");
        register("\uE460", "social_telegram", "Social Telegram", "Redes Sociales", ":social_telegram:", "textures/font/icons/social/telegram.png");
        register("\uE461", "social_whatsapp", "Social Whatsapp", "Redes Sociales", ":social_whatsapp:", "textures/font/icons/social/whatsapp.png");
        register("\uE462", "social_facebook", "Social Facebook", "Redes Sociales", ":social_facebook:", "textures/font/icons/social/facebook.png");
        register("\uE463", "social_linkedin", "Social Linkedin", "Redes Sociales", ":social_linkedin:", "textures/font/icons/social/linkedin.png");
        register("\uE464", "social_kick", "Social Kick", "Redes Sociales", ":social_kick:", "textures/font/icons/social/kick.png");
        register("\uE465", "social_patreon", "Social Patreon", "Redes Sociales", ":social_patreon:", "textures/font/icons/social/patreon.png");
        register("\uE466", "social_paypal", "Social Paypal", "Redes Sociales", ":social_paypal:", "textures/font/icons/social/paypal.png");
        register("\uE467", "social_minecraft", "Social Minecraft", "Redes Sociales", ":social_minecraft:", "textures/font/icons/social/minecraft.png");
        register("\uE468", "social_modrinth", "Social Modrinth", "Redes Sociales", ":social_modrinth:", "textures/font/icons/social/modrinth.png");
        register("\uE469", "social_snapchat", "Social Snapchat", "Redes Sociales", ":social_snapchat:", "textures/font/icons/social/snapchat.png");
        register("\uE46A", "social_google", "Social Google", "Redes Sociales", ":social_google:", "textures/font/icons/social/google.png");
        register("\uE46B", "social_microsoft", "Social Microsoft", "Redes Sociales", ":social_microsoft:", "textures/font/icons/social/microsoft.png");
        register("\uE46C", "social_windows", "Social Windows", "Redes Sociales", ":social_windows:", "textures/font/icons/social/windows.png");
        register("\uE46D", "social_notion", "Social Notion", "Redes Sociales", ":social_notion:", "textures/font/icons/social/notion.png");
        register("\uE46E", "social_trello", "Social Trello", "Redes Sociales", ":social_trello:", "textures/font/icons/social/trello.png");
        register("\uE46F", "social_threads", "Social Threads", "Redes Sociales", ":social_threads:", "textures/font/icons/social/threads.png");
        register("\uE470", "social_teamspeak", "Social Teamspeak", "Redes Sociales", ":social_teamspeak:", "textures/font/icons/social/teamspeak.png");
        register("\uE471", "social_kofi", "Social Kofi", "Redes Sociales", ":social_kofi:", "textures/font/icons/social/kofi.png");
        register("\uE472", "social_bluesky", "Social Bluesky", "Redes Sociales", ":social_bluesky:", "textures/font/icons/social/bluesky.png");
        register("\uE473", "social_vk", "Social Vk", "Redes Sociales", ":social_vk:", "textures/font/icons/social/vk.png");
        register("\uE474", "social_tumblr", "Social Tumblr", "Redes Sociales", ":social_tumblr:", "textures/font/icons/social/tumblr.png");
        register("\uE475", "social_stripe", "Social Stripe", "Redes Sociales", ":social_stripe:", "textures/font/icons/social/stripe.png");
        register("\uE476", "social_wikipedia", "Social Wikipedia", "Redes Sociales", ":social_wikipedia:", "textures/font/icons/social/wikipedia.png");
        register("\uE477", "social_android", "Social Android", "Redes Sociales", ":social_android:", "textures/font/icons/social/android.png");
        register("\uE478", "social_apple", "Social Apple", "Redes Sociales", ":social_apple:", "textures/font/icons/social/apple.png");
        register("\uE479", "social_artstation", "Social Artstation", "Redes Sociales", ":social_artstation:", "textures/font/icons/social/artstation.png");
        register("\uE47A", "social_cruseforge", "Social Cruseforge", "Redes Sociales", ":social_cruseforge:", "textures/font/icons/social/cruseforge.png");
        register("\uE47B", "social_deviantart", "Social Deviantart", "Redes Sociales", ":social_deviantart:", "textures/font/icons/social/deviantart.png");
        register("\uE47C", "social_domain", "Social Domain", "Redes Sociales", ":social_domain:", "textures/font/icons/social/domain.png");
        register("\uE47D", "social_fandom", "Social Fandom", "Redes Sociales", ":social_fandom:", "textures/font/icons/social/fandom.png");
        register("\uE47E", "social_patreon2", "Social Patreon2", "Redes Sociales", ":social_patreon2:", "textures/font/icons/social/patreon2.png");
        register("\uE47F", "social_promptpay", "Social Promptpay", "Redes Sociales", ":social_promptpay:", "textures/font/icons/social/promptpay.png");
        register("\uE480", "social_qrcode", "Social Qrcode", "Redes Sociales", ":social_qrcode:", "textures/font/icons/social/qrcode.png");
        register("\uE481", "social_qrpayment", "Social Qrpayment", "Redes Sociales", ":social_qrpayment:", "textures/font/icons/social/qrpayment.png");
        register("\uE482", "social_sketchfab", "Social Sketchfab", "Redes Sociales", ":social_sketchfab:", "textures/font/icons/social/sketchfab.png");
        register("\uE483", "social_truewallet", "Social Truewallet", "Redes Sociales", ":social_truewallet:", "textures/font/icons/social/truewallet.png");
        register("\uE484", "social_wise", "Social Wise", "Redes Sociales", ":social_wise:", "textures/font/icons/social/wise.png");
    }

    private static void initCapella() {
        register("\uE485", "general_icon_1", "Capella General Icon 1", "Capella", ":general_icon_1:", "textures/font/icons/capella/general_icon_1.png");
        register("\uE486", "general_icon_10", "Capella General Icon 10", "Capella", ":general_icon_10:", "textures/font/icons/capella/general_icon_10.png");
        register("\uE487", "general_icon_11", "Capella General Icon 11", "Capella", ":general_icon_11:", "textures/font/icons/capella/general_icon_11.png");
        register("\uE488", "general_icon_12", "Capella General Icon 12", "Capella", ":general_icon_12:", "textures/font/icons/capella/general_icon_12.png");
        register("\uE489", "general_icon_13", "Capella General Icon 13", "Capella", ":general_icon_13:", "textures/font/icons/capella/general_icon_13.png");
        register("\uE48A", "general_icon_14", "Capella General Icon 14", "Capella", ":general_icon_14:", "textures/font/icons/capella/general_icon_14.png");
        register("\uE48B", "general_icon_15", "Capella General Icon 15", "Capella", ":general_icon_15:", "textures/font/icons/capella/general_icon_15.png");
        register("\uE48C", "general_icon_15_0b", "Capella General Icon 15 0B", "Capella", ":general_icon_15_0b:", "textures/font/icons/capella/general_icon_15_0b.png");
        register("\uE48D", "general_icon_16", "Capella General Icon 16", "Capella", ":general_icon_16:", "textures/font/icons/capella/general_icon_16.png");
        register("\uE48E", "general_icon_17", "Capella General Icon 17", "Capella", ":general_icon_17:", "textures/font/icons/capella/general_icon_17.png");
        register("\uE48F", "general_icon_17_0b", "Capella General Icon 17 0B", "Capella", ":general_icon_17_0b:", "textures/font/icons/capella/general_icon_17_0b.png");
        register("\uE490", "general_icon_18", "Capella General Icon 18", "Capella", ":general_icon_18:", "textures/font/icons/capella/general_icon_18.png");
        register("\uE491", "general_icon_19", "Capella General Icon 19", "Capella", ":general_icon_19:", "textures/font/icons/capella/general_icon_19.png");
        register("\uE492", "general_icon_2", "Capella General Icon 2", "Capella", ":general_icon_2:", "textures/font/icons/capella/general_icon_2.png");
        register("\uE493", "general_icon_20", "Capella General Icon 20", "Capella", ":general_icon_20:", "textures/font/icons/capella/general_icon_20.png");
        register("\uE494", "general_icon_21", "Capella General Icon 21", "Capella", ":general_icon_21:", "textures/font/icons/capella/general_icon_21.png");
        register("\uE495", "general_icon_22", "Capella General Icon 22", "Capella", ":general_icon_22:", "textures/font/icons/capella/general_icon_22.png");
        register("\uE496", "general_icon_23", "Capella General Icon 23", "Capella", ":general_icon_23:", "textures/font/icons/capella/general_icon_23.png");
        register("\uE497", "general_icon_24", "Capella General Icon 24", "Capella", ":general_icon_24:", "textures/font/icons/capella/general_icon_24.png");
        register("\uE498", "general_icon_25", "Capella General Icon 25", "Capella", ":general_icon_25:", "textures/font/icons/capella/general_icon_25.png");
        register("\uE499", "general_icon_26", "Capella General Icon 26", "Capella", ":general_icon_26:", "textures/font/icons/capella/general_icon_26.png");
        register("\uE49A", "general_icon_27", "Capella General Icon 27", "Capella", ":general_icon_27:", "textures/font/icons/capella/general_icon_27.png");
        register("\uE49B", "general_icon_28", "Capella General Icon 28", "Capella", ":general_icon_28:", "textures/font/icons/capella/general_icon_28.png");
        register("\uE49C", "general_icon_29", "Capella General Icon 29", "Capella", ":general_icon_29:", "textures/font/icons/capella/general_icon_29.png");
        register("\uE49D", "general_icon_3", "Capella General Icon 3", "Capella", ":general_icon_3:", "textures/font/icons/capella/general_icon_3.png");
        register("\uE49E", "general_icon_30", "Capella General Icon 30", "Capella", ":general_icon_30:", "textures/font/icons/capella/general_icon_30.png");
        register("\uE49F", "general_icon_31", "Capella General Icon 31", "Capella", ":general_icon_31:", "textures/font/icons/capella/general_icon_31.png");
        register("\uE4A0", "general_icon_32", "Capella General Icon 32", "Capella", ":general_icon_32:", "textures/font/icons/capella/general_icon_32.png");
        register("\uE4A1", "general_icon_33", "Capella General Icon 33", "Capella", ":general_icon_33:", "textures/font/icons/capella/general_icon_33.png");
        register("\uE4A2", "general_icon_34", "Capella General Icon 34", "Capella", ":general_icon_34:", "textures/font/icons/capella/general_icon_34.png");
        register("\uE4A3", "general_icon_35", "Capella General Icon 35", "Capella", ":general_icon_35:", "textures/font/icons/capella/general_icon_35.png");
        register("\uE4A4", "general_icon_36", "Capella General Icon 36", "Capella", ":general_icon_36:", "textures/font/icons/capella/general_icon_36.png");
        register("\uE4A5", "general_icon_37", "Capella General Icon 37", "Capella", ":general_icon_37:", "textures/font/icons/capella/general_icon_37.png");
        register("\uE4A6", "general_icon_38", "Capella General Icon 38", "Capella", ":general_icon_38:", "textures/font/icons/capella/general_icon_38.png");
        register("\uE4A7", "general_icon_39", "Capella General Icon 39", "Capella", ":general_icon_39:", "textures/font/icons/capella/general_icon_39.png");
        register("\uE4A8", "general_icon_4", "Capella General Icon 4", "Capella", ":general_icon_4:", "textures/font/icons/capella/general_icon_4.png");
        register("\uE4A9", "general_icon_40", "Capella General Icon 40", "Capella", ":general_icon_40:", "textures/font/icons/capella/general_icon_40.png");
        register("\uE4AA", "general_icon_41", "Capella General Icon 41", "Capella", ":general_icon_41:", "textures/font/icons/capella/general_icon_41.png");
        register("\uE4AB", "general_icon_42", "Capella General Icon 42", "Capella", ":general_icon_42:", "textures/font/icons/capella/general_icon_42.png");
        register("\uE4AC", "general_icon_43", "Capella General Icon 43", "Capella", ":general_icon_43:", "textures/font/icons/capella/general_icon_43.png");
        register("\uE4AD", "general_icon_44", "Capella General Icon 44", "Capella", ":general_icon_44:", "textures/font/icons/capella/general_icon_44.png");
        register("\uE4AE", "general_icon_45", "Capella General Icon 45", "Capella", ":general_icon_45:", "textures/font/icons/capella/general_icon_45.png");
        register("\uE4AF", "general_icon_45_0b", "Capella General Icon 45 0B", "Capella", ":general_icon_45_0b:", "textures/font/icons/capella/general_icon_45_0b.png");
        register("\uE4B0", "general_icon_46", "Capella General Icon 46", "Capella", ":general_icon_46:", "textures/font/icons/capella/general_icon_46.png");
        register("\uE4B1", "general_icon_46_0b", "Capella General Icon 46 0B", "Capella", ":general_icon_46_0b:", "textures/font/icons/capella/general_icon_46_0b.png");
        register("\uE4B2", "general_icon_47", "Capella General Icon 47", "Capella", ":general_icon_47:", "textures/font/icons/capella/general_icon_47.png");
        register("\uE4B3", "general_icon_47_0b", "Capella General Icon 47 0B", "Capella", ":general_icon_47_0b:", "textures/font/icons/capella/general_icon_47_0b.png");
        register("\uE4B4", "general_icon_48", "Capella General Icon 48", "Capella", ":general_icon_48:", "textures/font/icons/capella/general_icon_48.png");
        register("\uE4B5", "general_icon_49", "Capella General Icon 49", "Capella", ":general_icon_49:", "textures/font/icons/capella/general_icon_49.png");
        register("\uE4B6", "general_icon_5", "Capella General Icon 5", "Capella", ":general_icon_5:", "textures/font/icons/capella/general_icon_5.png");
        register("\uE4B7", "general_icon_50", "Capella General Icon 50", "Capella", ":general_icon_50:", "textures/font/icons/capella/general_icon_50.png");
        register("\uE4B8", "general_icon_51", "Capella General Icon 51", "Capella", ":general_icon_51:", "textures/font/icons/capella/general_icon_51.png");
        register("\uE4B9", "general_icon_52", "Capella General Icon 52", "Capella", ":general_icon_52:", "textures/font/icons/capella/general_icon_52.png");
        register("\uE4BA", "general_icon_53", "Capella General Icon 53", "Capella", ":general_icon_53:", "textures/font/icons/capella/general_icon_53.png");
        register("\uE4BB", "general_icon_54", "Capella General Icon 54", "Capella", ":general_icon_54:", "textures/font/icons/capella/general_icon_54.png");
        register("\uE4BC", "general_icon_55", "Capella General Icon 55", "Capella", ":general_icon_55:", "textures/font/icons/capella/general_icon_55.png");
        register("\uE4BD", "general_icon_56", "Capella General Icon 56", "Capella", ":general_icon_56:", "textures/font/icons/capella/general_icon_56.png");
        register("\uE4BE", "general_icon_57", "Capella General Icon 57", "Capella", ":general_icon_57:", "textures/font/icons/capella/general_icon_57.png");
        register("\uE4BF", "general_icon_58", "Capella General Icon 58", "Capella", ":general_icon_58:", "textures/font/icons/capella/general_icon_58.png");
        register("\uE4C0", "general_icon_59", "Capella General Icon 59", "Capella", ":general_icon_59:", "textures/font/icons/capella/general_icon_59.png");
        register("\uE4C1", "general_icon_6", "Capella General Icon 6", "Capella", ":general_icon_6:", "textures/font/icons/capella/general_icon_6.png");
        register("\uE4C2", "general_icon_60", "Capella General Icon 60", "Capella", ":general_icon_60:", "textures/font/icons/capella/general_icon_60.png");
        register("\uE4C3", "general_icon_61", "Capella General Icon 61", "Capella", ":general_icon_61:", "textures/font/icons/capella/general_icon_61.png");
        register("\uE4C4", "general_icon_62", "Capella General Icon 62", "Capella", ":general_icon_62:", "textures/font/icons/capella/general_icon_62.png");
        register("\uE4C5", "general_icon_63", "Capella General Icon 63", "Capella", ":general_icon_63:", "textures/font/icons/capella/general_icon_63.png");
        register("\uE4C6", "general_icon_63_0b", "Capella General Icon 63 0B", "Capella", ":general_icon_63_0b:", "textures/font/icons/capella/general_icon_63_0b.png");
        register("\uE4C7", "general_icon_63_1b", "Capella General Icon 63 1B", "Capella", ":general_icon_63_1b:", "textures/font/icons/capella/general_icon_63_1b.png");
        register("\uE4C8", "general_icon_63_2b", "Capella General Icon 63 2B", "Capella", ":general_icon_63_2b:", "textures/font/icons/capella/general_icon_63_2b.png");
        register("\uE4C9", "general_icon_63_3b", "Capella General Icon 63 3B", "Capella", ":general_icon_63_3b:", "textures/font/icons/capella/general_icon_63_3b.png");
        register("\uE4CA", "general_icon_63_4b", "Capella General Icon 63 4B", "Capella", ":general_icon_63_4b:", "textures/font/icons/capella/general_icon_63_4b.png");
        register("\uE4CB", "general_icon_63_5b", "Capella General Icon 63 5B", "Capella", ":general_icon_63_5b:", "textures/font/icons/capella/general_icon_63_5b.png");
        register("\uE4CC", "general_icon_63_6b", "Capella General Icon 63 6B", "Capella", ":general_icon_63_6b:", "textures/font/icons/capella/general_icon_63_6b.png");
        register("\uE4CD", "general_icon_64", "Capella General Icon 64", "Capella", ":general_icon_64:", "textures/font/icons/capella/general_icon_64.png");
        register("\uE4CE", "general_icon_65", "Capella General Icon 65", "Capella", ":general_icon_65:", "textures/font/icons/capella/general_icon_65.png");
        register("\uE4CF", "general_icon_65_0b", "Capella General Icon 65 0B", "Capella", ":general_icon_65_0b:", "textures/font/icons/capella/general_icon_65_0b.png");
        register("\uE4D0", "general_icon_65_1b", "Capella General Icon 65 1B", "Capella", ":general_icon_65_1b:", "textures/font/icons/capella/general_icon_65_1b.png");
        register("\uE4D1", "general_icon_65_2b", "Capella General Icon 65 2B", "Capella", ":general_icon_65_2b:", "textures/font/icons/capella/general_icon_65_2b.png");
        register("\uE4D2", "general_icon_65_3b", "Capella General Icon 65 3B", "Capella", ":general_icon_65_3b:", "textures/font/icons/capella/general_icon_65_3b.png");
        register("\uE4D3", "general_icon_65_4b", "Capella General Icon 65 4B", "Capella", ":general_icon_65_4b:", "textures/font/icons/capella/general_icon_65_4b.png");
        register("\uE4D4", "general_icon_65_5b", "Capella General Icon 65 5B", "Capella", ":general_icon_65_5b:", "textures/font/icons/capella/general_icon_65_5b.png");
        register("\uE4D5", "general_icon_65_6b", "Capella General Icon 65 6B", "Capella", ":general_icon_65_6b:", "textures/font/icons/capella/general_icon_65_6b.png");
        register("\uE4D6", "general_icon_66", "Capella General Icon 66", "Capella", ":general_icon_66:", "textures/font/icons/capella/general_icon_66.png");
        register("\uE4D7", "general_icon_67", "Capella General Icon 67", "Capella", ":general_icon_67:", "textures/font/icons/capella/general_icon_67.png");
        register("\uE4D8", "general_icon_7", "Capella General Icon 7", "Capella", ":general_icon_7:", "textures/font/icons/capella/general_icon_7.png");
        register("\uE4D9", "general_icon_8", "Capella General Icon 8", "Capella", ":general_icon_8:", "textures/font/icons/capella/general_icon_8.png");
        register("\uE4DA", "general_icon_9", "Capella General Icon 9", "Capella", ":general_icon_9:", "textures/font/icons/capella/general_icon_9.png");
        register("\uE4DB", "general_icon_arrow_down", "Capella General Icon Arrow Down", "Capella", ":general_icon_arrow_down:", "textures/font/icons/capella/general_icon_arrow_down.png");
        register("\uE4DC", "general_icon_arrow_left", "Capella General Icon Arrow Left", "Capella", ":general_icon_arrow_left:", "textures/font/icons/capella/general_icon_arrow_left.png");
        register("\uE4DD", "general_icon_arrow_right", "Capella General Icon Arrow Right", "Capella", ":general_icon_arrow_right:", "textures/font/icons/capella/general_icon_arrow_right.png");
        register("\uE4DE", "general_icon_arrow_up", "Capella General Icon Arrow Up", "Capella", ":general_icon_arrow_up:", "textures/font/icons/capella/general_icon_arrow_up.png");
    }

    private static void initGeneral() {
        register("\uE4DF", "malvinas", "General Malvinas", "Combate & General", ":malvinas:", "textures/font/icons/general/malvinas.png");
        register("\uE4E0", "anvil", "General Anvil", "Combate & General", ":anvil:", "textures/font/icons/general/anvil.png");
        register("\uE4E1", "book", "General Book", "Combate & General", ":book:", "textures/font/icons/general/book.png");
        register("\uE4E2", "cave", "General Cave", "Combate & General", ":cave:", "textures/font/icons/general/cave.png");
        register("\uE4E3", "chef_hat", "General Chef Hat", "Combate & General", ":chef_hat:", "textures/font/icons/general/chef_hat.png");
        register("\uE4E4", "coins", "General Coins", "Combate & General", ":coins:", "textures/font/icons/general/coins.png");
        register("\uE4E5", "crown", "General Crown", "Combate & General", ":crown:", "textures/font/icons/general/crown.png");
        register("\uE4E6", "diamond", "General Diamond", "Combate & General", ":diamond:", "textures/font/icons/general/diamond.png");
        register("\uE4E7", "experience_orb", "General Experience Orb", "Combate & General", ":experience_orb:", "textures/font/icons/general/experience_orb.png");
        register("\uE4E8", "floating_island", "General Floating Island", "Combate & General", ":floating_island:", "textures/font/icons/general/floating_island.png");
        register("\uE4E9", "fountain", "General Fountain", "Combate & General", ":fountain:", "textures/font/icons/general/fountain.png");
        register("\uE4EA", "heart", "General Heart", "Combate & General", ":heart:", "textures/font/icons/general/heart.png");
        register("\uE4EB", "hourglass", "General Hourglass", "Combate & General", ":hourglass:", "textures/font/icons/general/hourglass.png");
        register("\uE4EC", "house", "General House", "Combate & General", ":house:", "textures/font/icons/general/house.png");
        register("\uE4ED", "judge_gavel", "General Judge Gavel", "Combate & General", ":judge_gavel:", "textures/font/icons/general/judge_gavel.png");
        register("\uE4EE", "key", "General Key", "Combate & General", ":key:", "textures/font/icons/general/key.png");
        register("\uE4EF", "level_up", "General Level Up", "Combate & General", ":level_up:", "textures/font/icons/general/level_up.png");
        register("\uE4F0", "magnifying_glass", "General Magnifying Glass", "Combate & General", ":magnifying_glass:", "textures/font/icons/general/magnifying_glass.png");
        register("\uE4F1", "mana", "General Mana", "Combate & General", ":mana:", "textures/font/icons/general/mana.png");
        register("\uE4F2", "moon", "General Moon", "Combate & General", ":moon:", "textures/font/icons/general/moon.png");
        register("\uE4F3", "plant", "General Plant", "Combate & General", ":plant:", "textures/font/icons/general/plant.png");
        register("\uE4F4", "potion", "General Potion", "Combate & General", ":potion:", "textures/font/icons/general/potion.png");
        register("\uE4F5", "purple_orb", "General Purple Orb", "Combate & General", ":purple_orb:", "textures/font/icons/general/purple_orb.png");
        register("\uE4F6", "scroll", "General Scroll", "Combate & General", ":scroll:", "textures/font/icons/general/scroll.png");
        register("\uE4F7", "shield", "General Shield", "Combate & General", ":shield:", "textures/font/icons/general/shield.png");
        register("\uE4F8", "skull", "General Skull", "Combate & General", ":skull:", "textures/font/icons/general/skull.png");
        register("\uE4F9", "star", "General Star", "Combate & General", ":star:", "textures/font/icons/general/star.png");
        register("\uE4FA", "sun", "General Sun", "Combate & General", ":sun:", "textures/font/icons/general/sun.png");
        register("\uE4FB", "sword", "General Sword", "Combate & General", ":sword:", "textures/font/icons/general/sword.png");
        register("\uE4FC", "trees", "General Trees", "Combate & General", ":trees:", "textures/font/icons/general/trees.png");
        register("\uE4FD", "wizard_tower", "General Wizard Tower", "Combate & General", ":wizard_tower:", "textures/font/icons/general/wizard_tower.png");
    }

    private static void initBlocks() {
        register("\uE500", "block_coal_ore", "Bloque Coal Ore", "Bloques", ":block_coal_ore:", "textures/font/icons/blocks/coal_ore.png");
        register("\uE501", "block_deepslate_coal_ore", "Bloque Deepslate Coal Ore", "Bloques", ":block_deepslate_coal_ore:", "textures/font/icons/blocks/deepslate_coal_ore.png");
        register("\uE502", "block_coal_block", "Bloque Coal Block", "Bloques", ":block_coal_block:", "textures/font/icons/blocks/coal_block.png");
        register("\uE503", "block_iron_ore", "Bloque Iron Ore", "Bloques", ":block_iron_ore:", "textures/font/icons/blocks/iron_ore.png");
        register("\uE504", "block_deepslate_iron_ore", "Bloque Deepslate Iron Ore", "Bloques", ":block_deepslate_iron_ore:", "textures/font/icons/blocks/deepslate_iron_ore.png");
        register("\uE505", "block_iron_block", "Bloque Iron Block", "Bloques", ":block_iron_block:", "textures/font/icons/blocks/iron_block.png");
        register("\uE506", "block_raw_iron_block", "Bloque Raw Iron Block", "Bloques", ":block_raw_iron_block:", "textures/font/icons/blocks/raw_iron_block.png");
        register("\uE507", "block_copper_ore", "Bloque Copper Ore", "Bloques", ":block_copper_ore:", "textures/font/icons/blocks/copper_ore.png");
        register("\uE508", "block_deepslate_copper_ore", "Bloque Deepslate Copper Ore", "Bloques", ":block_deepslate_copper_ore:", "textures/font/icons/blocks/deepslate_copper_ore.png");
        register("\uE509", "block_copper_block", "Bloque Copper Block", "Bloques", ":block_copper_block:", "textures/font/icons/blocks/copper_block.png");
        register("\uE50A", "block_raw_copper_block", "Bloque Raw Copper Block", "Bloques", ":block_raw_copper_block:", "textures/font/icons/blocks/raw_copper_block.png");
        register("\uE50B", "block_exposed_copper", "Bloque Exposed Copper", "Bloques", ":block_exposed_copper:", "textures/font/icons/blocks/exposed_copper.png");
        register("\uE50C", "block_weathered_copper", "Bloque Weathered Copper", "Bloques", ":block_weathered_copper:", "textures/font/icons/blocks/weathered_copper.png");
        register("\uE50D", "block_oxidized_copper", "Bloque Oxidized Copper", "Bloques", ":block_oxidized_copper:", "textures/font/icons/blocks/oxidized_copper.png");
        register("\uE50E", "block_cut_copper", "Bloque Cut Copper", "Bloques", ":block_cut_copper:", "textures/font/icons/blocks/cut_copper.png");
        register("\uE50F", "block_gold_ore", "Bloque Gold Ore", "Bloques", ":block_gold_ore:", "textures/font/icons/blocks/gold_ore.png");
        register("\uE510", "block_deepslate_gold_ore", "Bloque Deepslate Gold Ore", "Bloques", ":block_deepslate_gold_ore:", "textures/font/icons/blocks/deepslate_gold_ore.png");
        register("\uE511", "block_gold_block", "Bloque Gold Block", "Bloques", ":block_gold_block:", "textures/font/icons/blocks/gold_block.png");
        register("\uE512", "block_raw_gold_block", "Bloque Raw Gold Block", "Bloques", ":block_raw_gold_block:", "textures/font/icons/blocks/raw_gold_block.png");
        register("\uE513", "block_redstone_ore", "Bloque Redstone Ore", "Bloques", ":block_redstone_ore:", "textures/font/icons/blocks/redstone_ore.png");
        register("\uE514", "block_deepslate_redstone_ore", "Bloque Deepslate Redstone Ore", "Bloques", ":block_deepslate_redstone_ore:", "textures/font/icons/blocks/deepslate_redstone_ore.png");
        register("\uE515", "block_redstone_block", "Bloque Redstone Block", "Bloques", ":block_redstone_block:", "textures/font/icons/blocks/redstone_block.png");
        register("\uE516", "block_emerald_ore", "Bloque Emerald Ore", "Bloques", ":block_emerald_ore:", "textures/font/icons/blocks/emerald_ore.png");
        register("\uE517", "block_deepslate_emerald_ore", "Bloque Deepslate Emerald Ore", "Bloques", ":block_deepslate_emerald_ore:", "textures/font/icons/blocks/deepslate_emerald_ore.png");
        register("\uE518", "block_emerald_block", "Bloque Emerald Block", "Bloques", ":block_emerald_block:", "textures/font/icons/blocks/emerald_block.png");
        register("\uE519", "block_lapis_ore", "Bloque Lapis Ore", "Bloques", ":block_lapis_ore:", "textures/font/icons/blocks/lapis_ore.png");
        register("\uE51A", "block_deepslate_lapis_ore", "Bloque Deepslate Lapis Ore", "Bloques", ":block_deepslate_lapis_ore:", "textures/font/icons/blocks/deepslate_lapis_ore.png");
        register("\uE51B", "block_lapis_block", "Bloque Lapis Block", "Bloques", ":block_lapis_block:", "textures/font/icons/blocks/lapis_block.png");
        register("\uE51C", "block_diamond_ore", "Bloque Diamond Ore", "Bloques", ":block_diamond_ore:", "textures/font/icons/blocks/diamond_ore.png");
        register("\uE51D", "block_deepslate_diamond_ore", "Bloque Deepslate Diamond Ore", "Bloques", ":block_deepslate_diamond_ore:", "textures/font/icons/blocks/deepslate_diamond_ore.png");
        register("\uE51E", "block_diamond_block", "Bloque Diamond Block", "Bloques", ":block_diamond_block:", "textures/font/icons/blocks/diamond_block.png");
        register("\uE51F", "block_nether_gold_ore", "Bloque Nether Gold Ore", "Bloques", ":block_nether_gold_ore:", "textures/font/icons/blocks/nether_gold_ore.png");
        register("\uE520", "block_nether_quartz_ore", "Bloque Nether Quartz Ore", "Bloques", ":block_nether_quartz_ore:", "textures/font/icons/blocks/nether_quartz_ore.png");
        register("\uE521", "block_quartz_block", "Bloque Quartz Block", "Bloques", ":block_quartz_block:", "textures/font/icons/blocks/quartz_block.png");
        register("\uE522", "block_chiseled_quartz_block", "Bloque Chiseled Quartz Block", "Bloques", ":block_chiseled_quartz_block:", "textures/font/icons/blocks/chiseled_quartz_block.png");
        register("\uE523", "block_quartz_pillar", "Bloque Quartz Pillar", "Bloques", ":block_quartz_pillar:", "textures/font/icons/blocks/quartz_pillar.png");
        register("\uE524", "block_quartz_bricks", "Bloque Quartz Bricks", "Bloques", ":block_quartz_bricks:", "textures/font/icons/blocks/quartz_bricks.png");
        register("\uE525", "block_smooth_quartz", "Bloque Smooth Quartz", "Bloques", ":block_smooth_quartz:", "textures/font/icons/blocks/smooth_quartz.png");
        register("\uE526", "block_ancient_debris", "Bloque Ancient Debris", "Bloques", ":block_ancient_debris:", "textures/font/icons/blocks/ancient_debris.png");
        register("\uE527", "block_netherite_block", "Bloque Netherite Block", "Bloques", ":block_netherite_block:", "textures/font/icons/blocks/netherite_block.png");
        register("\uE528", "block_amethyst_block", "Bloque Amethyst Block", "Bloques", ":block_amethyst_block:", "textures/font/icons/blocks/amethyst_block.png");
        register("\uE529", "block_budding_amethyst", "Bloque Budding Amethyst", "Bloques", ":block_budding_amethyst:", "textures/font/icons/blocks/budding_amethyst.png");
        register("\uE52A", "block_amethyst_cluster", "Bloque Amethyst Cluster", "Bloques", ":block_amethyst_cluster:", "textures/font/icons/blocks/amethyst_cluster.png");
        register("\uE52B", "block_stone", "Bloque Stone", "Bloques", ":block_stone:", "textures/font/icons/blocks/stone.png");
        register("\uE52C", "block_smooth_stone", "Bloque Smooth Stone", "Bloques", ":block_smooth_stone:", "textures/font/icons/blocks/smooth_stone.png");
        register("\uE52D", "block_cobblestone", "Bloque Cobblestone", "Bloques", ":block_cobblestone:", "textures/font/icons/blocks/cobblestone.png");
        register("\uE52E", "block_mossy_cobblestone", "Bloque Mossy Cobblestone", "Bloques", ":block_mossy_cobblestone:", "textures/font/icons/blocks/mossy_cobblestone.png");
        register("\uE52F", "block_stone_bricks", "Bloque Stone Bricks", "Bloques", ":block_stone_bricks:", "textures/font/icons/blocks/stone_bricks.png");
        register("\uE530", "block_mossy_stone_bricks", "Bloque Mossy Stone Bricks", "Bloques", ":block_mossy_stone_bricks:", "textures/font/icons/blocks/mossy_stone_bricks.png");
        register("\uE531", "block_cracked_stone_bricks", "Bloque Cracked Stone Bricks", "Bloques", ":block_cracked_stone_bricks:", "textures/font/icons/blocks/cracked_stone_bricks.png");
        register("\uE532", "block_chiseled_stone_bricks", "Bloque Chiseled Stone Bricks", "Bloques", ":block_chiseled_stone_bricks:", "textures/font/icons/blocks/chiseled_stone_bricks.png");
        register("\uE533", "block_granite", "Bloque Granite", "Bloques", ":block_granite:", "textures/font/icons/blocks/granite.png");
        register("\uE534", "block_polished_granite", "Bloque Polished Granite", "Bloques", ":block_polished_granite:", "textures/font/icons/blocks/polished_granite.png");
        register("\uE535", "block_diorite", "Bloque Diorite", "Bloques", ":block_diorite:", "textures/font/icons/blocks/diorite.png");
        register("\uE536", "block_polished_diorite", "Bloque Polished Diorite", "Bloques", ":block_polished_diorite:", "textures/font/icons/blocks/polished_diorite.png");
        register("\uE537", "block_andesite", "Bloque Andesite", "Bloques", ":block_andesite:", "textures/font/icons/blocks/andesite.png");
        register("\uE538", "block_polished_andesite", "Bloque Polished Andesite", "Bloques", ":block_polished_andesite:", "textures/font/icons/blocks/polished_andesite.png");
        register("\uE539", "block_deepslate", "Bloque Deepslate", "Bloques", ":block_deepslate:", "textures/font/icons/blocks/deepslate.png");
        register("\uE53A", "block_cobbled_deepslate", "Bloque Cobbled Deepslate", "Bloques", ":block_cobbled_deepslate:", "textures/font/icons/blocks/cobbled_deepslate.png");
        register("\uE53B", "block_polished_deepslate", "Bloque Polished Deepslate", "Bloques", ":block_polished_deepslate:", "textures/font/icons/blocks/polished_deepslate.png");
        register("\uE53C", "block_deepslate_bricks", "Bloque Deepslate Bricks", "Bloques", ":block_deepslate_bricks:", "textures/font/icons/blocks/deepslate_bricks.png");
        register("\uE53D", "block_cracked_deepslate_bricks", "Bloque Cracked Deepslate Bricks", "Bloques", ":block_cracked_deepslate_bricks:", "textures/font/icons/blocks/cracked_deepslate_bricks.png");
        register("\uE53E", "block_deepslate_tiles", "Bloque Deepslate Tiles", "Bloques", ":block_deepslate_tiles:", "textures/font/icons/blocks/deepslate_tiles.png");
        register("\uE53F", "block_cracked_deepslate_tiles", "Bloque Cracked Deepslate Tiles", "Bloques", ":block_cracked_deepslate_tiles:", "textures/font/icons/blocks/cracked_deepslate_tiles.png");
        register("\uE540", "block_chiseled_deepslate", "Bloque Chiseled Deepslate", "Bloques", ":block_chiseled_deepslate:", "textures/font/icons/blocks/chiseled_deepslate.png");
        register("\uE541", "block_reinforced_deepslate", "Bloque Reinforced Deepslate", "Bloques", ":block_reinforced_deepslate:", "textures/font/icons/blocks/reinforced_deepslate.png");
        register("\uE542", "block_tuff", "Bloque Tuff", "Bloques", ":block_tuff:", "textures/font/icons/blocks/tuff.png");
        register("\uE543", "block_polished_tuff", "Bloque Polished Tuff", "Bloques", ":block_polished_tuff:", "textures/font/icons/blocks/polished_tuff.png");
        register("\uE544", "block_tuff_bricks", "Bloque Tuff Bricks", "Bloques", ":block_tuff_bricks:", "textures/font/icons/blocks/tuff_bricks.png");
        register("\uE545", "block_chiseled_tuff", "Bloque Chiseled Tuff", "Bloques", ":block_chiseled_tuff:", "textures/font/icons/blocks/chiseled_tuff.png");
        register("\uE546", "block_chiseled_tuff_bricks", "Bloque Chiseled Tuff Bricks", "Bloques", ":block_chiseled_tuff_bricks:", "textures/font/icons/blocks/chiseled_tuff_bricks.png");
        register("\uE547", "block_calcite", "Bloque Calcite", "Bloques", ":block_calcite:", "textures/font/icons/blocks/calcite.png");
        register("\uE548", "block_dripstone_block", "Bloque Dripstone Block", "Bloques", ":block_dripstone_block:", "textures/font/icons/blocks/dripstone_block.png");
        register("\uE549", "block_bedrock", "Bloque Bedrock", "Bloques", ":block_bedrock:", "textures/font/icons/blocks/bedrock.png");
        register("\uE54A", "block_dirt", "Bloque Dirt", "Bloques", ":block_dirt:", "textures/font/icons/blocks/dirt.png");
        register("\uE54B", "block_coarse_dirt", "Bloque Coarse Dirt", "Bloques", ":block_coarse_dirt:", "textures/font/icons/blocks/coarse_dirt.png");
        register("\uE54C", "block_rooted_dirt", "Bloque Rooted Dirt", "Bloques", ":block_rooted_dirt:", "textures/font/icons/blocks/rooted_dirt.png");
        register("\uE54D", "block_mud", "Bloque Mud", "Bloques", ":block_mud:", "textures/font/icons/blocks/mud.png");
        register("\uE54E", "block_packed_mud", "Bloque Packed Mud", "Bloques", ":block_packed_mud:", "textures/font/icons/blocks/packed_mud.png");
        register("\uE54F", "block_mud_bricks", "Bloque Mud Bricks", "Bloques", ":block_mud_bricks:", "textures/font/icons/blocks/mud_bricks.png");
        register("\uE550", "block_clay", "Bloque Clay", "Bloques", ":block_clay:", "textures/font/icons/blocks/clay.png");
        register("\uE551", "block_gravel", "Bloque Gravel", "Bloques", ":block_gravel:", "textures/font/icons/blocks/gravel.png");
        register("\uE552", "block_sand", "Bloque Sand", "Bloques", ":block_sand:", "textures/font/icons/blocks/sand.png");
        register("\uE553", "block_red_sand", "Bloque Red Sand", "Bloques", ":block_red_sand:", "textures/font/icons/blocks/red_sand.png");
        register("\uE554", "block_sandstone", "Bloque Sandstone", "Bloques", ":block_sandstone:", "textures/font/icons/blocks/sandstone.png");
        register("\uE555", "block_chiseled_sandstone", "Bloque Chiseled Sandstone", "Bloques", ":block_chiseled_sandstone:", "textures/font/icons/blocks/chiseled_sandstone.png");
        register("\uE556", "block_cut_sandstone", "Bloque Cut Sandstone", "Bloques", ":block_cut_sandstone:", "textures/font/icons/blocks/cut_sandstone.png");
        register("\uE557", "block_smooth_sandstone", "Bloque Smooth Sandstone", "Bloques", ":block_smooth_sandstone:", "textures/font/icons/blocks/smooth_sandstone.png");
        register("\uE558", "block_red_sandstone", "Bloque Red Sandstone", "Bloques", ":block_red_sandstone:", "textures/font/icons/blocks/red_sandstone.png");
        register("\uE559", "block_chiseled_red_sandstone", "Bloque Chiseled Red Sandstone", "Bloques", ":block_chiseled_red_sandstone:", "textures/font/icons/blocks/chiseled_red_sandstone.png");
        register("\uE55A", "block_cut_red_sandstone", "Bloque Cut Red Sandstone", "Bloques", ":block_cut_red_sandstone:", "textures/font/icons/blocks/cut_red_sandstone.png");
        register("\uE55B", "block_smooth_red_sandstone", "Bloque Smooth Red Sandstone", "Bloques", ":block_smooth_red_sandstone:", "textures/font/icons/blocks/smooth_red_sandstone.png");
        register("\uE55C", "block_grass_block", "Bloque Grass Block", "Bloques", ":block_grass_block:", "textures/font/icons/blocks/grass_block.png");
        register("\uE55D", "block_podzol", "Bloque Podzol", "Bloques", ":block_podzol:", "textures/font/icons/blocks/podzol.png");
        register("\uE55E", "block_mycelium", "Bloque Mycelium", "Bloques", ":block_mycelium:", "textures/font/icons/blocks/mycelium.png");
        register("\uE55F", "block_dirt_path", "Bloque Dirt Path", "Bloques", ":block_dirt_path:", "textures/font/icons/blocks/dirt_path.png");
        register("\uE560", "block_farmland", "Bloque Farmland", "Bloques", ":block_farmland:", "textures/font/icons/blocks/farmland.png");
        register("\uE561", "block_snow_block", "Bloque Snow Block", "Bloques", ":block_snow_block:", "textures/font/icons/blocks/snow_block.png");
        register("\uE562", "block_ice", "Bloque Ice", "Bloques", ":block_ice:", "textures/font/icons/blocks/ice.png");
        register("\uE563", "block_packed_ice", "Bloque Packed Ice", "Bloques", ":block_packed_ice:", "textures/font/icons/blocks/packed_ice.png");
        register("\uE564", "block_blue_ice", "Bloque Blue Ice", "Bloques", ":block_blue_ice:", "textures/font/icons/blocks/blue_ice.png");
        register("\uE565", "block_moss_block", "Bloque Moss Block", "Bloques", ":block_moss_block:", "textures/font/icons/blocks/moss_block.png");
        register("\uE566", "block_pale_moss_block", "Bloque Pale Moss Block", "Bloques", ":block_pale_moss_block:", "textures/font/icons/blocks/pale_moss_block.png");
        register("\uE567", "block_resin_block", "Bloque Resin Block", "Bloques", ":block_resin_block:", "textures/font/icons/blocks/resin_block.png");
        register("\uE568", "block_resin_bricks", "Bloque Resin Bricks", "Bloques", ":block_resin_bricks:", "textures/font/icons/blocks/resin_bricks.png");
        register("\uE569", "block_chiseled_resin_bricks", "Bloque Chiseled Resin Bricks", "Bloques", ":block_chiseled_resin_bricks:", "textures/font/icons/blocks/chiseled_resin_bricks.png");
        register("\uE56A", "block_obsidian", "Bloque Obsidian", "Bloques", ":block_obsidian:", "textures/font/icons/blocks/obsidian.png");
        register("\uE56B", "block_crying_obsidian", "Bloque Crying Obsidian", "Bloques", ":block_crying_obsidian:", "textures/font/icons/blocks/crying_obsidian.png");
        register("\uE56C", "block_oak_planks", "Bloque Oak Planks", "Bloques", ":block_oak_planks:", "textures/font/icons/blocks/oak_planks.png");
        register("\uE56D", "block_oak_log", "Bloque Oak Log", "Bloques", ":block_oak_log:", "textures/font/icons/blocks/oak_log.png");
        register("\uE56E", "block_stripped_oak_log", "Bloque Stripped Oak Log", "Bloques", ":block_stripped_oak_log:", "textures/font/icons/blocks/stripped_oak_log.png");
        register("\uE56F", "block_spruce_planks", "Bloque Spruce Planks", "Bloques", ":block_spruce_planks:", "textures/font/icons/blocks/spruce_planks.png");
        register("\uE570", "block_spruce_log", "Bloque Spruce Log", "Bloques", ":block_spruce_log:", "textures/font/icons/blocks/spruce_log.png");
        register("\uE571", "block_stripped_spruce_log", "Bloque Stripped Spruce Log", "Bloques", ":block_stripped_spruce_log:", "textures/font/icons/blocks/stripped_spruce_log.png");
        register("\uE572", "block_birch_planks", "Bloque Birch Planks", "Bloques", ":block_birch_planks:", "textures/font/icons/blocks/birch_planks.png");
        register("\uE573", "block_birch_log", "Bloque Birch Log", "Bloques", ":block_birch_log:", "textures/font/icons/blocks/birch_log.png");
        register("\uE574", "block_stripped_birch_log", "Bloque Stripped Birch Log", "Bloques", ":block_stripped_birch_log:", "textures/font/icons/blocks/stripped_birch_log.png");
        register("\uE575", "block_jungle_planks", "Bloque Jungle Planks", "Bloques", ":block_jungle_planks:", "textures/font/icons/blocks/jungle_planks.png");
        register("\uE576", "block_jungle_log", "Bloque Jungle Log", "Bloques", ":block_jungle_log:", "textures/font/icons/blocks/jungle_log.png");
        register("\uE577", "block_stripped_jungle_log", "Bloque Stripped Jungle Log", "Bloques", ":block_stripped_jungle_log:", "textures/font/icons/blocks/stripped_jungle_log.png");
        register("\uE578", "block_acacia_planks", "Bloque Acacia Planks", "Bloques", ":block_acacia_planks:", "textures/font/icons/blocks/acacia_planks.png");
        register("\uE579", "block_acacia_log", "Bloque Acacia Log", "Bloques", ":block_acacia_log:", "textures/font/icons/blocks/acacia_log.png");
        register("\uE57A", "block_stripped_acacia_log", "Bloque Stripped Acacia Log", "Bloques", ":block_stripped_acacia_log:", "textures/font/icons/blocks/stripped_acacia_log.png");
        register("\uE57B", "block_dark_oak_planks", "Bloque Dark Oak Planks", "Bloques", ":block_dark_oak_planks:", "textures/font/icons/blocks/dark_oak_planks.png");
        register("\uE57C", "block_dark_oak_log", "Bloque Dark Oak Log", "Bloques", ":block_dark_oak_log:", "textures/font/icons/blocks/dark_oak_log.png");
        register("\uE57D", "block_stripped_dark_oak_log", "Bloque Stripped Dark Oak Log", "Bloques", ":block_stripped_dark_oak_log:", "textures/font/icons/blocks/stripped_dark_oak_log.png");
        register("\uE57E", "block_mangrove_planks", "Bloque Mangrove Planks", "Bloques", ":block_mangrove_planks:", "textures/font/icons/blocks/mangrove_planks.png");
        register("\uE57F", "block_mangrove_log", "Bloque Mangrove Log", "Bloques", ":block_mangrove_log:", "textures/font/icons/blocks/mangrove_log.png");
        register("\uE580", "block_stripped_mangrove_log", "Bloque Stripped Mangrove Log", "Bloques", ":block_stripped_mangrove_log:", "textures/font/icons/blocks/stripped_mangrove_log.png");
        register("\uE581", "block_cherry_planks", "Bloque Cherry Planks", "Bloques", ":block_cherry_planks:", "textures/font/icons/blocks/cherry_planks.png");
        register("\uE582", "block_cherry_log", "Bloque Cherry Log", "Bloques", ":block_cherry_log:", "textures/font/icons/blocks/cherry_log.png");
        register("\uE583", "block_stripped_cherry_log", "Bloque Stripped Cherry Log", "Bloques", ":block_stripped_cherry_log:", "textures/font/icons/blocks/stripped_cherry_log.png");
        register("\uE584", "block_pale_oak_planks", "Bloque Pale Oak Planks", "Bloques", ":block_pale_oak_planks:", "textures/font/icons/blocks/pale_oak_planks.png");
        register("\uE585", "block_pale_oak_log", "Bloque Pale Oak Log", "Bloques", ":block_pale_oak_log:", "textures/font/icons/blocks/pale_oak_log.png");
        register("\uE586", "block_stripped_pale_oak_log", "Bloque Stripped Pale Oak Log", "Bloques", ":block_stripped_pale_oak_log:", "textures/font/icons/blocks/stripped_pale_oak_log.png");
        register("\uE587", "block_crimson_planks", "Bloque Crimson Planks", "Bloques", ":block_crimson_planks:", "textures/font/icons/blocks/crimson_planks.png");
        register("\uE588", "block_crimson_stem", "Bloque Crimson Stem", "Bloques", ":block_crimson_stem:", "textures/font/icons/blocks/crimson_stem.png");
        register("\uE589", "block_stripped_crimson_stem", "Bloque Stripped Crimson Stem", "Bloques", ":block_stripped_crimson_stem:", "textures/font/icons/blocks/stripped_crimson_stem.png");
        register("\uE58A", "block_warped_planks", "Bloque Warped Planks", "Bloques", ":block_warped_planks:", "textures/font/icons/blocks/warped_planks.png");
        register("\uE58B", "block_warped_stem", "Bloque Warped Stem", "Bloques", ":block_warped_stem:", "textures/font/icons/blocks/warped_stem.png");
        register("\uE58C", "block_stripped_warped_stem", "Bloque Stripped Warped Stem", "Bloques", ":block_stripped_warped_stem:", "textures/font/icons/blocks/stripped_warped_stem.png");
        register("\uE58D", "block_bamboo_planks", "Bloque Bamboo Planks", "Bloques", ":block_bamboo_planks:", "textures/font/icons/blocks/bamboo_planks.png");
        register("\uE58E", "block_bamboo_block", "Bloque Bamboo Block", "Bloques", ":block_bamboo_block:", "textures/font/icons/blocks/bamboo_block.png");
        register("\uE58F", "block_stripped_bamboo_block", "Bloque Stripped Bamboo Block", "Bloques", ":block_stripped_bamboo_block:", "textures/font/icons/blocks/stripped_bamboo_block.png");
        register("\uE590", "block_bamboo_mosaic", "Bloque Bamboo Mosaic", "Bloques", ":block_bamboo_mosaic:", "textures/font/icons/blocks/bamboo_mosaic.png");
        register("\uE591", "block_oak_leaves", "Bloque Oak Leaves", "Bloques", ":block_oak_leaves:", "textures/font/icons/blocks/oak_leaves.png");
        register("\uE592", "block_spruce_leaves", "Bloque Spruce Leaves", "Bloques", ":block_spruce_leaves:", "textures/font/icons/blocks/spruce_leaves.png");
        register("\uE593", "block_birch_leaves", "Bloque Birch Leaves", "Bloques", ":block_birch_leaves:", "textures/font/icons/blocks/birch_leaves.png");
        register("\uE594", "block_jungle_leaves", "Bloque Jungle Leaves", "Bloques", ":block_jungle_leaves:", "textures/font/icons/blocks/jungle_leaves.png");
        register("\uE595", "block_acacia_leaves", "Bloque Acacia Leaves", "Bloques", ":block_acacia_leaves:", "textures/font/icons/blocks/acacia_leaves.png");
        register("\uE596", "block_dark_oak_leaves", "Bloque Dark Oak Leaves", "Bloques", ":block_dark_oak_leaves:", "textures/font/icons/blocks/dark_oak_leaves.png");
        register("\uE597", "block_mangrove_leaves", "Bloque Mangrove Leaves", "Bloques", ":block_mangrove_leaves:", "textures/font/icons/blocks/mangrove_leaves.png");
        register("\uE598", "block_cherry_leaves", "Bloque Cherry Leaves", "Bloques", ":block_cherry_leaves:", "textures/font/icons/blocks/cherry_leaves.png");
        register("\uE599", "block_pale_oak_leaves", "Bloque Pale Oak Leaves", "Bloques", ":block_pale_oak_leaves:", "textures/font/icons/blocks/pale_oak_leaves.png");
        register("\uE59A", "block_azalea_leaves", "Bloque Azalea Leaves", "Bloques", ":block_azalea_leaves:", "textures/font/icons/blocks/azalea_leaves.png");
        register("\uE59B", "block_flowering_azalea_leaves", "Bloque Flowering Azalea Leaves", "Bloques", ":block_flowering_azalea_leaves:", "textures/font/icons/blocks/flowering_azalea_leaves.png");
        register("\uE59C", "block_cactus", "Bloque Cactus", "Bloques", ":block_cactus:", "textures/font/icons/blocks/cactus.png");
        register("\uE59D", "block_melon", "Bloque Melon", "Bloques", ":block_melon:", "textures/font/icons/blocks/melon.png");
        register("\uE59E", "block_pumpkin", "Bloque Pumpkin", "Bloques", ":block_pumpkin:", "textures/font/icons/blocks/pumpkin.png");
        register("\uE59F", "block_carved_pumpkin", "Bloque Carved Pumpkin", "Bloques", ":block_carved_pumpkin:", "textures/font/icons/blocks/carved_pumpkin.png");
        register("\uE5A0", "block_jack_o_lantern", "Bloque Jack O Lantern", "Bloques", ":block_jack_o_lantern:", "textures/font/icons/blocks/jack_o_lantern.png");
        register("\uE5A1", "block_hay_block", "Bloque Hay Block", "Bloques", ":block_hay_block:", "textures/font/icons/blocks/hay_block.png");
        register("\uE5A2", "block_sponge", "Bloque Sponge", "Bloques", ":block_sponge:", "textures/font/icons/blocks/sponge.png");
        register("\uE5A3", "block_wet_sponge", "Bloque Wet Sponge", "Bloques", ":block_wet_sponge:", "textures/font/icons/blocks/wet_sponge.png");
        register("\uE5A4", "block_honeycomb_block", "Bloque Honeycomb Block", "Bloques", ":block_honeycomb_block:", "textures/font/icons/blocks/honeycomb_block.png");
        register("\uE5A5", "block_honey_block", "Bloque Honey Block", "Bloques", ":block_honey_block:", "textures/font/icons/blocks/honey_block.png");
        register("\uE5A6", "block_slime_block", "Bloque Slime Block", "Bloques", ":block_slime_block:", "textures/font/icons/blocks/slime_block.png");
        register("\uE5A7", "block_netherrack", "Bloque Netherrack", "Bloques", ":block_netherrack:", "textures/font/icons/blocks/netherrack.png");
        register("\uE5A8", "block_crimson_nylium", "Bloque Crimson Nylium", "Bloques", ":block_crimson_nylium:", "textures/font/icons/blocks/crimson_nylium.png");
        register("\uE5A9", "block_warped_nylium", "Bloque Warped Nylium", "Bloques", ":block_warped_nylium:", "textures/font/icons/blocks/warped_nylium.png");
        register("\uE5AA", "block_nether_bricks", "Bloque Nether Bricks", "Bloques", ":block_nether_bricks:", "textures/font/icons/blocks/nether_bricks.png");
        register("\uE5AB", "block_red_nether_bricks", "Bloque Red Nether Bricks", "Bloques", ":block_red_nether_bricks:", "textures/font/icons/blocks/red_nether_bricks.png");
        register("\uE5AC", "block_cracked_nether_bricks", "Bloque Cracked Nether Bricks", "Bloques", ":block_cracked_nether_bricks:", "textures/font/icons/blocks/cracked_nether_bricks.png");
        register("\uE5AD", "block_chiseled_nether_bricks", "Bloque Chiseled Nether Bricks", "Bloques", ":block_chiseled_nether_bricks:", "textures/font/icons/blocks/chiseled_nether_bricks.png");
        register("\uE5AE", "block_soul_sand", "Bloque Soul Sand", "Bloques", ":block_soul_sand:", "textures/font/icons/blocks/soul_sand.png");
        register("\uE5AF", "block_soul_soil", "Bloque Soul Soil", "Bloques", ":block_soul_soil:", "textures/font/icons/blocks/soul_soil.png");
        register("\uE5B0", "block_basalt", "Bloque Basalt", "Bloques", ":block_basalt:", "textures/font/icons/blocks/basalt.png");
        register("\uE5B1", "block_polished_basalt", "Bloque Polished Basalt", "Bloques", ":block_polished_basalt:", "textures/font/icons/blocks/polished_basalt.png");
        register("\uE5B2", "block_smooth_basalt", "Bloque Smooth Basalt", "Bloques", ":block_smooth_basalt:", "textures/font/icons/blocks/smooth_basalt.png");
        register("\uE5B3", "block_blackstone", "Bloque Blackstone", "Bloques", ":block_blackstone:", "textures/font/icons/blocks/blackstone.png");
        register("\uE5B4", "block_polished_blackstone", "Bloque Polished Blackstone", "Bloques", ":block_polished_blackstone:", "textures/font/icons/blocks/polished_blackstone.png");
        register("\uE5B5", "block_chiseled_polished_blackstone", "Bloque Chiseled Polished Blackstone", "Bloques", ":block_chiseled_polished_blackstone:", "textures/font/icons/blocks/chiseled_polished_blackstone.png");
        register("\uE5B6", "block_polished_blackstone_bricks", "Bloque Polished Blackstone Bricks", "Bloques", ":block_polished_blackstone_bricks:", "textures/font/icons/blocks/polished_blackstone_bricks.png");
        register("\uE5B7", "block_cracked_polished_blackstone_bricks", "Bloque Cracked Polished Blackstone Bricks", "Bloques", ":block_cracked_polished_blackstone_bricks:", "textures/font/icons/blocks/cracked_polished_blackstone_bricks.png");
        register("\uE5B8", "block_gilded_blackstone", "Bloque Gilded Blackstone", "Bloques", ":block_gilded_blackstone:", "textures/font/icons/blocks/gilded_blackstone.png");
        register("\uE5B9", "block_glowstone", "Bloque Glowstone", "Bloques", ":block_glowstone:", "textures/font/icons/blocks/glowstone.png");
        register("\uE5BA", "block_shroomlight", "Bloque Shroomlight", "Bloques", ":block_shroomlight:", "textures/font/icons/blocks/shroomlight.png");
        register("\uE5BB", "block_magma_block", "Bloque Magma Block", "Bloques", ":block_magma_block:", "textures/font/icons/blocks/magma_block.png");
        register("\uE5BC", "block_end_stone", "Bloque End Stone", "Bloques", ":block_end_stone:", "textures/font/icons/blocks/end_stone.png");
        register("\uE5BD", "block_end_stone_bricks", "Bloque End Stone Bricks", "Bloques", ":block_end_stone_bricks:", "textures/font/icons/blocks/end_stone_bricks.png");
        register("\uE5BE", "block_purpur_block", "Bloque Purpur Block", "Bloques", ":block_purpur_block:", "textures/font/icons/blocks/purpur_block.png");
        register("\uE5BF", "block_purpur_pillar", "Bloque Purpur Pillar", "Bloques", ":block_purpur_pillar:", "textures/font/icons/blocks/purpur_pillar.png");
        register("\uE5C0", "block_white_wool", "Bloque White Wool", "Bloques", ":block_white_wool:", "textures/font/icons/blocks/white_wool.png");
        register("\uE5C1", "block_light_gray_wool", "Bloque Light Gray Wool", "Bloques", ":block_light_gray_wool:", "textures/font/icons/blocks/light_gray_wool.png");
        register("\uE5C2", "block_gray_wool", "Bloque Gray Wool", "Bloques", ":block_gray_wool:", "textures/font/icons/blocks/gray_wool.png");
        register("\uE5C3", "block_black_wool", "Bloque Black Wool", "Bloques", ":block_black_wool:", "textures/font/icons/blocks/black_wool.png");
        register("\uE5C4", "block_brown_wool", "Bloque Brown Wool", "Bloques", ":block_brown_wool:", "textures/font/icons/blocks/brown_wool.png");
        register("\uE5C5", "block_red_wool", "Bloque Red Wool", "Bloques", ":block_red_wool:", "textures/font/icons/blocks/red_wool.png");
        register("\uE5C6", "block_orange_wool", "Bloque Orange Wool", "Bloques", ":block_orange_wool:", "textures/font/icons/blocks/orange_wool.png");
        register("\uE5C7", "block_yellow_wool", "Bloque Yellow Wool", "Bloques", ":block_yellow_wool:", "textures/font/icons/blocks/yellow_wool.png");
        register("\uE5C8", "block_lime_wool", "Bloque Lime Wool", "Bloques", ":block_lime_wool:", "textures/font/icons/blocks/lime_wool.png");
        register("\uE5C9", "block_green_wool", "Bloque Green Wool", "Bloques", ":block_green_wool:", "textures/font/icons/blocks/green_wool.png");
        register("\uE5CA", "block_cyan_wool", "Bloque Cyan Wool", "Bloques", ":block_cyan_wool:", "textures/font/icons/blocks/cyan_wool.png");
        register("\uE5CB", "block_light_blue_wool", "Bloque Light Blue Wool", "Bloques", ":block_light_blue_wool:", "textures/font/icons/blocks/light_blue_wool.png");
        register("\uE5CC", "block_blue_wool", "Bloque Blue Wool", "Bloques", ":block_blue_wool:", "textures/font/icons/blocks/blue_wool.png");
        register("\uE5CD", "block_purple_wool", "Bloque Purple Wool", "Bloques", ":block_purple_wool:", "textures/font/icons/blocks/purple_wool.png");
        register("\uE5CE", "block_magenta_wool", "Bloque Magenta Wool", "Bloques", ":block_magenta_wool:", "textures/font/icons/blocks/magenta_wool.png");
        register("\uE5CF", "block_pink_wool", "Bloque Pink Wool", "Bloques", ":block_pink_wool:", "textures/font/icons/blocks/pink_wool.png");
        register("\uE5D0", "block_white_concrete", "Bloque White Concrete", "Bloques", ":block_white_concrete:", "textures/font/icons/blocks/white_concrete.png");
        register("\uE5D1", "block_light_gray_concrete", "Bloque Light Gray Concrete", "Bloques", ":block_light_gray_concrete:", "textures/font/icons/blocks/light_gray_concrete.png");
        register("\uE5D2", "block_gray_concrete", "Bloque Gray Concrete", "Bloques", ":block_gray_concrete:", "textures/font/icons/blocks/gray_concrete.png");
        register("\uE5D3", "block_black_concrete", "Bloque Black Concrete", "Bloques", ":block_black_concrete:", "textures/font/icons/blocks/black_concrete.png");
        register("\uE5D4", "block_brown_concrete", "Bloque Brown Concrete", "Bloques", ":block_brown_concrete:", "textures/font/icons/blocks/brown_concrete.png");
        register("\uE5D5", "block_red_concrete", "Bloque Red Concrete", "Bloques", ":block_red_concrete:", "textures/font/icons/blocks/red_concrete.png");
        register("\uE5D6", "block_orange_concrete", "Bloque Orange Concrete", "Bloques", ":block_orange_concrete:", "textures/font/icons/blocks/orange_concrete.png");
        register("\uE5D7", "block_yellow_concrete", "Bloque Yellow Concrete", "Bloques", ":block_yellow_concrete:", "textures/font/icons/blocks/yellow_concrete.png");
        register("\uE5D8", "block_lime_concrete", "Bloque Lime Concrete", "Bloques", ":block_lime_concrete:", "textures/font/icons/blocks/lime_concrete.png");
        register("\uE5D9", "block_green_concrete", "Bloque Green Concrete", "Bloques", ":block_green_concrete:", "textures/font/icons/blocks/green_concrete.png");
        register("\uE5DA", "block_cyan_concrete", "Bloque Cyan Concrete", "Bloques", ":block_cyan_concrete:", "textures/font/icons/blocks/cyan_concrete.png");
        register("\uE5DB", "block_light_blue_concrete", "Bloque Light Blue Concrete", "Bloques", ":block_light_blue_concrete:", "textures/font/icons/blocks/light_blue_concrete.png");
        register("\uE5DC", "block_blue_concrete", "Bloque Blue Concrete", "Bloques", ":block_blue_concrete:", "textures/font/icons/blocks/blue_concrete.png");
        register("\uE5DD", "block_purple_concrete", "Bloque Purple Concrete", "Bloques", ":block_purple_concrete:", "textures/font/icons/blocks/purple_concrete.png");
        register("\uE5DE", "block_magenta_concrete", "Bloque Magenta Concrete", "Bloques", ":block_magenta_concrete:", "textures/font/icons/blocks/magenta_concrete.png");
        register("\uE5DF", "block_pink_concrete", "Bloque Pink Concrete", "Bloques", ":block_pink_concrete:", "textures/font/icons/blocks/pink_concrete.png");
        register("\uE5E0", "block_terracotta", "Bloque Terracotta", "Bloques", ":block_terracotta:", "textures/font/icons/blocks/terracotta.png");
        register("\uE5E1", "block_white_terracotta", "Bloque White Terracotta", "Bloques", ":block_white_terracotta:", "textures/font/icons/blocks/white_terracotta.png");
        register("\uE5E2", "block_light_gray_terracotta", "Bloque Light Gray Terracotta", "Bloques", ":block_light_gray_terracotta:", "textures/font/icons/blocks/light_gray_terracotta.png");
        register("\uE5E3", "block_gray_terracotta", "Bloque Gray Terracotta", "Bloques", ":block_gray_terracotta:", "textures/font/icons/blocks/gray_terracotta.png");
        register("\uE5E4", "block_black_terracotta", "Bloque Black Terracotta", "Bloques", ":block_black_terracotta:", "textures/font/icons/blocks/black_terracotta.png");
        register("\uE5E5", "block_brown_terracotta", "Bloque Brown Terracotta", "Bloques", ":block_brown_terracotta:", "textures/font/icons/blocks/brown_terracotta.png");
        register("\uE5E6", "block_red_terracotta", "Bloque Red Terracotta", "Bloques", ":block_red_terracotta:", "textures/font/icons/blocks/red_terracotta.png");
        register("\uE5E7", "block_orange_terracotta", "Bloque Orange Terracotta", "Bloques", ":block_orange_terracotta:", "textures/font/icons/blocks/orange_terracotta.png");
        register("\uE5E8", "block_yellow_terracotta", "Bloque Yellow Terracotta", "Bloques", ":block_yellow_terracotta:", "textures/font/icons/blocks/yellow_terracotta.png");
        register("\uE5E9", "block_lime_terracotta", "Bloque Lime Terracotta", "Bloques", ":block_lime_terracotta:", "textures/font/icons/blocks/lime_terracotta.png");
        register("\uE5EA", "block_green_terracotta", "Bloque Green Terracotta", "Bloques", ":block_green_terracotta:", "textures/font/icons/blocks/green_terracotta.png");
        register("\uE5EB", "block_cyan_terracotta", "Bloque Cyan Terracotta", "Bloques", ":block_cyan_terracotta:", "textures/font/icons/blocks/cyan_terracotta.png");
        register("\uE5EC", "block_light_blue_terracotta", "Bloque Light Blue Terracotta", "Bloques", ":block_light_blue_terracotta:", "textures/font/icons/blocks/light_blue_terracotta.png");
        register("\uE5ED", "block_blue_terracotta", "Bloque Blue Terracotta", "Bloques", ":block_blue_terracotta:", "textures/font/icons/blocks/blue_terracotta.png");
        register("\uE5EE", "block_purple_terracotta", "Bloque Purple Terracotta", "Bloques", ":block_purple_terracotta:", "textures/font/icons/blocks/purple_terracotta.png");
        register("\uE5EF", "block_magenta_terracotta", "Bloque Magenta Terracotta", "Bloques", ":block_magenta_terracotta:", "textures/font/icons/blocks/magenta_terracotta.png");
        register("\uE5F0", "block_pink_terracotta", "Bloque Pink Terracotta", "Bloques", ":block_pink_terracotta:", "textures/font/icons/blocks/pink_terracotta.png");
        register("\uE5F1", "block_white_glazed_terracotta", "Bloque White Glazed Terracotta", "Bloques", ":block_white_glazed_terracotta:", "textures/font/icons/blocks/white_glazed_terracotta.png");
        register("\uE5F2", "block_light_gray_glazed_terracotta", "Bloque Light Gray Glazed Terracotta", "Bloques", ":block_light_gray_glazed_terracotta:", "textures/font/icons/blocks/light_gray_glazed_terracotta.png");
        register("\uE5F3", "block_gray_glazed_terracotta", "Bloque Gray Glazed Terracotta", "Bloques", ":block_gray_glazed_terracotta:", "textures/font/icons/blocks/gray_glazed_terracotta.png");
        register("\uE5F4", "block_black_glazed_terracotta", "Bloque Black Glazed Terracotta", "Bloques", ":block_black_glazed_terracotta:", "textures/font/icons/blocks/black_glazed_terracotta.png");
        register("\uE5F5", "block_brown_glazed_terracotta", "Bloque Brown Glazed Terracotta", "Bloques", ":block_brown_glazed_terracotta:", "textures/font/icons/blocks/brown_glazed_terracotta.png");
        register("\uE5F6", "block_red_glazed_terracotta", "Bloque Red Glazed Terracotta", "Bloques", ":block_red_glazed_terracotta:", "textures/font/icons/blocks/red_glazed_terracotta.png");
        register("\uE5F7", "block_orange_glazed_terracotta", "Bloque Orange Glazed Terracotta", "Bloques", ":block_orange_glazed_terracotta:", "textures/font/icons/blocks/orange_glazed_terracotta.png");
        register("\uE5F8", "block_yellow_glazed_terracotta", "Bloque Yellow Glazed Terracotta", "Bloques", ":block_yellow_glazed_terracotta:", "textures/font/icons/blocks/yellow_glazed_terracotta.png");
        register("\uE5F9", "block_lime_glazed_terracotta", "Bloque Lime Glazed Terracotta", "Bloques", ":block_lime_glazed_terracotta:", "textures/font/icons/blocks/lime_glazed_terracotta.png");
        register("\uE5FA", "block_green_glazed_terracotta", "Bloque Green Glazed Terracotta", "Bloques", ":block_green_glazed_terracotta:", "textures/font/icons/blocks/green_glazed_terracotta.png");
        register("\uE5FB", "block_cyan_glazed_terracotta", "Bloque Cyan Glazed Terracotta", "Bloques", ":block_cyan_glazed_terracotta:", "textures/font/icons/blocks/cyan_glazed_terracotta.png");
        register("\uE5FC", "block_light_blue_glazed_terracotta", "Bloque Light Blue Glazed Terracotta", "Bloques", ":block_light_blue_glazed_terracotta:", "textures/font/icons/blocks/light_blue_glazed_terracotta.png");
        register("\uE5FD", "block_blue_glazed_terracotta", "Bloque Blue Glazed Terracotta", "Bloques", ":block_blue_glazed_terracotta:", "textures/font/icons/blocks/blue_glazed_terracotta.png");
        register("\uE5FE", "block_purple_glazed_terracotta", "Bloque Purple Glazed Terracotta", "Bloques", ":block_purple_glazed_terracotta:", "textures/font/icons/blocks/purple_glazed_terracotta.png");
        register("\uE5FF", "block_magenta_glazed_terracotta", "Bloque Magenta Glazed Terracotta", "Bloques", ":block_magenta_glazed_terracotta:", "textures/font/icons/blocks/magenta_glazed_terracotta.png");
        register("\uE600", "block_pink_glazed_terracotta", "Bloque Pink Glazed Terracotta", "Bloques", ":block_pink_glazed_terracotta:", "textures/font/icons/blocks/pink_glazed_terracotta.png");
        register("\uE601", "block_glass", "Bloque Glass", "Bloques", ":block_glass:", "textures/font/icons/blocks/glass.png");
        register("\uE602", "block_tinted_glass", "Bloque Tinted Glass", "Bloques", ":block_tinted_glass:", "textures/font/icons/blocks/tinted_glass.png");
        register("\uE603", "block_white_stained_glass", "Bloque White Stained Glass", "Bloques", ":block_white_stained_glass:", "textures/font/icons/blocks/white_stained_glass.png");
        register("\uE604", "block_light_gray_stained_glass", "Bloque Light Gray Stained Glass", "Bloques", ":block_light_gray_stained_glass:", "textures/font/icons/blocks/light_gray_stained_glass.png");
        register("\uE605", "block_gray_stained_glass", "Bloque Gray Stained Glass", "Bloques", ":block_gray_stained_glass:", "textures/font/icons/blocks/gray_stained_glass.png");
        register("\uE606", "block_black_stained_glass", "Bloque Black Stained Glass", "Bloques", ":block_black_stained_glass:", "textures/font/icons/blocks/black_stained_glass.png");
        register("\uE607", "block_brown_stained_glass", "Bloque Brown Stained Glass", "Bloques", ":block_brown_stained_glass:", "textures/font/icons/blocks/brown_stained_glass.png");
        register("\uE608", "block_red_stained_glass", "Bloque Red Stained Glass", "Bloques", ":block_red_stained_glass:", "textures/font/icons/blocks/red_stained_glass.png");
        register("\uE609", "block_orange_stained_glass", "Bloque Orange Stained Glass", "Bloques", ":block_orange_stained_glass:", "textures/font/icons/blocks/orange_stained_glass.png");
        register("\uE60A", "block_yellow_stained_glass", "Bloque Yellow Stained Glass", "Bloques", ":block_yellow_stained_glass:", "textures/font/icons/blocks/yellow_stained_glass.png");
        register("\uE60B", "block_lime_stained_glass", "Bloque Lime Stained Glass", "Bloques", ":block_lime_stained_glass:", "textures/font/icons/blocks/lime_stained_glass.png");
        register("\uE60C", "block_green_stained_glass", "Bloque Green Stained Glass", "Bloques", ":block_green_stained_glass:", "textures/font/icons/blocks/green_stained_glass.png");
        register("\uE60D", "block_cyan_stained_glass", "Bloque Cyan Stained Glass", "Bloques", ":block_cyan_stained_glass:", "textures/font/icons/blocks/cyan_stained_glass.png");
        register("\uE60E", "block_light_blue_stained_glass", "Bloque Light Blue Stained Glass", "Bloques", ":block_light_blue_stained_glass:", "textures/font/icons/blocks/light_blue_stained_glass.png");
        register("\uE60F", "block_blue_stained_glass", "Bloque Blue Stained Glass", "Bloques", ":block_blue_stained_glass:", "textures/font/icons/blocks/blue_stained_glass.png");
        register("\uE610", "block_purple_stained_glass", "Bloque Purple Stained Glass", "Bloques", ":block_purple_stained_glass:", "textures/font/icons/blocks/purple_stained_glass.png");
        register("\uE611", "block_magenta_stained_glass", "Bloque Magenta Stained Glass", "Bloques", ":block_magenta_stained_glass:", "textures/font/icons/blocks/magenta_stained_glass.png");
        register("\uE612", "block_pink_stained_glass", "Bloque Pink Stained Glass", "Bloques", ":block_pink_stained_glass:", "textures/font/icons/blocks/pink_stained_glass.png");
        register("\uE613", "block_crafting_table", "Bloque Crafting Table", "Bloques", ":block_crafting_table:", "textures/font/icons/blocks/crafting_table.png");
        register("\uE614", "block_furnace", "Bloque Furnace", "Bloques", ":block_furnace:", "textures/font/icons/blocks/furnace.png");
        register("\uE615", "block_furnace_on", "Bloque Furnace On", "Bloques", ":block_furnace_on:", "textures/font/icons/blocks/furnace_on.png");
        register("\uE616", "block_blast_furnace", "Bloque Blast Furnace", "Bloques", ":block_blast_furnace:", "textures/font/icons/blocks/blast_furnace.png");
        register("\uE617", "block_smoker", "Bloque Smoker", "Bloques", ":block_smoker:", "textures/font/icons/blocks/smoker.png");
        register("\uE618", "block_dispenser", "Bloque Dispenser", "Bloques", ":block_dispenser:", "textures/font/icons/blocks/dispenser.png");
        register("\uE619", "block_dropper", "Bloque Dropper", "Bloques", ":block_dropper:", "textures/font/icons/blocks/dropper.png");
        register("\uE61A", "block_observer", "Bloque Observer", "Bloques", ":block_observer:", "textures/font/icons/blocks/observer.png");
        register("\uE61B", "block_piston", "Bloque Piston", "Bloques", ":block_piston:", "textures/font/icons/blocks/piston.png");
        register("\uE61C", "block_sticky_piston", "Bloque Sticky Piston", "Bloques", ":block_sticky_piston:", "textures/font/icons/blocks/sticky_piston.png");
        register("\uE61D", "block_tnt", "Bloque Tnt", "Bloques", ":block_tnt:", "textures/font/icons/blocks/tnt.png");
        register("\uE61E", "block_enchanting_table", "Bloque Enchanting Table", "Bloques", ":block_enchanting_table:", "textures/font/icons/blocks/enchanting_table.png");
        register("\uE61F", "block_bookshelf", "Bloque Bookshelf", "Bloques", ":block_bookshelf:", "textures/font/icons/blocks/bookshelf.png");
        register("\uE620", "block_chiseled_bookshelf", "Bloque Chiseled Bookshelf", "Bloques", ":block_chiseled_bookshelf:", "textures/font/icons/blocks/chiseled_bookshelf.png");
        register("\uE621", "block_lectern", "Bloque Lectern", "Bloques", ":block_lectern:", "textures/font/icons/blocks/lectern.png");
        register("\uE622", "block_smithing_table", "Bloque Smithing Table", "Bloques", ":block_smithing_table:", "textures/font/icons/blocks/smithing_table.png");
        register("\uE623", "block_fletching_table", "Bloque Fletching Table", "Bloques", ":block_fletching_table:", "textures/font/icons/blocks/fletching_table.png");
        register("\uE624", "block_cartography_table", "Bloque Cartography Table", "Bloques", ":block_cartography_table:", "textures/font/icons/blocks/cartography_table.png");
        register("\uE625", "block_loom", "Bloque Loom", "Bloques", ":block_loom:", "textures/font/icons/blocks/loom.png");
        register("\uE626", "block_stonecutter", "Bloque Stonecutter", "Bloques", ":block_stonecutter:", "textures/font/icons/blocks/stonecutter.png");
        register("\uE627", "block_composter", "Bloque Composter", "Bloques", ":block_composter:", "textures/font/icons/blocks/composter.png");
        register("\uE628", "block_barrel", "Bloque Barrel", "Bloques", ":block_barrel:", "textures/font/icons/blocks/barrel.png");
        register("\uE629", "block_chest", "Bloque Chest", "Bloques", ":block_chest:", "textures/font/icons/blocks/chest.png");
        register("\uE62A", "block_ender_chest", "Bloque Ender Chest", "Bloques", ":block_ender_chest:", "textures/font/icons/blocks/ender_chest.png");
        register("\uE62B", "block_beacon", "Bloque Beacon", "Bloques", ":block_beacon:", "textures/font/icons/blocks/beacon.png");
        register("\uE62C", "block_conduit", "Bloque Conduit", "Bloques", ":block_conduit:", "textures/font/icons/blocks/conduit.png");
        register("\uE62D", "block_respawn_anchor", "Bloque Respawn Anchor", "Bloques", ":block_respawn_anchor:", "textures/font/icons/blocks/respawn_anchor.png");
        register("\uE62E", "block_lodestone", "Bloque Lodestone", "Bloques", ":block_lodestone:", "textures/font/icons/blocks/lodestone.png");
        register("\uE62F", "block_target", "Bloque Target", "Bloques", ":block_target:", "textures/font/icons/blocks/target.png");
        register("\uE630", "block_redstone_lamp", "Bloque Redstone Lamp", "Bloques", ":block_redstone_lamp:", "textures/font/icons/blocks/redstone_lamp.png");
        register("\uE631", "block_redstone_lamp_on", "Bloque Redstone Lamp On", "Bloques", ":block_redstone_lamp_on:", "textures/font/icons/blocks/redstone_lamp_on.png");
        register("\uE632", "block_sea_lantern", "Bloque Sea Lantern", "Bloques", ":block_sea_lantern:", "textures/font/icons/blocks/sea_lantern.png");
        register("\uE633", "block_lantern", "Bloque Lantern", "Bloques", ":block_lantern:", "textures/font/icons/blocks/lantern.png");
        register("\uE634", "block_soul_lantern", "Bloque Soul Lantern", "Bloques", ":block_soul_lantern:", "textures/font/icons/blocks/soul_lantern.png");
        register("\uE635", "block_bell", "Bloque Bell", "Bloques", ":block_bell:", "textures/font/icons/blocks/bell.png");
        register("\uE636", "block_brewing_stand", "Bloque Brewing Stand", "Bloques", ":block_brewing_stand:", "textures/font/icons/blocks/brewing_stand.png");
        register("\uE637", "block_cauldron", "Bloque Cauldron", "Bloques", ":block_cauldron:", "textures/font/icons/blocks/cauldron.png");
        register("\uE638", "block_hopper", "Bloque Hopper", "Bloques", ":block_hopper:", "textures/font/icons/blocks/hopper.png");
        register("\uE639", "block_shulker_box", "Bloque Shulker Box", "Bloques", ":block_shulker_box:", "textures/font/icons/blocks/shulker_box.png");
        register("\uE63A", "block_sculk", "Bloque Sculk", "Bloques", ":block_sculk:", "textures/font/icons/blocks/sculk.png");
        register("\uE63B", "block_sculk_catalyst", "Bloque Sculk Catalyst", "Bloques", ":block_sculk_catalyst:", "textures/font/icons/blocks/sculk_catalyst.png");
        register("\uE63C", "block_sculk_shrieker", "Bloque Sculk Shrieker", "Bloques", ":block_sculk_shrieker:", "textures/font/icons/blocks/sculk_shrieker.png");
        register("\uE63D", "block_sculk_sensor", "Bloque Sculk Sensor", "Bloques", ":block_sculk_sensor:", "textures/font/icons/blocks/sculk_sensor.png");
        register("\uE63E", "block_ochre_froglight", "Bloque Ochre Froglight", "Bloques", ":block_ochre_froglight:", "textures/font/icons/blocks/ochre_froglight.png");
        register("\uE63F", "block_pearlescent_froglight", "Bloque Pearlescent Froglight", "Bloques", ":block_pearlescent_froglight:", "textures/font/icons/blocks/pearlescent_froglight.png");
        register("\uE640", "block_verdant_froglight", "Bloque Verdant Froglight", "Bloques", ":block_verdant_froglight:", "textures/font/icons/blocks/verdant_froglight.png");
        register("\uE641", "block_prismarine", "Bloque Prismarine", "Bloques", ":block_prismarine:", "textures/font/icons/blocks/prismarine.png");
        register("\uE642", "block_prismarine_bricks", "Bloque Prismarine Bricks", "Bloques", ":block_prismarine_bricks:", "textures/font/icons/blocks/prismarine_bricks.png");
        register("\uE643", "block_dark_prismarine", "Bloque Dark Prismarine", "Bloques", ":block_dark_prismarine:", "textures/font/icons/blocks/dark_prismarine.png");
        register("\uE644", "block_chain", "Bloque Chain", "Bloques", ":block_chain:", "textures/font/icons/blocks/chain.png");
        register("\uE645", "block_iron_bars", "Bloque Iron Bars", "Bloques", ":block_iron_bars:", "textures/font/icons/blocks/iron_bars.png");
        register("\uE646", "block_end_rod", "Bloque End Rod", "Bloques", ":block_end_rod:", "textures/font/icons/blocks/end_rod.png");
        register("\uE647", "block_repeater", "Bloque Repeater", "Bloques", ":block_repeater:", "textures/font/icons/blocks/repeater.png");
        register("\uE648", "block_comparator", "Bloque Comparator", "Bloques", ":block_comparator:", "textures/font/icons/blocks/comparator.png");
        register("\uE649", "block_redstone_dust", "Bloque Redstone Dust", "Bloques", ":block_redstone_dust:", "textures/font/icons/blocks/redstone_dust.png");
        register("\uE64A", "block_rail", "Bloque Rail", "Bloques", ":block_rail:", "textures/font/icons/blocks/rail.png");
        register("\uE64B", "block_powered_rail", "Bloque Powered Rail", "Bloques", ":block_powered_rail:", "textures/font/icons/blocks/powered_rail.png");
        register("\uE64C", "block_detector_rail", "Bloque Detector Rail", "Bloques", ":block_detector_rail:", "textures/font/icons/blocks/detector_rail.png");
        register("\uE64D", "block_activator_rail", "Bloque Activator Rail", "Bloques", ":block_activator_rail:", "textures/font/icons/blocks/activator_rail.png");
        register("\uE64E", "block_copper_bulb", "Bloque Copper Bulb", "Bloques", ":block_copper_bulb:", "textures/font/icons/blocks/copper_bulb.png");
        register("\uE64F", "block_copper_grate", "Bloque Copper Grate", "Bloques", ":block_copper_grate:", "textures/font/icons/blocks/copper_grate.png");
        register("\uE650", "block_chiseled_copper", "Bloque Chiseled Copper", "Bloques", ":block_chiseled_copper:", "textures/font/icons/blocks/chiseled_copper.png");
        register("\uE651", "block_crafter", "Bloque Crafter", "Bloques", ":block_crafter:", "textures/font/icons/blocks/crafter.png");
        register("\uE652", "block_vault", "Bloque Vault", "Bloques", ":block_vault:", "textures/font/icons/blocks/vault.png");
        register("\uE653", "block_heavy_core", "Bloque Heavy Core", "Bloques", ":block_heavy_core:", "textures/font/icons/blocks/heavy_core.png");
    }

    private static void initLetters() {
        register("\uE350", "letter_a", "Letra A", "Letras", ":letra_a:", "textures/font/letters/letter_a.png");
        register("\uE351", "letter_b", "Letra B", "Letras", ":letra_b:", "textures/font/letters/letter_b.png");
        register("\uE352", "letter_c", "Letra C", "Letras", ":letra_c:", "textures/font/letters/letter_c.png");
        register("\uE353", "letter_d", "Letra D", "Letras", ":letra_d:", "textures/font/letters/letter_d.png");
        register("\uE354", "letter_e", "Letra E", "Letras", ":letra_e:", "textures/font/letters/letter_e.png");
        register("\uE355", "letter_f", "Letra F", "Letras", ":letra_f:", "textures/font/letters/letter_f.png");
        register("\uE356", "letter_g", "Letra G", "Letras", ":letra_g:", "textures/font/letters/letter_g.png");
        register("\uE357", "letter_h", "Letra H", "Letras", ":letra_h:", "textures/font/letters/letter_h.png");
        register("\uE358", "letter_i", "Letra I", "Letras", ":letra_i:", "textures/font/letters/letter_i.png");
        register("\uE359", "letter_j", "Letra J", "Letras", ":letra_j:", "textures/font/letters/letter_j.png");
        register("\uE35A", "letter_k", "Letra K", "Letras", ":letra_k:", "textures/font/letters/letter_k.png");
        register("\uE35B", "letter_l", "Letra L", "Letras", ":letra_l:", "textures/font/letters/letter_l.png");
        register("\uE35C", "letter_m", "Letra M", "Letras", ":letra_m:", "textures/font/letters/letter_m.png");
        register("\uE35D", "letter_n", "Letra N", "Letras", ":letra_n:", "textures/font/letters/letter_n.png");
        register("\uE35E", "letter_o", "Letra O", "Letras", ":letra_o:", "textures/font/letters/letter_o.png");
        register("\uE35F", "letter_p", "Letra P", "Letras", ":letra_p:", "textures/font/letters/letter_p.png");
        register("\uE360", "letter_q", "Letra Q", "Letras", ":letra_q:", "textures/font/letters/letter_q.png");
        register("\uE361", "letter_r", "Letra R", "Letras", ":letra_r:", "textures/font/letters/letter_r.png");
        register("\uE362", "letter_s", "Letra S", "Letras", ":letra_s:", "textures/font/letters/letter_s.png");
        register("\uE363", "letter_t", "Letra T", "Letras", ":letra_t:", "textures/font/letters/letter_t.png");
        register("\uE364", "letter_u", "Letra U", "Letras", ":letra_u:", "textures/font/letters/letter_u.png");
        register("\uE365", "letter_v", "Letra V", "Letras", ":letra_v:", "textures/font/letters/letter_v.png");
        register("\uE366", "letter_w", "Letra W", "Letras", ":letra_w:", "textures/font/letters/letter_w.png");
        register("\uE367", "letter_x", "Letra X", "Letras", ":letra_x:", "textures/font/letters/letter_x.png");
        register("\uE368", "letter_y", "Letra Y", "Letras", ":letra_y:", "textures/font/letters/letter_y.png");
        register("\uE369", "letter_z", "Letra Z", "Letras", ":letra_z:", "textures/font/letters/letter_z.png");
    }

    private static void initAliases() {
        alias(":ar:", "flag_ar");
        alias(":argentina:", "flag_ar");
        alias(":arg:", "flag_ar");
        alias(":br:", "flag_br");
        alias(":brasil:", "flag_br");
        alias(":us:", "flag_us");
        alias(":usa:", "flag_us");
        alias(":es:", "flag_es");
        alias(":espana:", "flag_es");
        alias(":esp:", "flag_es");
        alias(":owner:", "badge_owner");
        alias(":dueno:", "badge_owner");
        alias(":rango_owner:", "badge_owner");
        alias(":coowner:", "badge_coowner");
        alias(":rango_coowner:", "badge_coowner");
        alias(":manager:", "badge_manager");
        alias(":rango_manager:", "badge_manager");
        alias(":senioradmin:", "badge_senioradmin");
        alias(":sr_admin:", "badge_senioradmin");
        alias(":rango_senioradmin:", "badge_senioradmin");
        alias(":admin:", "badge_admin");
        alias(":administrador:", "badge_admin");
        alias(":rango_admin:", "badge_admin");
        alias(":modplus:", "badge_modplus");
        alias(":mod_plus:", "badge_modplus");
        alias(":rango_modplus:", "badge_modplus");
        alias(":mod:", "badge_mod");
        alias(":moderador:", "badge_mod");
        alias(":rango_mod:", "badge_mod");
        alias(":ayudante:", "badge_ayudante");
        alias(":helper:", "badge_ayudante");
        alias(":rango_ayudante:", "badge_ayudante");
        alias(":famoso:", "badge_famoso");
        alias(":famous:", "badge_famoso");
        alias(":rango_famoso:", "badge_famoso");
        alias(":media:", "badge_media");
        alias(":creator:", "badge_media");
        alias(":rango_media:", "badge_media");
        alias(":developer:", "badge_developer");
        alias(":dev:", "badge_developer");
        alias(":rango_developer:", "badge_developer");
        alias(":guemes:", "badge_guemes");
        alias(":rango_guemes:", "badge_guemes");
        alias(":sarmiento:", "badge_sarmiento");
        alias(":rango_sarmiento:", "badge_sarmiento");
        alias(":belgrano:", "badge_belgrano");
        alias(":rango_belgrano:", "badge_belgrano");
        alias(":sanmartin:", "badge_sanmartin");
        alias(":rango_sanmartin:", "badge_sanmartin");
        alias(":betatester:", "badge_betatester");
        alias(":beta:", "badge_betatester");
        alias(":rango_betatester:", "badge_betatester");
        alias(":usuario:", "badge_usuario");
        alias(":user:", "badge_usuario");
        alias(":rango_usuario:", "badge_usuario");
        alias(":rango_user:", "badge_usuario");
        alias(":mate:", "badge_mate");
        alias(":rango_mate:", "badge_mate");
        alias(":matero:", "badge_matero");
        alias(":rango_matero:", "badge_matero");
        alias(":discord:", "social_discord");
        alias(":youtube:", "social_youtube");
        alias(":twitch:", "social_twitch");
        alias(":instagram:", "social_instagram");
        alias(":twitter:", "social_twitter");
        alias(":tiktok:", "social_tiktok");
        alias(":fuerza:", "eff_strength");
        alias(":velocidad:", "eff_speed");
        alias(":resistencia:", "eff_resistance");
        alias(":regeneracion:", "eff_regeneration");
        alias(":hamburguesa:", "food_burger");
        alias(":burger:", "food_burger");
        alias(":pizza:", "food_pizza");
        alias(":taco:", "food_taco");
        alias(":carne:", "food_steak");
        alias(":steak:", "food_steak");
        alias(":espada:", "combat_pvp_swords");
        alias(":escudo:", "combat_shield_defense");
        alias(":subasta:", "uicon_auction_gavel");
        alias(":mochila:", "uicon_backpack_storage");
        alias(":tienda:", "shop_cart_market");
        alias(":mercado:", "shop_market_stand");
        alias(":banco:", "shop_bank_vault");
        alias(":cmi:", "cmi_logo");
        alias(":sol_de_mayo:", "badge_sol_de_mayo");
        alias(":sol:", "badge_sol_de_mayo");
        alias(":escarapela:", "badge_escarapela");
        alias(":malvinas:", "badge_islas_malvinas");
        alias(":islas_malvinas:", "badge_islas_malvinas");
        alias(":escudo_nacional:", "badge_escudo_nacional");
        alias(":escudo_argentino:", "badge_escudo_nacional");
        alias(":gorro_frigio:", "badge_gorro_frigio");
        alias(":cabildo:", "badge_cabildo");
        alias(":obelisco:", "badge_obelisco");
        alias(":monumento_bandera:", "badge_monumento_bandera");
        alias(":tres_estrellas:", "badge_tres_estrellas");
        alias(":estrellas:", "badge_tres_estrellas");
        alias(":copa_del_mundo:", "badge_copa_del_mundo");
        alias(":copa:", "badge_copa_del_mundo");
        alias(":camiseta_argentina:", "badge_camiseta_argentina");
        alias(":camiseta:", "badge_camiseta_argentina");
        alias(":asado:", "badge_asado_criollo");
        alias(":asado_criollo:", "badge_asado_criollo");
        alias(":empanada:", "badge_empanada");
        alias(":dulce_de_leche:", "badge_dulce_de_leche");
        alias(":ddl:", "badge_dulce_de_leche");
        alias(":alfajor:", "badge_alfajor_marplatense");
        alias(":alfajor_marplatense:", "badge_alfajor_marplatense");
        alias(":alfajor_maicena:", "badge_alfajor_maicena");
        alias(":termo:", "badge_termo_mate");
        alias(":termo_mate:", "badge_termo_mate");
        alias(":stanley:", "badge_termo_mate");
        alias(":gaucho:", "badge_gaucho");
        alias(":bandoneon:", "badge_bandoneon");
        alias(":hornero:", "badge_hornero");
        alias(":ceibo:", "badge_flor_ceibo");
        alias(":flor_ceibo:", "badge_flor_ceibo");
        alias(":mapa_argentina:", "badge_mapa_argentina");
        alias(":mapa_arg:", "badge_mapa_argentina");
        alias(":a:", "letter_a");
        alias(":b:", "letter_b");
        alias(":c:", "letter_c");
        alias(":d:", "letter_d");
        alias(":e:", "letter_e");
        alias(":f:", "letter_f");
        alias(":g:", "letter_g");
        alias(":h:", "letter_h");
        alias(":i:", "letter_i");
        alias(":j:", "letter_j");
        alias(":k:", "letter_k");
        alias(":l:", "letter_l");
        alias(":m:", "letter_m");
        alias(":n:", "letter_n");
        alias(":o:", "letter_o");
        alias(":p:", "letter_p");
        alias(":q:", "letter_q");
        alias(":r:", "letter_r");
        alias(":s:", "letter_s");
        alias(":t:", "letter_t");
        alias(":u:", "letter_u");
        alias(":v:", "letter_v");
        alias(":w:", "letter_w");
        alias(":x:", "letter_x");
        alias(":y:", "letter_y");
        alias(":z:", "letter_z");
    }

    private static void initFavorites() {
        String[] favIds = {
            "flag_ar", "crown", "sword", "shield", "skull", "diamond", "star", "badge_owner", "badge_admin", "badge_mod", "badge_belgrano", "badge_sanmartin", "badge_mate", "badge_matero", "badge_sol_de_mayo", "badge_copa_del_mundo", "badge_asado_criollo", "cmi_logo", "uicon_pvp_swords", "uicon_auction_gavel", "uicon_backpack_storage", "shop_cash_register", "sui_trophy_cup", "social_discord"
        };
        for (String fid : favIds) {
            ServerEmoji fe = BY_ID.get(fid);
            if (fe != null) {
                QUICK_FAVORITES.add(fe);
            }
        }
    }

    public static boolean isBadgeUnicode(String u) {
        if (u == null || u.isEmpty()) return false;
        char c = u.charAt(0);
        return (c >= 0xE200 && c <= 0xE221) || c == 0xE302 || (c >= 0xE8B0 && c <= 0xE8C5);
    }

    public static boolean hasAnimatedContext(String input) {
        if (input == null) return false;
        String lower = input.toLowerCase();
        return lower.contains("<animated>") || lower.contains("<rainbow>") || lower.contains("<gold>")
                || lower.contains("<fire>") || lower.contains("<celestial>") || lower.contains("<ocean>")
                || lower.contains("<toxic>") || lower.contains("<sakura>") || lower.contains("<void>")
                || lower.contains("<silver>") || lower.contains("<frost>") || lower.contains("<lava>")
                || lower.contains("<matrix>") || lower.contains("<aurora>") || lower.contains("<sunset>")
                || lower.contains("<neon>") || lower.contains("<candy>") || lower.contains("<galaxy>")
                || lower.contains("<glitch>") || lower.contains("<blood>") || lower.contains("<emerald>")
                || lower.contains("<diamond>");
    }

    public static boolean hasAnyShortcode(String input) {
        if (input == null || input.isEmpty()) return false;
        int firstColon = input.indexOf(':');
        if (firstColon == -1) return false;
        int secondColon = input.indexOf(':', firstColon + 1);
        if (secondColon == -1) return false;
        String lower = input.toLowerCase();
        for (String code : BY_SHORTCODE.keySet()) {
            if (lower.contains(code)) return true;
        }
        return false;
    }

    public static String replaceShortcodes(String input) {
        if (input == null || input.isEmpty() || !input.contains(":")) return input;
        String res = input;
        boolean hasAnim = hasAnimatedContext(input);
        for (Map.Entry<String, ServerEmoji> entry : BY_SHORTCODE.entrySet()) {
            if (res.toLowerCase().contains(entry.getKey())) {
                ServerEmoji emoji = entry.getValue();
                String rep = emoji.unicode();
                if ("Rangos".equalsIgnoreCase(emoji.category())) {
                    // Rangos oficiales siempre 100% estáticos en Minecraft (forzar §f sin tags de animación)
                    rep = "&f" + rep;
                } else if (isBadgeUnicode(rep) && !hasAnim) {
                    rep = "&f" + rep;
                }
                res = res.replaceAll("(?i)" + java.util.regex.Pattern.quote(entry.getKey()), java.util.regex.Matcher.quoteReplacement(rep));
            }
        }
        return res;
    }

    public static List<ServerEmoji> filter(String category, String searchQuery) {
        List<ServerEmoji> res = new ArrayList<>();
        String q = (searchQuery != null) ? searchQuery.trim().toLowerCase() : "";
        for (ServerEmoji e : ALL_EMOJIS) {
            boolean matchesCat;
            if (category == null || category.equalsIgnoreCase("Todos") || category.isEmpty()) {
                matchesCat = true;
            } else if ("Combate".equalsIgnoreCase(category) || "Combate & General".equalsIgnoreCase(category)) {
                matchesCat = "Combate".equalsIgnoreCase(e.category())
                        || "Combate & General".equalsIgnoreCase(e.category());
            } else if ("Server UI".equalsIgnoreCase(category) || "UI Servidor".equalsIgnoreCase(category)) {
                matchesCat = "Server UI".equalsIgnoreCase(e.category())
                        || "UI".equalsIgnoreCase(e.category());
            } else if ("Útiles".equalsIgnoreCase(category) || "Utiles".equalsIgnoreCase(category)) {
                matchesCat = "Útiles".equalsIgnoreCase(e.category());
            } else if ("Mercado & Tienda".equalsIgnoreCase(category) || "Tienda".equalsIgnoreCase(category) || "Mercado".equalsIgnoreCase(category)) {
                matchesCat = "Mercado & Tienda".equalsIgnoreCase(e.category());
            } else if ("Items & Materiales".equalsIgnoreCase(category) || "Items".equalsIgnoreCase(category) || "Materiales".equalsIgnoreCase(category)) {
                matchesCat = "Items".equalsIgnoreCase(e.category())
                        || "Materiales".equalsIgnoreCase(e.category());
            } else if ("Pociones & Efectos".equalsIgnoreCase(category) || "Pociones".equalsIgnoreCase(category) || "Efectos".equalsIgnoreCase(category)) {
                matchesCat = "Pociones".equalsIgnoreCase(e.category())
                        || "Efectos".equalsIgnoreCase(e.category());
            } else if ("Capella & UI".equalsIgnoreCase(category) || "Capella".equalsIgnoreCase(category)) {
                matchesCat = "Capella".equalsIgnoreCase(e.category())
                        || "Logos".equalsIgnoreCase(e.category());
            } else if ("Patria".equalsIgnoreCase(category) || "Patria Argentina".equalsIgnoreCase(category)) {
                matchesCat = "Patria Argentina".equalsIgnoreCase(e.category());
            } else if ("Letras".equalsIgnoreCase(category)) {
                matchesCat = "Letras".equalsIgnoreCase(e.category());
            } else if ("Números".equalsIgnoreCase(category) || "Numeros".equalsIgnoreCase(category)) {
                matchesCat = "Números".equalsIgnoreCase(e.category());
            } else {
                matchesCat = e.category().equalsIgnoreCase(category);
            }
            if (!matchesCat) continue;

            if (q.isEmpty()
                    || e.displayName().toLowerCase().contains(q)
                    || e.id().toLowerCase().contains(q)
                    || e.shortcode().toLowerCase().contains(q)
                    || e.category().toLowerCase().contains(q)) {
                res.add(e);
            }
        }
        return res;
    }

    public static Identifier getTextureLocation(String id) {
        if (id == null) return null;
        ServerEmoji emoji = BY_ID.get(id.toLowerCase());
        return (emoji != null) ? emoji.texture() : null;
    }

    public static String resolveTexturePath(String id) {
        if (id == null) return null;
        ServerEmoji emoji = BY_ID.get(id.toLowerCase());
        return (emoji != null) ? emoji.texturePath() : null;
    }
}
