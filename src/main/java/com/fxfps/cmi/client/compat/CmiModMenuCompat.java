package com.fxfps.cmi.client.compat;

import com.fxfps.cmi.client.gui.CmiSettingsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class CmiModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CmiSettingsScreen::new;
    }
}
