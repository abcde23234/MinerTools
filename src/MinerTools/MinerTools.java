package MinerTools;

import MinerTools.core.*;
import MinerTools.ui.*;
import arc.*;
import arc.scene.ui.layout.*;
import io.mnemotechnician.autoupdater.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;

import static MinerTools.MinerFuncs.*;
import static MinerTools.MinerVars.*;
import static arc.Core.*;
import static mindustry.Vars.*;

public class MinerTools extends Mod{

    public MinerTools(){
        enableConsole = true;

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Updater.checkUpdates(this);

            MinerVars.init();

            initUI();
            betterUiscaleSetting();
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Core.app.post(() -> Core.app.post(() -> Core.app.post(() -> Core.app.post(() -> Core.app.post(PowerInfo::init)))));

            showBannedInfo();
        });

        Events.run(Trigger.update, () -> update());

        Drawer.setEvents();
    }

    public static void initUI(){
        ui.hudGroup.fill(t -> {
            t.top().right().name = "miner-tools";
            t.visible(() -> ui.hudfrag.shown && !ui.minimapfrag.shown());

            t.add(new MinerToolsTable());

            Table minimap = ui.hudGroup.find("minimap/position");
            Table overlaymarker = ui.hudGroup.find("overlaymarker");
            t.update(() -> {
                if(t.getPrefWidth() + overlaymarker.getPrefWidth() + minimap.getPrefWidth() > Core.scene.getWidth()){
                    t.translation.x = 0;
                    t.translation.y = -minimap.getPrefHeight();
                }else{
                    t.translation.x = -minimap.getPrefWidth();
                    t.translation.y = 0;
                }
            });
        });
    }

    public static void betterUiscaleSetting(){
        int[] lastUiScale = {settings.getInt("uiscale", 100)};
        int index = ui.settings.graphics.getSettings().indexOf(setting -> setting.name.equals("uiscale"));

        settings.put("uiscale", settings.getInt("_uiscale", 100));

        if(index != -1){
            ui.settings.graphics.getSettings().set(index, new SliderSetting("uiscale", 100, 25, 300, 1, s -> {
                //if the user changed their UI scale, but then put it back, don't consider it 'changed'
                settings.put("uiscalechanged", s != lastUiScale[0]);
                settings.put("_uiscale", s);
                return s + "%";
            }));
        }
        ui.settings.graphics.rebuild();

        Scl.setProduct(settings.getInt("_uiscale", 100) / 100f);
    }

    public static void update(){
        if((desktop && input.alt()) || (mobile && enableUpdateConveyor)){
            tryUpdateConveyor();
        }
    }
}