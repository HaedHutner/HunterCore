package dev.haedhutner.core.module;

public interface Module {

    String getId();

    String getName();

    String getDescription();

    boolean isEnabled();

    boolean isStarted();

    boolean isShutdown();

    ModuleStartupResult start();

    ModuleShutdownResult stop();
}
