package com.jarhax.caliper;

import java.io.File;

import com.jarhax.caliper.commands.CommandCaliper;
import com.jarhax.caliper.debuggers.DebugEntitySpawns;
import com.jarhax.caliper.debuggers.DebugEventListeners;
import com.jarhax.caliper.debuggers.DebugIdUsage;
import com.jarhax.caliper.debuggers.DebugLoadtimes;
import com.jarhax.caliper.proxy.CommonProxy;

import net.darkhax.bookshelf.BookshelfRegistry;
import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Caliper.MODID, name = Caliper.NAME, acceptedMinecraftVersions = "1.12.1", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.2.462,)", certificateFingerprint = "@FINGERPRINT@")
public class Caliper {

    public static final String MODID = "caliper";
    public static final String NAME = "Caliper";
    public static final LoggingHelper LOG = new LoggingHelper("Caliper");
    public static final RegistryHelper helper = new RegistryHelper(MODID).setTab(new CreativeTabCaliper());

    @SidedProxy(clientSide = "com.jarhax.caliper.proxy.ClientProxy", serverSide = "com.jarhax.caliper.proxy.ServerProxy")
    public static CommonProxy proxy;

    public Caliper () {

        ((org.apache.logging.log4j.core.Logger) FMLLog.log).addFilter(new DebugLoadtimes());
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        new File("logs/caliper/").mkdirs();
        BookshelfRegistry.addCommand(new CommandCaliper());
        proxy.preInit(event);
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

        proxy.init(event);
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        proxy.postInit(event);

        DebugEntitySpawns.debug();
    }

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        DebugLoadtimes.onLoadingComplete();
        DebugEventListeners.printAllListeners();
        DebugIdUsage.onLoadingComplete();
    }

    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}