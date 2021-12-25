package dev.haedhutner.core.utils;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An abstract utility class for creating quick and simple configuration classes using an object
 * mapper.
 */
public abstract class PluginConfig {

    protected ObjectMapper<PluginConfig>.BoundInstance configMapper;

    protected ConfigurationLoader<CommentedConfigurationNode> loader;

    protected ConfigurationOptions options;

    protected Path filePath;

    /**
     * This constructor will load all serializable fields ( the ones marked with {@link Setting} and
     * {@link ConfigSerializable}, then attempt to create a HOCON file in the given directory with the
     * given name and a {@link HoconConfigurationLoader} from that file.
     *
     * @param filePath the path to the config file
     */
    protected PluginConfig(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Override this method to provide custom type serializers for the ConfigurationLoader
     *
     * @return the configuration options for the loader ot use
     */
    protected ConfigurationOptions getOptions() {
        return ConfigurationOptions.defaults();
    }

    /**
     * Populate the object mapper with the contents of the config file. This will override any default
     * values.
     */
    public SimpleOperationResult load() {
        try {
            this.configMapper.populate(this.loader.load(options));
        } catch (ObjectMappingException | IOException e) {
            return new SimpleOperationResult(false, e.getMessage(), e);
        }

        return new SimpleOperationResult(true, null, null);
    }

    /**
     * Initialize the config. If the config file had already existed, this will load values from the
     * config file, overriding the defaults. If it did not, this will save to the file with the
     * default values provided.
     */
    public SimpleOperationResult init() {
        try {
            this.configMapper = ObjectMapper.forObject(this);
            this.options = getOptions();

            this.loader = HoconConfigurationLoader.builder()
                    .setDefaultOptions(options)
                    .setPath(filePath)
                    .build();
        } catch (ObjectMappingException e) {
            return new SimpleOperationResult(false, e.getMessage(), e);
        }

        try {
            if (filePath == null) {
                throw new IOException("Configuration file path has not been provided");
            }

            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);

            SimpleConfigurationNode out = SimpleConfigurationNode.root();
            this.configMapper.serialize(out);
            this.loader.save(out);
        } catch (FileAlreadyExistsException e) {
            return this.load();
        } catch (ObjectMappingException | IOException e) {
            return new SimpleOperationResult(false, e.getMessage(), e);
        }

        return new SimpleOperationResult(true, null, null);
    }
}
